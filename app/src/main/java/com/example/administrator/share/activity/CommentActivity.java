package com.example.administrator.share.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.CommentListAdapter;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.NewsListItem;
import com.example.administrator.share.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class CommentActivity extends BaseActivity implements View.OnClickListener{

    private TextView titleText;
    private RefreshLayout refreshLayout;
    private RecyclerView mListView;
    private TextView msgRemindTv;
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private FrameLayout messageLl;
    private View title_back;
    private List<NewsListItem> mList;
    private CommentListAdapter adapter;
    private final int PAGE_COUNT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_normal_list);
        findViewById();
        initView();
        refreshListener();
    }

    @Override
    protected void findViewById(){
        titleText = $(R.id.titleText);
        title_back = $(R.id.title_back);
        mListView = findViewById(R.id.normal_list_lv);
        refreshLayout = findViewById(R.id.refreshLayout);
        msgRemindTv = findViewById(R.id.tv_msg_remind);
        messageLl = findViewById(R.id.layout_message);
    }

    @Override
    protected void initView(){
        mContext = this;
        titleText.setText("评论");
        title_back.setOnClickListener(this);
        layoutManager = new LinearLayoutManager(this);
        messageLl.setVisibility(View.VISIBLE);
    }


    private void refreshListener(){
        //下拉刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if(adapter != null){
                    adapter.resetDatas();
                }
                getComments();
            }

        });
        //上拉加载
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                updateRecyclerView(adapter.getItemCount(), adapter.getItemCount() + PAGE_COUNT);
            }

        });
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
            case R.id.title_back:
                finish();
                break;
        }
    }

    private List<NewsListItem> getDatas(final int firstIndex, final int lastIndex) {
        List<NewsListItem> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < mList.size()) {
                resList.add(mList.get(i));
            }
        }
        return resList;
    }

    private void updateRecyclerView(int fromIndex, int toIndex) {
        List<NewsListItem> newDatas = getDatas(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            adapter.updateList(newDatas);
        } else {
            DisplayToast("我已经到底咯");
        }
        refreshLayout.finishLoadmore();
    }

    /**
     * 获取评论
     */
    private void getComments() {
        new AsyncTask<Void,Void,Integer>(){

            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "Message?method=getCommentsList";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("authorName", Constants.USER.getUsername())
                        .build()
                        .execute(new MyStringCallback());
                return null;
            }
        }.execute();
    }


    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            switch (id) {
                case 1:
                    Type type = new TypeToken<ArrayList<NewsListItem>>() {}.getType();
                    mList = gson.fromJson(response, type);
                    if(mContext != null){
                        if (mList.size() == 0) {
                            msgRemindTv.setVisibility(View.VISIBLE);
                        } else {
                            msgRemindTv.setVisibility(View.INVISIBLE);
                        }
                        // 存储用户
                        adapter = new CommentListAdapter(mContext, getDatas(0, PAGE_COUNT));
                        mListView.setLayoutManager(layoutManager);
                        mListView.setAdapter(adapter);
                        refreshLayout.finishRefresh();
                    }
                    break;
                default:
                    DisplayToast("what?");
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            DisplayToast("网络链接出错!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getComments();
    }

}