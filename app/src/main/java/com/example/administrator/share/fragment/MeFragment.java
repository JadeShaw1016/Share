package com.example.administrator.share.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.FansActivity;
import com.example.administrator.share.activity.FocusActivity;
import com.example.administrator.share.activity.SettingActivity;
import com.example.administrator.share.adapter.FragmentAdapter;
import com.example.administrator.share.entity.FansListItem;
import com.example.administrator.share.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;


public class MeFragment extends Fragment implements View.OnClickListener{

    private TabLayout tabLayout;
    private ViewPager pager;
    private String [] title={"我的作品","我的点赞","我的收藏","我的草稿"};

    private ImageView settingIv;
    private TextView usernameTv;
    private TextView fansTv;
    private TextView focusTv;

    List<FansListItem> mList;

    private LinearLayout fansLl;
    private LinearLayout focusLl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        findViewById(view);
        initView();
        setAdapter();

        return view;
    }

    private void findViewById(View view){
        pager= view.findViewById(R.id.page);
        tabLayout= view.findViewById(R.id.tab_layout);
        settingIv = view.findViewById(R.id.iv1);
        usernameTv = view.findViewById(R.id.me_homepage_username);
        fansTv = view.findViewById(R.id.me_fans);
        fansLl = view.findViewById(R.id.ll_fans);
        focusLl = view.findViewById(R.id.ll_focus);
        focusTv = view.findViewById(R.id.tv_focus);
    }

    private void initView(){
        settingIv.setOnClickListener(this);
        fansLl.setOnClickListener(this);
        focusLl.setOnClickListener(this);
        echo();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.iv1:
                intent=new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_fans:
                intent=new Intent(getActivity(), FansActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_focus:
                intent=new Intent(getActivity(), FocusActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void setAdapter(){
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> mTitles = new ArrayList<>();

        Collections.addAll(mTitles, title);
        fragmentList.add(MyWorkFragment.newInstance(title[0]));
        fragmentList.add(MyFavoFragment.newInstance(title[1]));
        fragmentList.add(MyCollectionFragment.newInstance(title[2]));
        fragmentList.add(MyScriptFragment.newInstance(title[3]));

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getChildFragmentManager(), fragmentList, mTitles);
        pager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(pager);//与ViewPage建立关系
    }

    private void echo(){
        usernameTv.setText(Constants.USER.getUsername());
    }

    /**
     * 获取粉丝数
     */
    private void getFans() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "Follows?method=getFansList";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("userId", Constants.USER.getUserId() + "")
                        .build()
                        .execute(new MeFragment.MyStringCallback());
            }
        }).start();
    }

    /**
     * 获取关注数
     */
    private void getFocus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "Follows?method=getFocusList";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(2)
                        .addParams("userId", Constants.USER.getUserId() + "")
                        .build()
                        .execute(new MeFragment.MyStringCallback());
            }
        }).start();
    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<FansListItem>>() {
            }.getType();
            mList = gson.fromJson(response, type);
            switch (id) {
                case 1:
                    fansTv.setText(String.valueOf(mList.size()));
                    break;
                case 2:
                    focusTv.setText(String.valueOf(mList.size()));
                    break;
                default:
                    Toast.makeText(getActivity(), "What?", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            Toast.makeText(getActivity(), "网络链接出错!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getFans();
        getFocus();
    }
}
