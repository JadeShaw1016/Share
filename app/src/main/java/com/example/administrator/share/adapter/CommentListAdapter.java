package com.example.administrator.share.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.administrator.share.R;
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.entity.CommonListItem;
import com.example.administrator.share.util.Constants;
import com.startsmake.mainnavigatetabbar.widget.BadgeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<CommonListItem> mList;
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

    public CommentListAdapter(Context mContext, List<CommonListItem> mList) {
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
                CommonListItem commonListItem = mList.get(position);
                final int commentId = commonListItem.getCommentId();
                if(commonListItem.getIsVisited() == 0){
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
                CommonListItem commonListItem = mList.get(position);
                Intent intent = new Intent(mContext, CircleDetailActivity.class);
                intent.putExtra("newsId", commonListItem.getNewsId());
                intent.putExtra("authorId", mList.get(position).getUserId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final CommonListItem detail = mList.get(position);
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
        Uri uri = Uri.parse(Constants.BASE_URL+"download/getImage?face="+detail.getFace());
        Glide.with(mContext).load(uri).into(((ViewHolder)holder).faceIv);
        uri = Uri.parse(Constants.BASE_URL+"download/getImage?imageName="+detail.getImage());
        Glide.with(mContext).load(uri).into(((ViewHolder)holder).imageIv);
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

    public void updateList(List<CommonListItem> newDatas) {
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        notifyDataSetChanged();
    }

    public interface OnCommentButtonClickListner {
        void OnCommentButtonClicked(CommonListItem commonListItem);
    }

    public void setOnCommentButtonClickListner(OnCommentButtonClickListner onCommentButtonClickListner) {
        this.onCommentButtonClickListner = onCommentButtonClickListner;
    }

    public void doButtonClickAction(CommonListItem commonListItem) {
        if (onCommentButtonClickListner != null) {
            onCommentButtonClickListner.OnCommentButtonClicked(commonListItem);
        }
    }
}
