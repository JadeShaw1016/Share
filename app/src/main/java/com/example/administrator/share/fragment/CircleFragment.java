package com.example.administrator.share.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.BeforeDateCheckActivity;


public class CircleFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{

    private ImageView calendarIv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle, container, false);
        findViewById(view);
        initView();

        return view;
    }

    private void findViewById(View view){
        calendarIv = view.findViewById(R.id.iv_record);
    }

    private void initView(){
        calendarIv.setOnClickListener(this);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

}
