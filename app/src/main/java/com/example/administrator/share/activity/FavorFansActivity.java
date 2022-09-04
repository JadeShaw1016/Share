package com.example.administrator.share.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.FavorListAdapter;
import com.example.administrator.share.adapter.NewFansListAdapter;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.CommonListItem;
import com.example.administrator.share.entity.FollowsListItem;
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

public class FavorFansActivity extends BaseActivity implements View.OnClickListener{

    private TextView titleText;
    private RefreshLayout refreshLayout;
    private RecyclerView mListView;
    private ImageView msgRemindIv;
    private TextView msgRemindTv;
    private TextView fansRemindTv;
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private FrameLayout messageLl;
    private View title_back;
    private FavorListAdapter favorListAdapter;
    private NewFansListAdapter fansListAdapter;
    private List<CommonListItem> mNewsList;
    private List<FollowsListItem> mFansList;
    private final int PAGE_COUNT = 10;
    private int INDEX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INDEX = getIntent().getIntExtra("index",0);
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
        msgRemindIv = $(R.id.iv_normal_list_remind);
        msgRemindTv = $(R.id.tv_new_fans_remind);
        fansRemindTv = $(R.id.tv_fans_remind);
        messageLl = $(R.id.layout_message);
    }

    @Override
    protected void initView(){
        mContext = this;
        if(INDEX == 0){
            titleText.setText("点赞");
        }else{
            titleText.setText("粉丝");
        }
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
                if(INDEX == 0){
                    updateRecyclerView(favorListAdapter.getItemCount(), favorListAdapter.getItemCount() + PAGE_COUNT,0);
                }
                else{
                    updateRecyclerView(fansListAdapter.getItemCount(), fansListAdapter.getItemCount() + PAGE_COUNT,1);
                }
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
    private void getFavors() {
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

    /**
     * 获取新增粉丝列表
     */
    private void getFans() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "Follows?method=getFansList";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(2)
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
            Type type;
            switch (id) {
                case 1:
                    type = new TypeToken<ArrayList<CommonListItem>>() {}.getType();
                    mNewsList = gson.fromJson(response, type);
                    if(mContext != null){
                        if (mNewsList.size() == 0) {
                            msgRemindIv.setVisibility(View.VISIBLE);
                            msgRemindTv.setVisibility(View.VISIBLE);
                        } else {
                            msgRemindIv.setVisibility(View.INVISIBLE);
                            msgRemindTv.setVisibility(View.INVISIBLE);
                        }
                        // 存储用户
                        favorListAdapter = new FavorListAdapter(mContext, mNewsList);
                        mListView.setLayoutManager(layoutManager);
                        mListView.setAdapter(favorListAdapter);
                        refreshLayout.finishRefresh();
                    }
                    break;
                case 2:
                    type = new TypeToken<ArrayList<FollowsListItem>>() {}.getType();
                    mFansList = gson.fromJson(response, type);
                    if(mContext != null){
                        if (mFansList.size() == 0) {
                            msgRemindIv.setVisibility(View.VISIBLE);
                            fansRemindTv.setVisibility(View.VISIBLE);
                        } else {
                            msgRemindIv.setVisibility(View.INVISIBLE);
                            fansRemindTv.setVisibility(View.INVISIBLE);
                        }
                        // 存储用户
                        fansListAdapter = new NewFansListAdapter(mContext, mFansList);
                        mListView.setLayoutManager(layoutManager);
                        mListView.setAdapter(fansListAdapter);
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
        switch (INDEX){
            case 0:
                getFavors();
                break;
            case 1:
                getFans();
                break;
        }
    }

    private List<CommonListItem> getNewsDatas(final int firstIndex, final int lastIndex) {
        List<CommonListItem> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < mNewsList.size()) {
                resList.add(mNewsList.get(i));
            }
        }
        return resList;
    }

    private List<FollowsListItem> getFansDatas(final int firstIndex, final int lastIndex) {
        List<FollowsListItem> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < mFansList.size()) {
                resList.add(mFansList.get(i));
            }
        }
        return resList;
    }

    private void updateRecyclerView(int fromIndex, int toIndex,int index) {
        switch (index){
            case 0:
                List<CommonListItem> newNewsDatas = getNewsDatas(fromIndex, toIndex);
                if (newNewsDatas.size() > 0) {
                    favorListAdapter.updateList(newNewsDatas);
                    refreshLayout.finishLoadmore();
                } else {
                    refreshLayout.finishLoadmoreWithNoMoreData();
                }
                break;
            case 1:
                List<FollowsListItem> newFansDatas = getFansDatas(fromIndex, toIndex);
                if (newFansDatas.size() > 0) {
                    fansListAdapter.updateList(newFansDatas);
                    refreshLayout.finishLoadmore();
                } else {
                    refreshLayout.finishLoadmoreWithNoMoreData();
                }
                break;
        }

    }

}