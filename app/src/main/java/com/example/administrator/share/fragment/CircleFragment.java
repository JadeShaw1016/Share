package com.example.administrator.share.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.BeforeDateCheckActivity;
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.adapter.FoundCircleAdapter;
import com.example.administrator.share.entity.CircleListForFound;
import com.example.administrator.share.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class CircleFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{

    private ImageView calendarIv;
    private ListView circleList;
    private List<CircleListForFound> mList;
    private Context mContext;
    private FoundCircleAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle, container, false);
        findViewById(view);
        initView();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        reLoadNews();
    }

    private void reLoadNews() {
        String url = Constants.BASE_URL + "News?method=getNewsList";
        OkHttpUtils
                .post()
                .url(url)
                .id(1)
                .build()
                .execute(new MyStringCallback());
    }

    private void findViewById(View view){
        calendarIv = view.findViewById(R.id.iv_record);
        circleList = view.findViewById(R.id.list_circle);
    }

    private void initView(){
        mContext = getActivity();
        calendarIv.setOnClickListener(this);
        circleList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_record:
                Intent intent=new Intent(getActivity(), BeforeDateCheckActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), CircleDetailActivity.class);
        intent.putExtra("newsId", mList.get(position).getNewsId());
        intent.putExtra("fansId", mList.get(position).getUserId());
        startActivity(intent);
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            try {
                Type type = new TypeToken<ArrayList<CircleListForFound>>() {
                }.getType();
                mList = gson.fromJson(response, type);
            } catch (Exception e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                mList = null;
            }
            switch (id) {
                case 1:
                    if (mList != null && mList.size() > 0) {
                        adapter = new FoundCircleAdapter(mContext, mList);
                        circleList.setAdapter(adapter);
                    }
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
