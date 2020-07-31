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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.adapter.NormalListAdapter;
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


public class MyCollectionFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{

    private NormalListAdapter adapter;
    private List<NewsListItem> mList;
    private RefreshLayout refreshLayout;
    private TextView favRemindTv;
    private ListView mListView;

    public static Fragment newInstance(String title){
        MyCollectionFragment fragmentOne = new MyCollectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", title);
        //fragment保存参数，传入一个Bundle对象
        fragmentOne.setArguments(bundle);
        return fragmentOne;
    }

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
        favRemindTv = view.findViewById(R.id.tv_fav_remind);
    }

    private void initView(){
        mListView.setOnItemClickListener(this);
        getCollections();
    }

    private void refreshListener(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getCollections();
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

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mList!=null && mList.size()>0){
            Intent intent = new Intent();
            intent.setClass(getActivity(), CircleDetailActivity.class);
            intent.putExtra("newsId", mList.get(position).getNewsId());
            startActivity(intent);
        }
    }


    /**
     * 获取收藏
     */
    private void getCollections() {

        String url = Constants.BASE_URL + "Favor?method=getFavorsList";
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
                    Type type = new TypeToken<ArrayList<NewsListItem>>() {
                    }.getType();
                    mList = gson.fromJson(response, type);
                    if (mList == null || mList.size() == 0) {
                        adapter = new NormalListAdapter(getActivity(), mList);
                        mListView.setAdapter(adapter);
                        favRemindTv.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(),"暂无数据",Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        // 设置数据倒叙
                        Collections.reverse(mList);
                        favRemindTv.setVisibility(View.INVISIBLE);
                        // 存储用户
                        adapter = new NormalListAdapter(getActivity(), mList);
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

}
