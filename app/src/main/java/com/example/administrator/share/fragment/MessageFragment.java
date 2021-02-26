package com.example.administrator.share.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.CommentActivity;
import com.example.administrator.share.activity.FavorActivity;
import com.example.administrator.share.util.Constants;
import com.startsmake.mainnavigatetabbar.widget.BadgeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class MessageFragment extends Fragment implements View.OnClickListener{

    private Context mContext;
    private ImageView moreIv;
    private LinearLayout commentLl;
    private LinearLayout favorLl;
    private BadgeView commentBadge;
    private BadgeView favorBadge;
    private TextView commentBadgeTv;
    private TextView favorBadgeTv;
    private TextView titleText;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_message,container,false);
        findViewById(view);
        initView();
        return view;
    }

    private void findViewById(View view){
        titleText = view.findViewById(R.id.titleText);
        moreIv = view.findViewById(R.id.iv_more);
        commentLl = view.findViewById(R.id.ll_message_comment);
        favorLl = view.findViewById(R.id.ll_message_favor);
        commentBadgeTv = view.findViewById(R.id.tv_comment_badge);
        favorBadgeTv = view.findViewById(R.id.tv_favor_badge);
    }

    private void initView(){
        mContext = getActivity();
        titleText.setText("通知");
        moreIv.setVisibility(View.VISIBLE);
        moreIv.setOnClickListener(this);
        commentLl.setOnClickListener(this);
        favorLl.setOnClickListener(this);
        commentBadge = new BadgeView(mContext);
        commentBadge.setTargetView(commentBadgeTv);
        favorBadge = new BadgeView(mContext);
        favorBadge.setTargetView(favorBadgeTv);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.ll_message_comment:
                intent = new Intent(getActivity(), CommentActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_message_favor:
                intent = new Intent(getActivity(), FavorActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_more:
                break;
        }
    }

    private void findCommentStatus() {
        String url = Constants.BASE_URL + "Message?method=findCommentStatus";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("authorName",Constants.USER.getUsername())
                .build()
                .execute(new StringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        commentBadge.setBadgeCount(Integer.parseInt(response));
                    }
                });
    }

    private void findFavorStatus() {
        String url = Constants.BASE_URL + "Message?method=findFavorStatus";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("authorId",Constants.USER.getUserId()+"")
                .build()
                .execute(new StringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        favorBadge.setBadgeCount(Integer.parseInt(response));
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        findCommentStatus();
        findFavorStatus();
    }
}
