package com.example.administrator.share.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2019/7/9.
 */
public class FragmentAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList;//各导航的Fragment
    private final List<String> mTitle; //导航的标题


    public FragmentAdapter(FragmentManager fragmentManager, List<Fragment> fragments, List<String> title) {
        super(fragmentManager);
        mFragmentList = fragments;
        mTitle = title;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle.get(position);
    }
}
