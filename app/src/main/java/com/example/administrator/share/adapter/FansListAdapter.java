package com.example.administrator.share.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.entity.FansListItem;
import com.example.administrator.share.util.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

public class FansListAdapter extends RecyclerView.Adapter<FansListAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<FansListItem> mList;
    private int flag;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTv;
        TextView descriptionTv;
        TextView stateTv;
        ImageView stateIv;
        ImageView faceIv;
        public ViewHolder(View view) {
            super(view);
            usernameTv = view.findViewById(R.id.tv_username);
            descriptionTv = view.findViewById(R.id.tv_description);
            stateTv =view.findViewById(R.id.tv_state);
            stateIv = view.findViewById(R.id.iv_state);
            faceIv = view.findViewById(R.id.iv_fans_focus);
        }
    }

    public FansListAdapter(Context mContext, List<FansListItem> mList,int flag) {
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
        FansListItem detail = mList.get(position);
        holder.usernameTv.setText(detail.getUserName());
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
                        .addParams("userId", Constants.USER.getUserId() + "")
                        .addParams("fansId",fansId+"")
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
        new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "Download?method=getUserFaceImage";
                OkHttpUtils
                        .get()//
                        .url(url)//
                        .addParams("face", imageName)
                        .build()//
                        .execute(new BitmapCallback() {
                            @Override
                            public void onError(Call call, Exception e, int i) {
                            }
                            @Override
                            public void onResponse(Bitmap bitmap, int i) {
                                holder.faceIv.setImageBitmap(bitmap);
                            }
                        });
                return 0;
            }
        }.execute();
    }


}
