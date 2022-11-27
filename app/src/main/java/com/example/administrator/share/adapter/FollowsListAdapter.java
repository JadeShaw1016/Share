package com.example.administrator.share.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;

public class FollowsListAdapter extends RecyclerView.Adapter<FollowsListAdapter.ViewHolder> {

    private Context mContext;
    private final LayoutInflater inflater;
    private final List<FollowsListItem> mList;
    //flag区分关注列表和粉丝列表，0表示关注列表，1表示粉丝列表
    private final int flag;

    private static final ThreadPoolExecutor THREADPOOL = new ThreadPoolExecutor(3, 4, 3,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3),
            new ThreadPoolExecutor.DiscardOldestPolicy());

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
            stateTv = view.findViewById(R.id.tv_state);
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
                intent.putExtra("userId", mList.get(position).getUserId());
                intent.putExtra("nickname", mList.get(position).getNickName());
                intent.putExtra("face", mList.get(position).getFace());
                intent.putExtra("signature", mList.get(position).getSignature());
                mContext.startActivity(intent);
            }
        });
        holder.stateLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = holder.getAdapterPosition();
                switch (holder.stateTv.getText().toString()) {
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
                                        addFocus(mList.get(position).getUserId(), holder, 2);
                                    }
                                }).show();
                        break;
                    case "添加关注":
                        if (Constants.USER.getUserId() == mList.get(position).getUserId()) {
                            Toast.makeText(mContext, "不能关注自己哦！", Toast.LENGTH_SHORT).show();
                        } else {
                            addFocus(mList.get(position).getUserId(), holder, 1);
                        }
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
        if (detail.getSignature() != null) {
            holder.signatureTv.setText(detail.getSignature());
        }
        if (flag == 0) {
            holder.stateIv.setImageResource(R.drawable.icon_has_focused);
            holder.stateTv.setText("\u0020\u0020已关注\u0020\u0020");
            isFocused(detail.getUserId(), holder);
            isFocusedEachOther(detail.getUserId(), holder);
        } else {
            holder.stateIv.setImageResource(R.drawable.icon_add_focus);
            holder.stateTv.setText("添加关注");
            isFocusedEachOther(detail.getFansId(), holder);
        }
        getImage(detail.getFace(), holder);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void getImage(final String imageName, final ViewHolder holder) {
        Uri uri = Uri.parse(Constants.BASE_URL + "download/getImage?face=" + imageName);
        Glide.with(mContext).load(uri).into(holder.faceIv);
    }

    public void updateList(List<FollowsListItem> newDatas) {
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        notifyDataSetChanged();
    }

    private void addFocus(final int userId, final ViewHolder holder, final int id) {
        THREADPOOL.execute(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "follows/addFocus";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(id)
                        .addParams("fansId", String.valueOf(Constants.USER.getUserId()))
                        .addParams("userId", String.valueOf(userId))
                        .build()
                        .execute(new MyStringCallback(holder));
            }
        });
    }

    private void isFocusedEachOther(final int fansId, final ViewHolder holder) {
        THREADPOOL.execute(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "follows/isFocusedEachOther";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(3)
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .addParams("fansId", String.valueOf(fansId))
                        .build()
                        .execute(new MyStringCallback(holder));
            }
        });
    }

    private void isFocused(final int fansId, final ViewHolder holder) {
        THREADPOOL.execute(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "follows/isFocused";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(4)
                        .addParams("fansId", String.valueOf(Constants.USER.getUserId()))
                        .addParams("userId", String.valueOf(fansId))
                        .build()
                        .execute(new MyStringCallback(holder));
            }
        });
    }

    public class MyStringCallback extends StringCallback {
        ViewHolder holder;

        public MyStringCallback(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 1:
                    if (response.equals("关注成功")) {
                        holder.stateIv.setImageResource(R.drawable.icon_focus_eachother);
                        holder.stateTv.setText("互相关注");
                    }
                    Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    if (response.equals("取消关注")) {
                        holder.stateIv.setImageResource(R.drawable.icon_add_focus);
                        holder.stateTv.setText("添加关注");
                    }
                    Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    if (response.equals("true")) {
                        holder.stateIv.setImageResource(R.drawable.icon_focus_eachother);
                        holder.stateTv.setText("互相关注");
                    }
                    break;
                case 4:
                    if (!response.equals("已关注")) {
                        holder.stateIv.setImageResource(R.drawable.icon_add_focus);
                        holder.stateTv.setText("添加关注");
                    }
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
