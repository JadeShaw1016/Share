package com.example.administrator.share.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.administrator.share.R;
import com.example.administrator.share.activity.PersonalHomepageActivity;
import com.example.administrator.share.dialog.CustomDialog;
import com.example.administrator.share.entity.FollowsListItem;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

public class FollowsListAdapter extends RecyclerView.Adapter<FollowsListAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<FollowsListItem> mList;
    private int flag;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nicknameTv;
        TextView signatureTv;
        TextView stateTv;
        ImageView stateIv;
        ImageView faceIv;
        LinearLayout stateLl;
        public ViewHolder(View view) {
            super(view);
            nicknameTv = view.findViewById(R.id.tv_fans_focus_nickname);
            signatureTv = view.findViewById(R.id.tv_signature);
            stateTv =view.findViewById(R.id.tv_state);
            stateIv = view.findViewById(R.id.iv_state);
            faceIv = view.findViewById(R.id.iv_fans_focus);
            stateLl = view.findViewById(R.id.ll_item_fans_focus);
        }
    }

    public FollowsListAdapter(Context mContext, List<FollowsListItem> mList, int flag) {
        this.mList = mList;
        this.inflater = LayoutInflater.from(mContext);
        this.flag = flag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = inflater.inflate(R.layout.item_fans_focus, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.faceIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(mContext, PersonalHomepageActivity.class);
                intent.putExtra("userId",mList.get(position).getUserId());
                intent.putExtra("nickname",mList.get(position).getNickName());
                intent.putExtra("face",mList.get(position).getFace());
                intent.putExtra("signature",mList.get(position).getSignature());
                mContext.startActivity(intent);
            }
        });
        holder.stateLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = holder.getAdapterPosition();
                switch (holder.stateTv.getText().toString()){
                    case "\u0020\u0020已关注\u0020\u0020":
                    case "互相关注":
                        final CustomDialog customDialog = new CustomDialog(mContext);
                        customDialog.setTitle("提示").setMessage("不再关注此人？")
                                .setCancel("取消", new CustomDialog.IOnCancelListener() {
                                    @Override
                                    public void onCancel(CustomDialog dialog) {
                                        customDialog.dismiss();
                                    }
                                }).
                                setConfirm("不再关注", new CustomDialog.IOnConfirmListener() {
                                    @Override
                                    public void onConfirm(CustomDialog dialog) {
                                        addFocus(mList.get(position).getUserId());
                                        holder.stateIv.setImageResource(R.drawable.icon_add_focus);
                                        holder.stateTv.setText("添加关注");
                                    }
                                }).show();
                        break;
                    case "添加关注":
                        addFocus(mList.get(position).getUserId());
                        holder.stateIv.setImageResource(R.drawable.icon_focus_eachother);
                        holder.stateTv.setText("互相关注");
                        break;
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FollowsListItem detail = mList.get(position);
        holder.nicknameTv.setText(detail.getNickName());
        if(detail.getSignature() != null){
            holder.signatureTv.setText(detail.getSignature());
        }
        if(flag == 0){
            holder.stateIv.setImageResource(R.drawable.icon_has_focused);
            holder.stateTv.setText("\u0020\u0020已关注\u0020\u0020");
            isFocusedEachOther(detail.getUserId(),holder);
        }else{
            holder.stateIv.setImageResource(R.drawable.icon_add_focus);
            holder.stateTv.setText("添加关注");
            isFocusedEachOther(detail.getFansId(),holder);
        }
        getImage(detail.getFace(),holder);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void getImage(final String imageName, final ViewHolder holder) {
        Uri uri = Uri.parse(Constants.BASE_URL+"download/getImage?face="+imageName);
        Glide.with(mContext).load(uri).into(holder.faceIv);
    }

    public void updateList(List<FollowsListItem> newDatas) {
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        notifyDataSetChanged();
    }


    private void isFocusedEachOther(final int fansId, final ViewHolder viewHolder) {
        new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "Follows?method=isFocusedEachOther";
                OkHttpUtils
                        .post()
                        .url(url)
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .addParams("fansId",String.valueOf(fansId))
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                            }
                            @Override
                            public void onResponse(String response, int id) {
                                if(response.equals("true")){
                                    viewHolder.stateIv.setImageResource(R.drawable.icon_focus_eachother);
                                    viewHolder.stateTv.setText("互相关注");
                                }
                            }
                        });
                return 0;
            }
        }.execute();
    }

    private void addFocus(final int userId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "Follows?method=addFocus";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("fansId", String.valueOf(Constants.USER.getUserId()))
                        .addParams("userId",String.valueOf(userId))
                        .addParams("followTime", Utils.getCurrentDatetime())
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 1:
                    Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(mContext, "What?", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            Toast.makeText(mContext, "网络链接出错!", Toast.LENGTH_SHORT).show();
        }
    }
}
