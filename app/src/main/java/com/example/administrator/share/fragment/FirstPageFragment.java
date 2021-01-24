package com.example.administrator.share.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.FirstPageListAdapter;
import com.example.administrator.share.adapter.FirstPageListAdapter2;
import com.example.administrator.share.entity.Fruit;
import com.example.administrator.share.util.DataResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FirstPageFragment extends Fragment{

    private Spinner spinner;
    private List<Map<String,Object>> mList;
    private DataResource data;
    private FirstPageListAdapter adapter;
    private FirstPageListAdapter2 adapter2;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private List<Fruit> fruitList = new ArrayList<>();

    private Fruit[] fruits = {new Fruit("Apple", R.drawable.apple), new Fruit("Banana", R.drawable.banana),
            new Fruit("Orange", R.drawable.orange), new Fruit("Watermelon", R.drawable.watermelon),
            new Fruit("Pear", R.drawable.pear), new Fruit("Grape", R.drawable.grape),
            new Fruit("Pineapple", R.drawable.pineapple), new Fruit("Strawberry", R.drawable.strawberry),
            new Fruit("Cherry", R.drawable.cherry), new Fruit("Mango", R.drawable.mango)};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firstpage, null);
        initFruits();
        findViewById(view);
        setAdapter();
        return view;
    }

    private void findViewById(View view){
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Data deleted", Snackbar.LENGTH_SHORT)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity(), "Data restored", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FirstPageListAdapter(fruitList);
        recyclerView.setAdapter(adapter);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.fuxk_base_color_cyan);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });

        spinner = view.findViewById(R.id.spinner);
        mList=new ArrayList<>();
        data=new DataResource();
    }


    private void initFruits() {
        fruitList.clear();
        for (int i = 0; i < 50; i++) {
            Random random = new Random();
            int index = random.nextInt(fruits.length);
            fruitList.add(fruits[index]);
        }
    }

    private void refreshFruits() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initFruits();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void initData(){
        data.getData();
        mList.addAll(data.getList());
        adapter2.notifyDataSetChanged();
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
        spinner.setAdapter(arr_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(position == 1) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    adapter2=new FirstPageListAdapter2(getActivity().getBaseContext(),mList,data);
                    recyclerView.setAdapter(adapter2);
                    initData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
