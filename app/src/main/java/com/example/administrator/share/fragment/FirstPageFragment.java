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
import com.example.administrator.share.activity.DetailActivity;
import com.example.administrator.share.adapter.ListAdapter;
import com.example.administrator.share.util.DataResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FirstPageFragment extends Fragment {
    private Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;

    private ListView mListView;
    private List<Map<String,Object>> mList;
    private DataResource data;
    private ListAdapter mAdapter;

    public FirstPageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firstpage, null);
        initView(view);
        initData();
        Click();
        setAdapter();

        return view;
    }

    private void initView(View view){
        mListView=view.findViewById(R.id.list);
        spinner = view.findViewById(R.id.spinner);
        mList=new ArrayList<>();
        data=new DataResource();
        mAdapter=new ListAdapter(getActivity().getBaseContext(),mList,data);
        mListView.setAdapter(mAdapter);

    }

    private void Click(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Map map = mList.get(i);
//                Toast.makeText(getActivity(),map.get("time").toString(),
//                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData(){
        data.getData();
        mList.addAll(data.getList());
        mAdapter.notifyDataSetChanged();
    }

    private void setAdapter(){
        //数据
        data_list = new ArrayList<String>();
        data_list.add("   我的关注   ");
        data_list.add("   干货分享   ");

        //适配器
        arr_adapter= new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
//        spinner.getChildAt(-1).setVisibility(view.INVISIBLE);
        System.out.println(spinner.getSelectedItemPosition());
        spinner.setAdapter(arr_adapter);
    }
}
