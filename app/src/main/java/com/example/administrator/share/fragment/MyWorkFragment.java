package com.example.administrator.share.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.share.R;


public class MyWorkFragment extends Fragment {
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
        return view;
    }
}
