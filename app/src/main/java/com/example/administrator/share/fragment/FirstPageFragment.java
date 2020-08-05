package com.example.administrator.share.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.FirstPageDetailActivity;
import com.example.administrator.share.adapter.FirstPageListAdapter;
import com.example.administrator.share.util.DataResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FirstPageFragment extends Fragment implements AdapterView.OnItemClickListener{
    private Spinner spinner;
    private ListView mListView;
    private List<Map<String,Object>> mList;
    private DataResource data;
    private FirstPageListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firstpage, null);
        findViewById(view);
        initView();
        initData();
        setAdapter();
        return view;
    }

    private void findViewById(View view){
        mListView=view.findViewById(R.id.list);
        spinner = view.findViewById(R.id.spinner);
        mList=new ArrayList<>();
        data=new DataResource();
        mAdapter=new FirstPageListAdapter(getActivity().getBaseContext(),mList,data);
        mListView.setAdapter(mAdapter);

    }

    private void initView(){
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), FirstPageDetailActivity.class);
        intent.putExtra("position", position);
        System.out.println("位置："+position+" id："+id);
        startActivity(intent);
    }

    private void initData(){
        data.getData();
        mList.addAll(data.getList());
        mAdapter.notifyDataSetChanged();
    }

    private void setAdapter(){
        //数据
        List<String> data_list = new ArrayList<>();
        data_list.add("   我的关注   ");
        data_list.add("   干货分享   ");

        //适配器
        ArrayAdapter<String> arr_adapter = new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
//        spinner.getChildAt(-1).setVisibility(view.INVISIBLE);
        System.out.println(spinner.getSelectedItemPosition());
        spinner.setAdapter(arr_adapter);
    }

}
