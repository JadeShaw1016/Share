package com.example.administrator.share.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.MessageListAdapter;
import com.example.administrator.share.entity.NewsListItem;
import com.example.administrator.share.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MessageFragment extends Fragment implements View.OnClickListener{

    private RefreshLayout refreshLayout;
    private RecyclerView mListView;
    private TextView msgRemindTv;
    private ImageView moreIv;
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private FrameLayout messageLl;

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
        msgRemindTv = view.findViewById(R.id.tv_msg_remind);
        moreIv = view.findViewById(R.id.iv_more);
        messageLl = view.findViewById(R.id.layout_message);
    }

    private void initView(){
        mContext = getActivity();
        moreIv.setOnClickListener(this);
        layoutManager = new LinearLayoutManager(getActivity());
        messageLl.setVisibility(View.VISIBLE);
    }


    private void refreshListener(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getComments();
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
//                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
                return new MaterialHeader(context).setShowBezierWave(false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_more:
//                Intent intent = new Intent(getActivity(), BadgeViewTwo.class);
//                startActivity(intent);
                break;
        }
    }

    /**
     * 获取评论
     */
    private void getComments() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "Comment?method=getCommentsList";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("authorName", Constants.USER.getUsername())
                        .build()
                        .execute(new MyStringCallback());
                refreshLayout.finishRefresh();
            }
        }).start();
    }


    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            switch (id) {
                case 1:
                    Type type = new TypeToken<ArrayList<NewsListItem>>() {}.getType();
                    List<NewsListItem> mList = gson.fromJson(response, type);
                    if(mContext != null){
                        if (mList.size() == 0) {
                            msgRemindTv.setVisibility(View.VISIBLE);
                        } else {
                            msgRemindTv.setVisibility(View.INVISIBLE);
                        }
                        // 存储用户
                        MessageListAdapter adapter = new MessageListAdapter(mContext, mList);
                        mListView.setLayoutManager(layoutManager);
                        mListView.setAdapter(adapter);
                    }
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
        getComments();
    }
}
