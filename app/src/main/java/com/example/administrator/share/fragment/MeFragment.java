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
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.SettingActivity;
import com.example.administrator.share.adapter.FragmentAdapter;
import com.example.administrator.share.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class MeFragment extends Fragment implements View.OnClickListener{

    private TabLayout tabLayout;
    private ViewPager pager;
    private FragmentAdapter fragmentAdapter;
    private List<Fragment> fragmentList;
    private List<String> mTitles;
    private String [] title={"我的作品","我的点赞","我的收藏","我的草稿"};

    private ImageView settingIv;
    private TextView usernameTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        findViewById(view);
        initView();


        fragmentList=new ArrayList<>();
        mTitles=new ArrayList<>();

        Collections.addAll(mTitles, title);
        fragmentList.add(MyWorkFragment.newInstance(title[0]));
        fragmentList.add(MyFavoFragment.newInstance(title[1]));
        fragmentList.add(MyCollectionFragment.newInstance(title[2]));
        fragmentList.add(MyScriptFragment.newInstance(title[3]));

        fragmentAdapter=new FragmentAdapter(getChildFragmentManager(),fragmentList,mTitles);
        pager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(pager);//与ViewPage建立关系
        return view;
    }

    private void findViewById(View view){
        pager= view.findViewById(R.id.page);
        tabLayout= view.findViewById(R.id.tab_layout);
        settingIv = view.findViewById(R.id.iv1);
        usernameTv = view.findViewById(R.id.me_homepage_username);
    }

    private void initView(){
        settingIv.setOnClickListener(this);

        echo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv1:
                Intent intent=new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void echo(){
        usernameTv.setText(Constants.USER.getUsername());
    }
}
