package com.example.administrator.share.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.administrator.share.R;
import com.example.administrator.share.entity.CommentListItem;
import com.example.administrator.share.util.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

public class CircleDetailCommentsAdapter extends RecyclerView.Adapter<CircleDetailCommentsAdapter.ViewHolder> {

    private Context mContext;
    private List<CommentListItem> mList;
    private LayoutInflater inflater;
    OnCommentButtonClickListner onCommentButtonClickListner;
    OnCommentDeleteClickListner onCommentDeleteClickListner;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nickname;
        TextView commentTime;
        TextView replyUser;
        TextView content;
        ImageView addComment;
        LinearLayout replyContainer;
        ImageView faceIv;
        RelativeLayout commentRl;
        ViewHolder(View view) {
            super(view);
            nickname = view.findViewById(R.id.news_detail_comment_nickname);
            commentTime = view.findViewById(R.id.news_detail_comment_time);
            replyUser = view.findViewById(R.id.news_detail_comment_reply_user);
            replyContainer = view.findViewById(R.id.news_detail_reply_info);
            content = view.findViewById(R.id.news_detail_commment_content);
            addComment = view.findViewById(R.id.news_detail_comment_add_reply);
            faceIv = view.findViewById(R.id.iv_comment_face);
            commentRl = view.findViewById(R.id.rl_detail_comment);
        }
    }

    public CircleDetailCommentsAdapter(Context mContext, List<CommentListItem> mList) {
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
                doReplyCommentButtonClickAction(mList.get(position).getNickname());
            }
        });
        holder.commentRl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final int position = holder.getAdapterPosition();
                if(mList.get(position).getNickname().equals(Constants.USER.getNickname())){
                    //弹出的“删除评论”的Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setItems(new String[]{mContext.getResources().getString(R.string.delete_comment)}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteCommentById(mList.get(position).getCommentId());
                        }
                    });
                    builder.show();
                }
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentListItem commentListItem = mList.get(position);
        holder.nickname.setText(commentListItem.getNickname());
        if (TextUtils.isEmpty(commentListItem.getReplyUser())) {
            holder.replyContainer.setVisibility(View.INVISIBLE);
        } else {
            holder.replyContainer.setVisibility(View.VISIBLE);
            holder.replyUser.setText(commentListItem.getReplyUser());
        }
        holder.commentTime.setText(commentListItem.getCommentTime());
        holder.content.setText(commentListItem.getComment());
        getFaceImage(commentListItem.getFace(),holder);
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

    public void doReplyCommentButtonClickAction(String replyUser) {
        if (onCommentButtonClickListner != null) {
            onCommentButtonClickListner.OnCommentButtonClicked(replyUser);
        }
    }

    public interface OnCommentDeleteClickListner {
        void OnCommentDeleteClicked();
    }

    public void setOnCommentDeleteClickListner(OnCommentDeleteClickListner onCommentDeleteClickListner) {
        this.onCommentDeleteClickListner = onCommentDeleteClickListner;
    }

    public void doDeleteButtonClickAction() {
        if (onCommentDeleteClickListner != null) {
            onCommentDeleteClickListner.OnCommentDeleteClicked();
        }
    }

    private void getFaceImage(final String face, final ViewHolder viewHolder) {
        Uri uri = Uri.parse(Constants.BASE_URL+"download/getImage?face="+face);
        Glide.with(mContext).load(uri).into(viewHolder.faceIv);
    }

    private void deleteCommentById(final int commentId) {
        new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "Message?method=deleteComment";
                OkHttpUtils
                        .get()//
                        .url(url)//
                        .addParams("commentId", String.valueOf(commentId))
                        .build()//
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                            }
                            @Override
                            public void onResponse(String response, int id) {
                                if(response.equals("success")){
                                    doDeleteButtonClickAction();
                                    Toast.makeText(mContext,"删除成功",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(mContext,"删除失败，请稍后再试",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                return 0;
            }
        }.execute();
    }
}
