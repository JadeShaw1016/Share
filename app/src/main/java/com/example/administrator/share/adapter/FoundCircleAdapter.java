package com.example.administrator.share.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.entity.CircleListForFound;
import com.example.administrator.share.util.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.util.List;

import okhttp3.Call;

public class FoundCircleAdapter extends RecyclerView.Adapter<FoundCircleAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<CircleListForFound> mList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bgIv;
        TextView titleTv;
        TextView usernameTv;
        public ViewHolder(View view) {
            super(view);
            bgIv = view.findViewById(R.id.found_list_icon);
            titleTv = view.findViewById(R.id.found_list_item_title);
            usernameTv = view.findViewById(R.id.found_list_item_username);
        }
    }

    public FoundCircleAdapter(Context mContext, List<CircleListForFound> mList) {
        this.mContext=mContext;
        mInflater=LayoutInflater.from(mContext);
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = mInflater.inflate(R.layout.item_circle, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.bgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(mContext,CircleDetailActivity.class);
                intent.putExtra("newsId", mList.get(position).getNewsId());
                intent.putExtra("be_focused_personId", mList.get(position).getUserId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        CircleListForFound news = mList.get(position);
        final String imageName = news.getImage();
        holder.titleTv.setText(news.getTitle());
        holder.usernameTv.setText(news.getUsername());

       new AsyncTask<Void, Void, Integer>(){

           @Override
           protected Integer doInBackground(Void... voids) {
               String url = Constants.BASE_URL + "Download?method=getNewsImage";
               OkHttpUtils
                       .get()//
                       .url(url)//
                       .addParams("imageName", imageName)
                       .build()//
                       .execute(new BitmapCallback() {
                           @Override
                           public void onError(Call call, Exception e, int i) {
                           }
                           @Override
                           public void onResponse(Bitmap bitmap, int i) {
                               holder.bgIv.setImageBitmap(bitmap);
                           }
                       });
               return 0;
           }

       }.execute();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


}
