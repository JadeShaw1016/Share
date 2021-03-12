package com.example.administrator.share.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.FollowsListAdapter;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.FollowsListItem;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.FalsifyHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

public class FansActivity extends BaseActivity implements View.OnClickListener{

    private TextView titleText;
    private TextView fansRemindTv;
    private RecyclerView mListView;
    private View title_back;
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private List<FollowsListItem> mFansList;
    private RefreshLayout refreshLayout;
    private FollowsListAdapter adapter;
    private final int PAGE_COUNT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFansList = getIntent().getParcelableArrayListExtra("mFansList");
        setContentView(R.layout.activity_fans_focus);
        findViewById();
        initView();
        refreshListener();
    }

    @Override
    protected void findViewById() {
        mContext = this;
        titleText = $(R.id.titleText);
        title_back = $(R.id.title_back);
        mListView = $(R.id.normal_list_lv);
        fansRemindTv = $(R.id.tv_fans_remind);
        refreshLayout = $(R.id.refreshLayout);
    }

    @Override
    protected void initView() {
        titleText.setText("我的粉丝");
        title_back.setOnClickListener(this);
        layoutManager = new LinearLayoutManager(this);
        getFans();
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
     * 获取粉丝数
     */
    private void getFans() {
        if (mFansList.size() == 0) {
            fansRemindTv.setVisibility(View.VISIBLE);
        } else {
            fansRemindTv.setVisibility(View.INVISIBLE);
        }
        // 存储用户
        adapter = new FollowsListAdapter(mContext, mFansList,1);
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(adapter);
    }

    private List<FollowsListItem> getDatas(final int firstIndex, final int lastIndex) {
        List<FollowsListItem> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < mFansList.size()) {
                resList.add(mFansList.get(i));
            }
        }
        return resList;
    }

    private void updateRecyclerView(int fromIndex, int toIndex) {
        List<FollowsListItem> newDatas = getDatas(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            adapter.updateList(newDatas);
            refreshLayout.finishLoadmore();
        } else {
            refreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

}