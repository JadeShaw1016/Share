package com.example.administrator.share.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.FirstPageListAdapter0;
import com.example.administrator.share.adapter.FirstPageListAdapter2;
import com.example.administrator.share.entity.CircleListForFound;
import com.example.administrator.share.util.BingPic;
import com.example.administrator.share.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import okhttp3.Call;

import static android.content.ContentValues.TAG;

public class FirstPageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private Context mContext;
    private TextView titleText;
    private Spinner spinner;
    private List<Map<String,String>> mList;
    private FirstPageListAdapter0 adapter0;
    private FirstPageListAdapter2 adapter2;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayoutManager layoutManager;
    private int POSITION = 0;
    private List<CircleListForFound> mCircleList;
    private final int PAGE_COUNT = 5;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int lastVisibleItem = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firstpage, null);
        findViewById(view);
        initView();
        setAdapter();
        return view;
    }

    private void findViewById(View view){
        titleText = view.findViewById(R.id.titleText);
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        spinner = view.findViewById(R.id.spinner);
        mList=new ArrayList<>();
    }

    private void initView(){
        mContext = getActivity();
        titleText.setText("每日精选");
        spinner.setVisibility(View.VISIBLE);
        swipeRefresh.setColorSchemeResources(R.color.fuxk_base_color_cyan);
        swipeRefresh.setOnRefreshListener(this);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(POSITION == 0 || POSITION == 1){
                        if (!adapter0.isFadeTips() && lastVisibleItem + 1 == adapter0.getItemCount()) {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    updateRecyclerView(adapter0.getRealLastPosition(), adapter0.getRealLastPosition() + PAGE_COUNT);
                                }
                            }, 500);
                        }

                        if (adapter0.isFadeTips() && lastVisibleItem + 2 == adapter0.getItemCount()) {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    updateRecyclerView(adapter0.getRealLastPosition(), adapter0.getRealLastPosition() + PAGE_COUNT);
                                }
                            }, 500);
                        }
                    }
                    else{
                        if (!adapter2.isFadeTips() && lastVisibleItem + 1 == adapter2.getItemCount()) {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    updateRecyclerView2(adapter2.getRealLastPosition(), adapter2.getRealLastPosition() + PAGE_COUNT);
                                }
                            }, 500);
                        }

                        if (adapter2.isFadeTips() && lastVisibleItem + 2 == adapter2.getItemCount()) {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    updateRecyclerView2(adapter2.getRealLastPosition(), adapter2.getRealLastPosition() + PAGE_COUNT);
                                }
                            }, 500);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        getSelectedCircles();
    }

    @Override
    public void onRefresh() {
        switch (POSITION){
            case 0:
                getSelectedCircles();
                break;
            case 1:
                getMyFocusCircles();
                break;
            case 2:
                swipeRefresh.setRefreshing(false);
                break;
        }
    }

    private void initData() throws ExecutionException, InterruptedException {
        DataAsyncTask myTask  = new DataAsyncTask();
        List<Map<String, String>> list = new ArrayList<>(myTask.executeOnExecutor(Executors.newCachedThreadPool()).get());
        mList.addAll(list);
        adapter2=new FirstPageListAdapter2(getActivity(),getDatas2(0, PAGE_COUNT),getDatas2(0, PAGE_COUNT).size()>0);
        adapter2.notifyDataSetChanged();
    }

    private void setAdapter(){
        //数据
        List<String> data_list = new ArrayList<>();
        data_list.add("   每日精选   ");
        data_list.add("   我的关注   ");
        data_list.add("   干货分享   ");

        //适配器
        ArrayAdapter<String> arr_adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()).getBaseContext(), android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position){
                    case 0:
                        titleText.setText("每日精选");
                        POSITION = 0;
                        getSelectedCircles();
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter0);
                        break;
                    case 1:
                        titleText.setText("我的关注");
                        POSITION = 1;
                        getMyFocusCircles();
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter0);
                        break;
                    case 2:
                        titleText.setText("干货分享");
                        POSITION = 2;
                        if(mList.isEmpty()){
                            try {
                                initData();
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            adapter2=new FirstPageListAdapter2(getActivity(),getDatas2(0, PAGE_COUNT),getDatas2(0, PAGE_COUNT).size()>0);
                            adapter2.notifyDataSetChanged();
                        }
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter2);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    static class DataAsyncTask extends AsyncTask<Void,Void,List<Map<String,String>>> {

        private List<Map<String,String>> list;

        DataAsyncTask(){
            super();
            list = new ArrayList<>();
        }

        @Override
        protected List<Map<String,String>> doInBackground(Void... params) {
            try{
                for(int count = 0;count<14;count+=7){
                    String path = "http://www.bing.com/HPImageArchive.aspx?format=js&idx="+count+"&n=7";
                    URL url = new URL(path);
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setConnectTimeout(5000);
                    urlConn.connect();
                    if (urlConn.getResponseCode() == 200) {
                        int i;
                        String data = readStream(urlConn.getInputStream());
                        Type type= new TypeToken<BingPic>(){}.getType();
                        Gson gson = new Gson();
                        BingPic bingPic = gson.fromJson(data,type);
                        for(i=0;i<7;i++){
                            Map<String,String> map;
                            map=new HashMap<>();
                            map.put("picUri","http://cn.bing.com"+bingPic.getImages().get(i).getUrl());
                            map.put("text",bingPic.getImages().get(i).getCopyright());
                            map.put("time",bingPic.getImages().get(i).getEnddate());
                            list.add(map);
                        }
                        Log.i(TAG, "请求成功");
                    } else {
                        Log.i(TAG, "请求失败");
                    }
                    urlConn.disconnect();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return list;
        }

        private String readStream(InputStream inputStream) throws IOException{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while((len=inputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,len);
                outputStream.flush();
            }
            outputStream.close();
            inputStream.close();
            return  outputStream.toString();
        }
    }

    private void getSelectedCircles() {
        new AsyncTask<Void,Void,Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "News?method=getNewsListWithLabel";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("label","精选")
                        .build()
                        .execute(new MyStringCallback());
                return null;
            }
        }.execute();
    }

    private void getMyFocusCircles() {
        new AsyncTask<Void,Void,Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "News?method=getMyFocusNewsList";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("userId",String.valueOf(Constants.USER.getUserId()))
                        .build()
                        .execute(new MyStringCallback());
                return null;
            }
        }.execute();
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            try {
                Type type = new TypeToken<ArrayList<CircleListForFound>>() {}.getType();
                mCircleList = gson.fromJson(response, type);
            } catch (Exception e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                mCircleList = null;
            }
            switch (id) {
                case 1:
                    if (mCircleList != null && mCircleList.size() > 0) {
                        adapter0 = new FirstPageListAdapter0(mContext, getDatas(0, PAGE_COUNT),getDatas(0, PAGE_COUNT).size()>0);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter0);
                        swipeRefresh.setRefreshing(false);
                    }
                    break;
                default:
                    Toast.makeText(mContext, "what！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            Toast.makeText(mContext, "网络链接出错！", Toast.LENGTH_SHORT).show();
        }
    }

    private List<CircleListForFound> getDatas(final int firstIndex, final int lastIndex) {
        List<CircleListForFound> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < mCircleList.size()) {
                resList.add(mCircleList.get(i));
            }
        }
        return resList;
    }

    private void updateRecyclerView(int fromIndex, int toIndex) {
        List<CircleListForFound> newDatas = getDatas(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            adapter0.updateList(newDatas,true);
        } else {
            adapter0.updateList(null, false);
        }
    }

    private List<Map<String,String>> getDatas2(final int firstIndex, final int lastIndex) {
        List<Map<String,String>> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < mList.size()) {
                resList.add(mList.get(i));
            }
        }
        return resList;
    }

    private void updateRecyclerView2(int fromIndex, int toIndex) {
        List<Map<String,String>> newDatas = getDatas2(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            adapter2.updateList(newDatas,true);
        } else {
            adapter2.updateList(null, false);
        }
    }
}
