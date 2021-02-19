package com.example.administrator.share.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.entity.Comment;
import com.example.administrator.share.util.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.util.List;

import okhttp3.Call;

/**
 * Created by djzhao on 17/05/02.
 */

public class CircleDetailCommnetsAdapter extends RecyclerView.Adapter<CircleDetailCommnetsAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mList;
    private LayoutInflater inflater;
    OnCommentButtonClickListner onCommentButtonClickListner;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView commentTime;
        TextView replyUser;
        TextView content;
        ImageView addComment;
        LinearLayout replyContainer;
        ImageView faceIv;
        ViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.news_detail_comment_username);
            commentTime = view.findViewById(R.id.news_detail_comment_time);
            replyUser = view.findViewById(R.id.news_detail_comment_reply_user);
            replyContainer = view.findViewById(R.id.news_detail_reply_info);
            content = view.findViewById(R.id.news_detail_commment_content);
            addComment = view.findViewById(R.id.news_detail_comment_add_reply);
            faceIv = view.findViewById(R.id.iv_comment_face);
        }
    }

    public CircleDetailCommnetsAdapter(Context mContext, List<Comment> mList) {
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
        View view = inflater.inflate(R.layout.item_circle_detail_comment, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                doButtonClickAction(mList.get(position).getUsername());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = mList.get(position);
        holder.username.setText(comment.getUsername());
        if (TextUtils.isEmpty(comment.getReplyUser())) {
            holder.replyContainer.setVisibility(View.INVISIBLE);
        } else {
            holder.replyContainer.setVisibility(View.VISIBLE);
            holder.replyUser.setText(comment.getReplyUser());
        }
        holder.commentTime.setText(comment.getCommentTime());
        holder.content.setText(comment.getComment());
        getFaceImage(comment.getFace(),holder);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


public interface OnCommentButtonClickListner {
    void OnCommentButtonClicked(String replyUser);
}

public void setOnCommentButtonClickListner(OnCommentButtonClickListner onCommentButtonClickListner) {
    this.onCommentButtonClickListner = onCommentButtonClickListner;
}

public void doButtonClickAction(String replyUser) {
    if (onCommentButtonClickListner != null) {
        onCommentButtonClickListner.OnCommentButtonClicked(replyUser);
    }
}

    private void getFaceImage(final String face, final ViewHolder viewHolder) {
        new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "Download?method=getUserFaceImage";
                OkHttpUtils
                        .get()//
                        .url(url)//
                        .addParams("face", face)
                        .build()//
                        .execute(new BitmapCallback() {
                            @Override
                            public void onError(Call call, Exception e, int i) {
                            }
                            @Override
                            public void onResponse(Bitmap bitmap, int i) {
                                viewHolder.faceIv.setImageBitmap(bitmap);
                            }
                        });
                return 0;
            }
        }.execute();
    }
}
