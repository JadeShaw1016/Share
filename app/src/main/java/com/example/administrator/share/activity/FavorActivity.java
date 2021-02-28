package com.example.administrator.share.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.FavorListAdapter;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.NewsListItem;
import com.example.administrator.share.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.FalsifyHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class FavorActivity extends BaseActivity implements View.OnClickListener{

    private TextView titleText;
    private RefreshLayout refreshLayout;
    private RecyclerView mListView;
    private TextView msgRemindTv;
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private FrameLayout messageLl;
    private View title_back;
    private FavorListAdapter adapter;
    private List<NewsListItem> mList;
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
        mListView = $(R.id.normal_list_lv);
        refreshLayout = $(R.id.refreshLayout);
        msgRemindTv = $(R.id.tv_fav_remind);
        messageLl = $(R.id.layout_message);
    }

    @Override
    protected void initView(){
        mContext = this;
        titleText.setText("点赞");
        title_back.setOnClickListener(this);
        layoutManager = new LinearLayoutManager(this);
        messageLl.setVisibility(View.VISIBLE);
    }

    private void refreshListener(){
        refreshLayout.setEnableLoadmoreWhenContentNotFull(false);
        refreshLayout.setDisableContentWhenLoading(true);//是否在加载的时候禁止列表的操作
        refreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
        refreshLayout.setEnableAutoLoadmore(false);//是否启用列表惯性滑动到底部时自动加载更多
        //上拉加载
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                updateRecyclerView(adapter.getItemCount(), adapter.getItemCount() + PAGE_COUNT);
            }
        });
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new FalsifyHeader(context);
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                return new ClassicsFooter(context).setDrawableSize(20);
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

    /**
     * 获取被点赞列表
     */
    private void getComments() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "Message?method=getBeFavoredList";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .build()
                        .execute(new MyStringCallback());
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
                    mList = gson.fromJson(response, type);
                    if(mContext != null){
                        if (mList.size() == 0) {
                            msgRemindTv.setVisibility(View.VISIBLE);
                        } else {
                            msgRemindTv.setVisibility(View.INVISIBLE);
                        }
                        // 存储用户
                        adapter = new FavorListAdapter(mContext, mList);
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
            refreshLayout.finishLoadmore();
        } else {
            refreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

}