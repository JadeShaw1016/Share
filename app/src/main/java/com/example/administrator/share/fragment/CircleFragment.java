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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.BeforeDateCheckActivity;
import com.example.administrator.share.adapter.FoundCircleAdapter;
import com.example.administrator.share.entity.CircleList;
import com.example.administrator.share.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class CircleFragment extends Fragment implements View.OnClickListener {

    private RecyclerView circleList;
    private Context mContext;
    private RefreshLayout refreshLayout;
    private ImageView circleRemindIv;
    private TextView circleRemindTv;
    private TextView bianpingTv, oumeiTv, erchaTv, xieshiTv, chouxiangTv;
    private TextView indexTv;
    private FoundCircleAdapter adapter;
    private List<CircleList> mCircleList;
    private final int PAGE_COUNT = 5;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle, container, false);
        setHasOptionsMenu(true);
        findViewById(view);
        initView();
        refreshListener();
        return view;
    }

    private void findViewById(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
        circleList = view.findViewById(R.id.list_circle);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        circleRemindIv = view.findViewById(R.id.iv_circle_remind);
        circleRemindTv = view.findViewById(R.id.tv_circle_remind);
        bianpingTv = view.findViewById(R.id.tv_circle_bianping);
        oumeiTv = view.findViewById(R.id.tv_circle_oumei);
        erchaTv = view.findViewById(R.id.tv_circle_ercha);
        xieshiTv = view.findViewById(R.id.tv_circle_xieshi);
        chouxiangTv = view.findViewById(R.id.tv_circle_chouxiang);
    }

    private void initView() {
        toolbar.setTitle("圈子");
        mContext = getActivity();
        bianpingTv.setOnClickListener(this);
        oumeiTv.setOnClickListener(this);
        erchaTv.setOnClickListener(this);
        xieshiTv.setOnClickListener(this);
        chouxiangTv.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        adapter = new FoundCircleAdapter(mContext, new ArrayList<CircleList>());
        circleList.setLayoutManager(layoutManager);
        circleList.setAdapter(adapter);
        getCircleList();
    }

    private void refreshListener() {
        refreshLayout.setEnableLoadmoreWhenContentNotFull(false);
        refreshLayout.setDisableContentWhenLoading(true);//是否在加载的时候禁止列表的操作
        refreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
        refreshLayout.setEnableAutoLoadmore(false);//是否启用列表惯性滑动到底部时自动加载更多
        //下拉刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.resetNoMoreData();
                if (indexTv != null) {
                    getCircleListWithLabel(indexTv.getText().toString());
                } else {
                    getCircleList();
                }
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
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calendar:
                Intent intent = new Intent(getActivity(), BeforeDateCheckActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
        inflater.inflate(R.menu.toolbar, menu);
        menu.findItem(R.id.calendar).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_circle_bianping:
                if (indexTv != null) {
                    indexTv.setBackgroundResource(R.drawable.bg_username);
                    if (indexTv == bianpingTv) {
                        indexTv = null;
                        getCircleList();
                        break;
                    }
                }
                bianpingTv.setBackgroundResource(R.drawable.bg_username_selected);
                indexTv = bianpingTv;
                getCircleListWithLabel(indexTv.getText().toString());
                break;
            case R.id.tv_circle_oumei:
                if (indexTv != null) {
                    indexTv.setBackgroundResource(R.drawable.bg_username);
                    if (indexTv == oumeiTv) {
                        indexTv = null;
                        getCircleList();
                        break;
                    }
                }
                oumeiTv.setBackgroundResource(R.drawable.bg_username_selected);
                indexTv = oumeiTv;
                getCircleListWithLabel(indexTv.getText().toString());
                break;
            case R.id.tv_circle_ercha:
                if (indexTv != null) {
                    indexTv.setBackgroundResource(R.drawable.bg_username);
                    if (indexTv == erchaTv) {
                        indexTv = null;
                        getCircleList();
                        break;
                    }
                }
                erchaTv.setBackgroundResource(R.drawable.bg_username_selected);
                indexTv = erchaTv;
                getCircleListWithLabel(indexTv.getText().toString());
                break;
            case R.id.tv_circle_xieshi:
                if (indexTv != null) {
                    indexTv.setBackgroundResource(R.drawable.bg_username);
                    if (indexTv == xieshiTv) {
                        indexTv = null;
                        getCircleList();
                        break;
                    }
                }
                xieshiTv.setBackgroundResource(R.drawable.bg_username_selected);
                indexTv = xieshiTv;
                getCircleListWithLabel(indexTv.getText().toString());
                break;
            case R.id.tv_circle_chouxiang:
                if (indexTv != null) {
                    indexTv.setBackgroundResource(R.drawable.bg_username);
                    if (indexTv == chouxiangTv) {
                        indexTv = null;
                        getCircleList();
                        break;
                    }
                }
                chouxiangTv.setBackgroundResource(R.drawable.bg_username_selected);
                indexTv = chouxiangTv;
                getCircleListWithLabel(indexTv.getText().toString());
                break;
        }
    }

    private void getCircleList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "circle/getCircleListWithTopic";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(1)
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    private void getCircleListWithLabel(final String label) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "circle/getCircleListWithTopicByLabel";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(1)
                        .addParams("label", label)
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            if (Constants.ERROR.equals(response)) {
                mCircleList = null;
            } else {
                try {
                    mCircleList = new Gson().fromJson(response, new TypeToken<ArrayList<CircleList>>() {
                    }.getType());
                } catch (Exception e) {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            switch (id) {
                case 1:
                    if (mCircleList != null && mCircleList.size() > 0) {
                        adapter = new FoundCircleAdapter(mContext, getDatas(0, PAGE_COUNT));
                        circleList.setAdapter(adapter);
                        circleRemindIv.setVisibility(View.INVISIBLE);
                        circleRemindTv.setVisibility(View.INVISIBLE);
                    } else {
                        adapter = new FoundCircleAdapter(mContext, new ArrayList<CircleList>());
                        circleList.setAdapter(adapter);
                        circleRemindIv.setVisibility(View.VISIBLE);
                        circleRemindTv.setVisibility(View.VISIBLE);
                    }
                    refreshLayout.finishRefresh();
                    break;
                default:
                    Toast.makeText(mContext, "what！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            refreshLayout.finishRefresh();
            adapter = new FoundCircleAdapter(mContext, new ArrayList<CircleList>());
            circleList.setAdapter(adapter);
            circleRemindIv.setImageResource(R.drawable.default_remind_nosignal);
            circleRemindTv.setText(R.string.no_network_remind);
            circleRemindIv.setVisibility(View.VISIBLE);
            circleRemindTv.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, "网络链接出错！", Toast.LENGTH_SHORT).show();
        }
    }

    private List<CircleList> getDatas(final int firstIndex, final int lastIndex) {
        List<CircleList> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < mCircleList.size()) {
                resList.add(mCircleList.get(i));
            }
        }
        return resList;
    }

    private void updateRecyclerView(int fromIndex, int toIndex) {
        List<CircleList> newDatas = getDatas(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            adapter.updateList(newDatas);
            refreshLayout.finishLoadmore();
        } else {
            refreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

}
