package com.example.administrator.share.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.share.R;
import com.example.administrator.share.ShareApplication;
import com.example.administrator.share.adapter.MyWorkAdapter;
import com.example.administrator.share.entity.MyWorkListItem;
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

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class MyWorkFragment extends Fragment {

    private List<MyWorkListItem> mList;
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private GridLayoutManager layoutManager;
    private ImageView myworkRemindIv;
    private TextView myworkRemindTv;
    private MyWorkAdapter adapter;
    private final int PAGE_COUNT = 10;
    private static String USERID;

    public static Fragment newInstance(String userId) {
        USERID = userId;
        return new MyWorkFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mywork, container, false);
        findViewById(view);
        initView();
        refreshListener();
        return view;
    }

    private void findViewById(View view) {
        recyclerView = view.findViewById(R.id.rv_mywork);
        refreshLayout = view.findViewById(R.id.swipe_refresh_mywork);
        myworkRemindIv = view.findViewById(R.id.iv_mywork_remind);
        myworkRemindTv = view.findViewById(R.id.tv_mywork_remind);
    }

    private void initView() {
        layoutManager = new GridLayoutManager(getActivity(), 2);
        adapter = new MyWorkAdapter(getActivity(), new ArrayList<MyWorkListItem>());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void refreshListener() {
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

    /**
     * 获取我的作品
     */
    private void getMyWorks() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "circle/getMyWorkList";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(1)
                        .addParams("userId", USERID)
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            if (Constants.ERROR.equals(response)) {
                mList = null;
            } else {
                try {
                    mList = new Gson().fromJson(response, new TypeToken<ArrayList<MyWorkListItem>>() {
                    }.getType());
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            switch (id) {
                case 1:
                    if (mList == null || mList.size() == 0) {
                        myworkRemindIv.setVisibility(View.VISIBLE);
                        myworkRemindTv.setVisibility(View.VISIBLE);
                    } else {
                        myworkRemindIv.setVisibility(View.INVISIBLE);
                        myworkRemindTv.setVisibility(View.INVISIBLE);
                        adapter = new MyWorkAdapter(getActivity(), mList);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    }
                    refreshLayout.finishRefresh();
                    break;

                default:
                    Toast.makeText(ShareApplication.getContextObject(), "What?", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            adapter = new MyWorkAdapter(getActivity(), new ArrayList<MyWorkListItem>());
            recyclerView.setAdapter(adapter);
            myworkRemindIv.setImageResource(R.drawable.default_remind_nosignal);
            myworkRemindTv.setText(R.string.no_network_remind);
            myworkRemindIv.setVisibility(View.VISIBLE);
            myworkRemindTv.setVisibility(View.VISIBLE);
            Toast.makeText(ShareApplication.getContextObject(), "网络链接出错!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyWorks();
    }

    private List<MyWorkListItem> getDatas(final int firstIndex, final int lastIndex) {
        List<MyWorkListItem> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < mList.size()) {
                resList.add(mList.get(i));
            }
        }
        return resList;
    }

    private void updateRecyclerView(int fromIndex, int toIndex) {
        List<MyWorkListItem> newDatas = getDatas(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            adapter.updateList(newDatas);
            refreshLayout.finishLoadmore();
        } else {
            refreshLayout.finishLoadmoreWithNoMoreData();
        }
    }
}
