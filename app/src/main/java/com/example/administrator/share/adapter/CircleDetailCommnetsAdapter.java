package com.example.administrator.share.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class CircleDetailCommnetsAdapter extends BaseAdapter {


    private List<Comment> mList;

    private LayoutInflater inflater;

    OnCommentButtonClickListner onCommentButtonClickListner;

    public CircleDetailCommnetsAdapter(Context mContext, List<Comment> mList) {
        this.mList = mList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_circle_detail_comment, null);
            viewholder = new ViewHolder();
            viewholder.username = (TextView) convertView.findViewById(R.id.news_detail_comment_username);
            viewholder.commentTime = (TextView) convertView.findViewById(R.id.news_detail_comment_time);
            viewholder.replyUser = (TextView) convertView.findViewById(R.id.news_detail_comment_reply_user);
            viewholder.replyContainer = (LinearLayout) convertView.findViewById(R.id.news_detail_reply_info);
            viewholder.content = (TextView) convertView.findViewById(R.id.news_detail_commment_content);
            viewholder.addComment = (ImageView) convertView.findViewById(R.id.news_detail_comment_add_reply);
            viewholder.faceIv = convertView.findViewById(R.id.iv_comment_face);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }
        Comment comment = mList.get(position);
        viewholder.username.setText(comment.getUsername());

        if (TextUtils.isEmpty(comment.getReplyUser())) {
            viewholder.replyContainer.setVisibility(View.INVISIBLE);
        } else {
            viewholder.replyContainer.setVisibility(View.VISIBLE);
            viewholder.replyUser.setText(comment.getReplyUser());
        }
        viewholder.commentTime.setText(comment.getCommentTime());
        viewholder.content.setText(comment.getComment());
        viewholder.addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doButtonClickAction(mList.get(position).getUsername());
            }
        });
        getFaceImage(comment.getFace(),viewholder);
        return convertView;
    }

    private static class ViewHolder {
        private TextView username;
        private TextView commentTime;
        private TextView replyUser;
        private TextView content;
        private ImageView addComment;
        private LinearLayout replyContainer;
        private ImageView faceIv;
    }

public interface OnCommentButtonClickListner {
    public void OnCommentButtonClicked(String replyUser);
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
