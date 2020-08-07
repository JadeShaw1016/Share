package com.example.administrator.share.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.FansListAdapter;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.FansListItem;
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

public class FansActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView titleText;
    private RefreshLayout refreshLayout;
    private TextView fansRemindTv;
    private ListView mListView;
    private View title_back;

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans);

        findViewById();
        initView();
        refreshListener();
    }

    @Override
    protected void findViewById() {
        mContext = this;
        titleText = $(R.id.titleText);
        title_back = $(R.id.title_back);
        mListView = findViewById(R.id.normal_list_lv);
        refreshLayout = findViewById(R.id.refreshLayout);
        fansRemindTv = findViewById(R.id.tv_fans_remind);
    }

    @Override
    protected void initView() {
        titleText.setText("我的粉丝");
        title_back.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        getFans();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private void refreshListener(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getFans();
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


    /**
     * 获取粉丝数
     */
    private void getFans() {

        String url = Constants.BASE_URL + "Follows?method=getFansList";
        OkHttpUtils
                .post()
                .url(url)
                .id(1)
                .addParams("userId", Constants.USER.getUserId() + "")
                .build()
                .execute(new MyStringCallback());
    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            switch (id) {
                case 1:
                    Type type = new TypeToken<ArrayList<FansListItem>>() {
                    }.getType();
                    List<FansListItem> mList = gson.fromJson(response, type);
                    FansListAdapter adapter;
                    if (mList == null || mList.size() == 0) {
                        adapter = new FansListAdapter(mContext, mList);
                        mListView.setAdapter(adapter);
                        fansRemindTv.setVisibility(View.VISIBLE);
                        DisplayToast("暂无数据");
                        return;
                    } else {
                        // 设置数据倒叙
                        Collections.reverse(mList);
                        fansRemindTv.setVisibility(View.INVISIBLE);
                        // 存储用户
                        adapter = new FansListAdapter(mContext, mList);
                        mListView.setAdapter(adapter);
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


}