package com.example.administrator.share.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
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
import com.example.administrator.share.activity.MainMenuActivity;
import com.example.administrator.share.adapter.FirstPageListAdapter0;
import com.example.administrator.share.adapter.FirstPageListAdapter1;
import com.example.administrator.share.adapter.FirstPageListAdapter2;
import com.example.administrator.share.entity.CircleListForFound;
import com.example.administrator.share.entity.Fruit;
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
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import okhttp3.Call;

import static android.content.ContentValues.TAG;

public class FirstPageFragment extends Fragment implements View.OnClickListener{

    private Context mContext;
    private TextView titleText;
    private Spinner spinner;
    private List<Map<String,Object>> mList;
    private FirstPageListAdapter0 adapter0;
    private FirstPageListAdapter1 adapter1;
    private FirstPageListAdapter2 adapter2;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private List<Fruit> fruitList = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private LinearLayoutManager layoutManager2;
    private FloatingActionButton fab;
    private int POSITION = 0;

    private Fruit[] fruits = {new Fruit("Apple", R.drawable.apple), new Fruit("Banana", R.drawable.banana),
            new Fruit("Orange", R.drawable.orange), new Fruit("Watermelon", R.drawable.watermelon),
            new Fruit("Pear", R.drawable.pear), new Fruit("Grape", R.drawable.grape),
            new Fruit("Pineapple", R.drawable.pineapple), new Fruit("Strawberry", R.drawable.strawberry),
            new Fruit("Cherry", R.drawable.cherry), new Fruit("Mango", R.drawable.mango)};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firstpage, null);
        initFruits();
        findViewById(view);
        initView();
        setAdapter();
        return view;
    }

    private void findViewById(View view){
        titleText = view.findViewById(R.id.titleText);
        fab = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        spinner = view.findViewById(R.id.spinner);
        mList=new ArrayList<>();
    }

    private void initView(){
        mContext = getActivity();
        titleText.setText("每日精选");
        spinner.setVisibility(View.VISIBLE);
        fab.setOnClickListener(this);
        swipeRefresh.setColorSchemeResources(R.color.fuxk_base_color_cyan);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (POSITION){
                    case 0:
                        getSelectedCircles();
                        break;
                    case 1:
                        refreshFruits();
                        break;
                }
            }
        });
        layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager2 = new LinearLayoutManager(getActivity());
        getSelectedCircles();
    }

    private void initFruits() {
        fruitList.clear();
        for (int i = 0; i < 50; i++) {
            Random random = new Random();
            int index = random.nextInt(fruits.length);
            fruitList.add(fruits[index]);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                Snackbar.make(view, "Data deleted", Snackbar.LENGTH_SHORT)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity(), "Data restored", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                break;
        }
    }

    private void refreshFruits() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initFruits();
                        adapter1.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void initData() throws ExecutionException, InterruptedException {
        DataAsyncTask myTask  = new DataAsyncTask();;
        List<Map<String, Object>> list = new ArrayList<>(myTask.executeOnExecutor(Executors.newCachedThreadPool()).get());
        mList.addAll(list);
        adapter2=new FirstPageListAdapter2(getActivity(),mList);
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
                        recyclerView.setLayoutManager(layoutManager2);
                        recyclerView.setAdapter(adapter0);
                        break;
                    case 1:
                        titleText.setText("我的关注");
                        POSITION = 1;
                        adapter1 = new FirstPageListAdapter1(fruitList);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter1);
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
                            adapter2=new FirstPageListAdapter2(getActivity(),mList);
                            adapter2.notifyDataSetChanged();
                        }
                        recyclerView.setLayoutManager(layoutManager2);
                        recyclerView.setAdapter(adapter2);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    static class DataAsyncTask extends AsyncTask<Void,Void,List<Map<String,Object>>> {

        private List<Map<String,Object>> list;

        DataAsyncTask(){
            super();
            list = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainMenuActivity.mContext,"切换中",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected List<Map<String,Object>> doInBackground(Void... params) {
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
                            Map<String,Object> map;
                            map=new HashMap<>();
                            map.put("picUri","http://cn.bing.com"+bingPic.getImages().get(i).getUrl());
                            map.put("pic",getBitmap("http://cn.bing.com"+bingPic.getImages().get(i).getUrl()));
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

        private  Bitmap getBitmap(String path) throws IOException {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200){
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                conn.disconnect();
                return bitmap;
            }
            conn.disconnect();
            return null;
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
                        .addParams("label","jingxuan")
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
            List<CircleListForFound> mList;
            try {
                Type type = new TypeToken<ArrayList<CircleListForFound>>() {}.getType();
                mList = gson.fromJson(response, type);
            } catch (Exception e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                mList = null;
            }
            switch (id) {
                case 1:
                    if (mList != null && mList.size() > 0) {
                        adapter0 = new FirstPageListAdapter0(mContext, mList);
                        recyclerView.setLayoutManager(layoutManager2);
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
}
