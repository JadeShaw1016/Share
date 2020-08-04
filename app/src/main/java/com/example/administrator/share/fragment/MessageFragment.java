package com.example.administrator.share.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.BadgeViewTwo;
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.adapter.MessageListAdapter;
import com.example.administrator.share.entity.NewsListItem;
import com.example.administrator.share.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;

public class MessageFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{

    private MessageListAdapter adapter;
    private List<NewsListItem> mList;
    private RefreshLayout refreshLayout;
    private ListView mListView;
    private LinearLayout messageLl;
    private TextView msgRemindTv;
    private ImageView moreIv;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_normal_list,container,false);
        findViewById(view);
        initView();
        refreshListener();
        return view;
    }

    private void findViewById(View view){
        mListView = view.findViewById(R.id.normal_list_lv);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        messageLl = view.findViewById(R.id.layout_message);
        msgRemindTv = view.findViewById(R.id.tv_msg_remind);
        moreIv = view.findViewById(R.id.iv_more);
    }

    private void initView(){
        mListView.setOnItemClickListener(this);
        moreIv.setOnClickListener(this);
        messageLl.setVisibility(View.VISIBLE);
        getComments();
    }


    private void refreshListener(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getComments();
                refreshlayout.finishRefresh(1000);
            }

        });
        //下拉加载
//        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
//            @Override
//            public void onLoadmore(RefreshLayout refreshlayout) {
//                refreshlayout.finishLoadmore(1000);
//            }
//
//        });
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_more:
                Intent intent = new Intent(getActivity(), BadgeViewTwo.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mList!=null && mList.size()>0){
            NewsListItem newsListItem = mList.get(position);
            Intent intent = new Intent();
            intent.setClass(getActivity(), CircleDetailActivity.class);
            intent.putExtra("newsId", newsListItem.getNewsId());
            startActivity(intent);
            if(newsListItem.getStatus() == 0){
                updateCommentStatus(newsListItem);
            }
        }
    }


    /**
     * 获取评论
     */
    private void getComments() {

        String url = Constants.BASE_URL + "Comment?method=getCommentsList";
        OkHttpUtils
                .post()
                .url(url)
                .id(1)
                .addParams("authorName", Constants.USER.getUsername())
                .build()
                .execute(new MyStringCallback());
    }

    /**
     * 修改评论状态
     */
    private void updateCommentStatus(NewsListItem newsListItem) {

        String url = Constants.BASE_URL + "Comment?method=updateCommentStatus";
        OkHttpUtils
                .post()
                .url(url)
                .id(2)
                .addParams("commentId", String.valueOf(newsListItem.getCommentId()))
                .build()
                .execute(new MyStringCallback());
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            switch (id) {
                case 1:
                    Type type = new TypeToken<ArrayList<NewsListItem>>() {
                    }.getType();
                    mList = gson.fromJson(response, type);
                    if (mList == null || mList.size() == 0) {
                        adapter = new MessageListAdapter(getActivity(), mList);
                        mListView.setAdapter(adapter);
                        msgRemindTv.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(),"暂无数据",Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        // 设置数据倒叙
                        Collections.reverse(mList);
                        msgRemindTv.setVisibility(View.INVISIBLE);
                        // 存储用户
                        adapter = new MessageListAdapter(getActivity(), mList);
                        mListView.setAdapter(adapter);
                    }
                    break;
                case 2:
                    break;
                default:
                    Toast.makeText(getActivity(),"What?",Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            Toast.makeText(getActivity(),"网络链接出错!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.autoRefresh();
    }
}
