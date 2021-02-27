package com.example.administrator.share.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.BeforeDateCheckActivity;
import com.example.administrator.share.adapter.FoundCircleAdapter;
import com.example.administrator.share.entity.CircleListForFound;
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
import java.util.List;

import okhttp3.Call;


public class CircleFragment extends Fragment implements View.OnClickListener{

    private ImageView calendarIv;
    private RecyclerView circleList;
    private Context mContext;
    private RefreshLayout refreshLayout;
    private TextView circleRemindTv;
    private TextView titleText;
    private LinearLayoutManager layoutManager;
    private TextView bianpingTv,oumeiTv,erchaTv,xieshiTv,chouxiangTv;
    private TextView indexTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle, container, false);
        findViewById(view);
        initView();
        refreshListener();
        return view;
    }

    private void findViewById(View view){
        titleText = view.findViewById(R.id.titleText);
        calendarIv = view.findViewById(R.id.iv_record);
        circleList = view.findViewById(R.id.list_circle);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        circleRemindTv = view.findViewById(R.id.tv_circle_remind);
        bianpingTv = view.findViewById(R.id.tv_circle_bianping);
        oumeiTv = view.findViewById(R.id.tv_circle_oumei);
        erchaTv = view.findViewById(R.id.tv_circle_ercha);
        xieshiTv = view.findViewById(R.id.tv_circle_xieshi);
        chouxiangTv = view.findViewById(R.id.tv_circle_chouxiang);
    }

    private void initView(){
        mContext = getActivity();
        titleText.setText("圈子");
        calendarIv.setVisibility(View.VISIBLE);
        calendarIv.setOnClickListener(this);
        bianpingTv.setOnClickListener(this);
        oumeiTv.setOnClickListener(this);
        erchaTv.setOnClickListener(this);
        xieshiTv.setOnClickListener(this);
        chouxiangTv.setOnClickListener(this);
        layoutManager = new LinearLayoutManager(getActivity());
        getNewsList();
    }

    private void refreshListener(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if(indexTv != null){
                    getNewsListWithLabel(indexTv.getText().toString());
                }else {
                    getNewsList();
                }
            }

        });

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
            case R.id.iv_record:
                Intent intent=new Intent(getActivity(), BeforeDateCheckActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_circle_bianping:
                if(indexTv != null){
                    indexTv.setBackgroundResource(R.drawable.bg_username);
                    if(indexTv == bianpingTv){
                        indexTv = null;
                        getNewsList();
                        break;
                    }
                }
                bianpingTv.setBackgroundResource(R.drawable.bg_username_selected);
                indexTv = bianpingTv;
                getNewsListWithLabel(indexTv.getText().toString());
                break;
            case R.id.tv_circle_oumei:
                if(indexTv != null){
                    indexTv.setBackgroundResource(R.drawable.bg_username);
                    if(indexTv == oumeiTv){
                        indexTv = null;
                        getNewsList();
                        break;
                    }
                }
                oumeiTv.setBackgroundResource(R.drawable.bg_username_selected);
                indexTv = oumeiTv;
                getNewsListWithLabel(indexTv.getText().toString());
                break;
            case R.id.tv_circle_ercha:
                if(indexTv != null){
                    indexTv.setBackgroundResource(R.drawable.bg_username);
                    if(indexTv == erchaTv){
                        indexTv = null;
                        getNewsList();
                        break;
                    }
                }
                erchaTv.setBackgroundResource(R.drawable.bg_username_selected);
                indexTv = erchaTv;
                getNewsListWithLabel(indexTv.getText().toString());
                break;
            case R.id.tv_circle_xieshi:
                if(indexTv != null){
                    indexTv.setBackgroundResource(R.drawable.bg_username);
                    if(indexTv == xieshiTv){
                        indexTv = null;
                        getNewsList();
                        break;
                    }
                }
                xieshiTv.setBackgroundResource(R.drawable.bg_username_selected);
                indexTv = xieshiTv;
                getNewsListWithLabel(indexTv.getText().toString());
                break;
            case R.id.tv_circle_chouxiang:
                if(indexTv != null){
                    indexTv.setBackgroundResource(R.drawable.bg_username);
                    if(indexTv == chouxiangTv){
                        indexTv = null;
                        getNewsList();
                        break;
                    }
                }
                chouxiangTv.setBackgroundResource(R.drawable.bg_username_selected);
                indexTv = chouxiangTv;
                getNewsListWithLabel(indexTv.getText().toString());
                break;
        }
    }

    private void getNewsList() {
        new AsyncTask<Void,Void,Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "News?method=getNewsList";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .build()
                        .execute(new MyStringCallback());
                return null;
            }
        }.execute();
    }

    private void getNewsListWithLabel(final String label){
        new AsyncTask<Void,Void,Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "News?method=getNewsListWithLabel";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("label",label)
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
            List<CircleListForFound> mList;
            try {
                Type type = new TypeToken<ArrayList<CircleListForFound>>() {}.getType();
                mList = gson.fromJson(response, type);
            } catch (Exception e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                mList = null;
            }
            switch (id) {
                case 1:
                    if (mList != null && mList.size() > 0) {
                        FoundCircleAdapter adapter = new FoundCircleAdapter(mContext, mList);
                        circleList.setLayoutManager(layoutManager);
                        circleList.setAdapter(adapter);
                        circleRemindTv.setVisibility(View.INVISIBLE);
                    }else{
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
            Toast.makeText(mContext, "网络链接出错！", Toast.LENGTH_SHORT).show();
        }
    }

}
