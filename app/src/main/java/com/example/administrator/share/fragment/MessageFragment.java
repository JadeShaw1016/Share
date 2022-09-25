package com.example.administrator.share.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.CommentActivity;
import com.example.administrator.share.activity.FavorFansActivity;
import com.example.administrator.share.activity.SystemInfoActivity;
import com.example.administrator.share.util.Constants;
import com.startsmake.mainnavigatetabbar.widget.BadgeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class MessageFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private LinearLayout commentLl;
    private LinearLayout favorLl;
    private LinearLayout fansLl;
    private LinearLayout systemLl;
    private BadgeView commentBadge;
    private BadgeView favorBadge;
    private BadgeView fansBadge;
    private TextView commentBadgeTv;
    private TextView favorBadgeTv;
    private TextView fansBadgeTv;
    private Toolbar toolbar;
    private RecyclerView msgRecyclerView;
    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        setHasOptionsMenu(true);
        findViewById(view);
        initView();
        return view;
    }

    private void findViewById(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        commentLl = view.findViewById(R.id.ll_message_comment);
        favorLl = view.findViewById(R.id.ll_message_favor);
        fansLl = view.findViewById(R.id.ll_message_fans);
        systemLl = view.findViewById(R.id.ll_message_system);
        commentBadgeTv = view.findViewById(R.id.tv_comment_badge);
        favorBadgeTv = view.findViewById(R.id.tv_favor_badge);
        fansBadgeTv = view.findViewById(R.id.tv_fans_badge);
        msgRecyclerView = view.findViewById(R.id.rv_message);
    }

    private void initView() {
        mContext = getActivity();
        toolbar.setTitle("通知");
        commentLl.setOnClickListener(this);
        favorLl.setOnClickListener(this);
        fansLl.setOnClickListener(this);
        systemLl.setOnClickListener(this);
        commentBadge = new BadgeView(mContext);
        commentBadge.setTargetView(commentBadgeTv);
        favorBadge = new BadgeView(mContext);
        favorBadge.setTargetView(favorBadgeTv);
        fansBadge = new BadgeView(mContext);
        fansBadge.setTargetView(fansBadgeTv);
        msgRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.more:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar, menu);
        menu.findItem(R.id.more).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.ll_message_comment:
                intent = new Intent(getActivity(), CommentActivity.class);
                break;
            case R.id.ll_message_favor:
                intent = new Intent(getActivity(), FavorFansActivity.class);
                intent.putExtra("index", 0);
                break;
            case R.id.ll_message_fans:
                intent = new Intent(getActivity(), FavorFansActivity.class);
                intent.putExtra("index", 1);
                updateNewFansStatus();
                break;
            case R.id.ll_message_system:
                intent = new Intent(getActivity(), SystemInfoActivity.class);
                break;
        }
        startActivity(intent);
    }

    private void findCommentStatus() {
        String url = Constants.BASE_URL + "Message?method=findCommentStatus";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("authorName", Constants.USER.getNickname())
                .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                .build()
                .execute(new StringCallback() {
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
                .addParams("authorId", String.valueOf(Constants.USER.getUserId()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (!Constants.ERROR.equals(response)) {
                            favorBadge.setBadgeCount(Integer.parseInt(response));
                        }
                    }
                });
    }

    private void findNewFansStatus() {
        String url = Constants.BASE_URL + "Message?method=findNewFansStatus";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        fansBadge.setBadgeCount(Integer.parseInt(response));
                    }
                });
    }

    private void updateNewFansStatus() {
        String url = Constants.BASE_URL + "Message?method=updateNewFansStatus";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        findCommentStatus();
        findFavorStatus();
        findNewFansStatus();
    }
}
