package com.example.administrator.share.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.MyWorkAdapter;
import com.example.administrator.share.entity.NewsListItem;
import com.example.administrator.share.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class MyWorkFragment extends Fragment {

    private List<NewsListItem> mList;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private GridLayoutManager layoutManager;
    private TextView myworkRemindTv;
    private MyWorkAdapter adapter;

    public static Fragment newInstance(String title){
        MyWorkFragment fragmentOne = new MyWorkFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", title);
        //fragment保存参数，传入一个Bundle对象
        fragmentOne.setArguments(bundle);
        return fragmentOne;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_mywork,container,false);
        findViewById(view);
        getMyWorks();
        return view;
    }

    private void findViewById(View view){
        recyclerView = view.findViewById(R.id.rv_mywork);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        swipeRefresh = view.findViewById(R.id.swipe_refresh_mywork);
        swipeRefresh.setColorSchemeResources(R.color.fuxk_base_color_cyan);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyWorks();
            }
        });
        myworkRemindTv = view.findViewById(R.id.tv_mywork_remind);
    }

    /**
     * 获取我的作品
     */
    private void getMyWorks() {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "GetCircleList?method=getMyWorkList";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("userId", Constants.USER.getUserId() + "")
                        .build()
                        .execute(new MyStringCallback());
                return 0;
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
                    if(getActivity() != null){
                        if (mList == null || mList.size() == 0) {
                            myworkRemindTv.setVisibility(View.VISIBLE);
                        } else {
                            myworkRemindTv.setVisibility(View.INVISIBLE);
                        }
                        // 存储用户
                        adapter = new MyWorkAdapter(getActivity(), mList);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
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
        getMyWorks();
    }
}
