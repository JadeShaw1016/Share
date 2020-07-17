package com.example.administrator.share.fragment;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.FragmentAdapter;
import com.example.administrator.share.view.SettingActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MeFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager pager;
    private FragmentAdapter fragmentAdapter;
    private List<Fragment> fragmentList;
    private List<String> mTitles;
    private String [] title={"我的作品","我的点赞","我的收藏","我的草稿"};

    public static MeFragment newInstance(String param1) {
        MeFragment fragment = new MeFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public MeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        pager= view.findViewById(R.id.page);
        tabLayout= view.findViewById(R.id.tab_layout);

        view.findViewById(R.id.iv1).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        fragmentList=new ArrayList<>();
        mTitles=new ArrayList<>();
//        for(String t:title){
//            mTitles.add(t);
//        }
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





}
