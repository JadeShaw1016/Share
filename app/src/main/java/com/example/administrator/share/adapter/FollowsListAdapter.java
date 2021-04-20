package com.example.administrator.share.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.share.R;
import com.example.administrator.share.entity.FollowsListItem;
import com.example.administrator.share.util.Constants;
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
        public ViewHolder(View view) {
            super(view);
            nicknameTv = view.findViewById(R.id.tv_fans_focus_nickname);
            signatureTv = view.findViewById(R.id.tv_signature);
            stateTv =view.findViewById(R.id.tv_state);
            stateIv = view.findViewById(R.id.iv_state);
            faceIv = view.findViewById(R.id.iv_fans_focus);
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

    private void getImage(final String imageName, final ViewHolder holder) {
        Uri uri = Uri.parse(Constants.BASE_URL+"Download?method=getUserFaceImage&face="+imageName);
        Glide.with(mContext).load(uri).into(holder.faceIv);
    }

    public void updateList(List<FollowsListItem> newDatas) {
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        notifyDataSetChanged();
    }
}
