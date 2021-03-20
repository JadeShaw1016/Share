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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.entity.NewsListItem;
import com.example.administrator.share.util.Constants;
import com.startsmake.mainnavigatetabbar.widget.BadgeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<NewsListItem> mList;
    private OnCommentButtonClickListner onCommentButtonClickListner;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nicknameTv;
        TextView commentTv;
        TextView badgeTv;
        ImageView faceIv;
        ImageView imageIv;
        TextView commentTimeTv;
        BadgeView badge;
        RelativeLayout messageLl;
        public ViewHolder(View view) {
            super(view);
            nicknameTv = view.findViewById(R.id.tv_comment_nickname);
            commentTv = view.findViewById(R.id.tv_comment_content);
            badgeTv = view.findViewById(R.id.tv_badge);
            faceIv = view.findViewById(R.id.iv_comment_face);
            imageIv = view.findViewById(R.id.iv_image);
            commentTimeTv = view.findViewById(R.id.tv_comment_time);
            messageLl = view.findViewById(R.id.ll_message);
        }
    }

    public CommentListAdapter(Context mContext, List<NewsListItem> mList) {
        this.mContext=mContext;
        this.mList = mList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = inflater.inflate(R.layout.item_comment, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.messageLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                doButtonClickAction(mList.get(position));
                NewsListItem newsListItem = mList.get(position);
                final int commentId = newsListItem.getCommentId();
                if(newsListItem.getIsVisited() == 0){
                    new AsyncTask<Void, Void, Integer>(){
                        @Override
                        protected Integer doInBackground(Void... voids) {
                            String url = Constants.BASE_URL + "Message?method=updateCommentStatus";
                            OkHttpUtils
                                    .post()
                                    .url(url)
                                    .addParams("commentId", String.valueOf(commentId))
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                        }
                                    });
                            return 0;
                        }
                    }.execute();
                }
            }
        });
        holder.imageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                NewsListItem newsListItem = mList.get(position);
                Intent intent = new Intent(mContext, CircleDetailActivity.class);
                intent.putExtra("newsId", newsListItem.getNewsId());
                intent.putExtra("be_focused_personId", mList.get(position).getUserId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final NewsListItem detail = mList.get(position);
        holder.nicknameTv.setText(detail.getNickname()+"评论了你");
        holder.commentTv.setText(detail.getComment());
        String time = detail.getCommentTime();
        try {
            if(isOldTime(time)){
                //设置为年月日
                holder.commentTimeTv.setText(time.substring(0,11));
            }else{
                //设置为时分
                holder.commentTimeTv.setText("今天 "+time.substring(11,16));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "Download?method=getUserFaceImage";
                OkHttpUtils
                        .get()//
                        .url(url)//
                        .addParams("face", detail.getFace())
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
        new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "Download?method=getNewsImage";
                OkHttpUtils
                        .get()//
                        .url(url)//
                        .addParams("imageName", detail.getImage())
                        .build()//
                        .execute(new BitmapCallback() {
                            @Override
                            public void onError(Call call, Exception e, int i) {
                            }
                            @Override
                            public void onResponse(Bitmap bitmap, int i) {
                                holder.imageIv.setImageBitmap(bitmap);
                            }
                        });
                return 0;
            }
        }.execute();
        if(detail.getIsVisited()==0){
            holder.badge = new BadgeView(mContext);
            holder.badge.setTargetView(holder.badgeTv);
            holder.badge.setBadgeCount(1);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private boolean isOldTime(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = sdf.format(new Date());
        String standardTime = nowTime.substring(0,11)+"00:00:00";
        Date date1 = sdf.parse(standardTime);
        Date date2 = sdf.parse(time);
        return date1.getTime() > date2.getTime();
    }

    public void updateList(List<NewsListItem> newDatas) {
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        notifyDataSetChanged();
    }

    public interface OnCommentButtonClickListner {
        void OnCommentButtonClicked(NewsListItem newsListItem);
    }

    public void setOnCommentButtonClickListner(OnCommentButtonClickListner onCommentButtonClickListner) {
        this.onCommentButtonClickListner = onCommentButtonClickListner;
    }

    public void doButtonClickAction(NewsListItem newsListItem) {
        if (onCommentButtonClickListner != null) {
            onCommentButtonClickListner.OnCommentButtonClicked(newsListItem);
        }
    }
}
