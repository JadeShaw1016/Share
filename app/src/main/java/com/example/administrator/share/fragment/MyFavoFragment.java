package com.example.administrator.share.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.administrator.share.R;


public class MyFavoFragment extends Fragment {

    public static Fragment newInstance(String title){
        MyFavoFragment fragmentOne = new MyFavoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", title);
        //fragment保存参数，传入一个Bundle对象
        fragmentOne.setArguments(bundle);
        return fragmentOne;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_myfavo,container,false);
        return view;
    }
}
