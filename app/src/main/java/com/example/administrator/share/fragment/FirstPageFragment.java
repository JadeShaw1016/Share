package com.example.administrator.share.fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.FirstPageListAdapter0;
import com.example.administrator.share.adapter.FirstPageListAdapter2;
import com.example.administrator.share.entity.CircleList;
import com.example.administrator.share.util.AppBarStateChangeListener;
import com.example.administrator.share.util.BingPic;
import com.example.administrator.share.util.Constants;
import com.google.android.material.appbar.AppBarLayout;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import okhttp3.Call;

public class FirstPageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context mContext;
    private List<Map<String, String>> mList;
    private FirstPageListAdapter0 adapter0;
    private FirstPageListAdapter2 adapter2;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayoutManager layoutManager;
    private int POSITION = 0;
    private List<CircleList> mCircleList;
    private final int PAGE_COUNT = 5;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private int lastVisibleItem = 0;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private SearchView searchView;
    private ImageView firstPageRemindIv;
    private TextView firstPageRemindTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firstpage, null);
        setHasOptionsMenu(true);
        findViewById(view);
        initView();
        searchListener();
        recyclerViewListener();
        appBarLayoutListener();
        return view;
    }

    private void findViewById(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        appBarLayout = view.findViewById(R.id.appbar_firstpage);
        searchView = view.findViewById(R.id.sv_firstpage);
        firstPageRemindIv = view.findViewById(R.id.iv_fristpage_remind);
        firstPageRemindTv = view.findViewById(R.id.tv_fristpage_remind);
    }

    private void initView() {
        mList = new ArrayList<>();
        mContext = getActivity();
        toolbar.setTitle("每日精选");
        swipeRefresh.setColorSchemeResources(R.color.fuxk_base_color_cyan);
        swipeRefresh.setOnRefreshListener(this);
        searchView.setSubmitButtonEnabled(true);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter0 = new FirstPageListAdapter0(mContext, new ArrayList<CircleList>(), false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter0);
        getSelectedCircles();
    }

    @Override
    public void onRefresh() {
        switch (POSITION) {
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
        DataAsyncTask myTask = new DataAsyncTask();
        List<Map<String, String>> list = new ArrayList<>(myTask.executeOnExecutor(Executors.newCachedThreadPool()).get());
        mList.addAll(list);
        adapter2 = new FirstPageListAdapter2(getActivity(), getDatas2(0, PAGE_COUNT), getDatas2(0, PAGE_COUNT).size() > 0);
        adapter2.notifyDataSetChanged();
    }

    private void recyclerViewListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (POSITION == 0 || POSITION == 1) {
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
                    } else {
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
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void searchListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                getSearchCircles(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }

    private void appBarLayoutListener() {
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    searchView.setVisibility(View.INVISIBLE);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    searchView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
        inflater.inflate(R.menu.toolbar, menu);
        menu.findItem(R.id.meirijingxuan).setVisible(true);
        menu.findItem(R.id.myfocus).setVisible(true);
        menu.findItem(R.id.ganhuofenxiang).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.meirijingxuan:
                toolbar.setTitle("每日精选");
                POSITION = 0;
                getSelectedCircles();
                recyclerView.setAdapter(adapter0);
                break;
            case R.id.myfocus:
                toolbar.setTitle("我的关注");
                POSITION = 1;
                getMyFocusCircles();
                recyclerView.setAdapter(adapter0);
                break;
            case R.id.ganhuofenxiang:
                toolbar.setTitle("干货分享");
                POSITION = 2;
                if (mList.isEmpty()) {
                    try {
                        firstPageRemindIv.setVisibility(View.INVISIBLE);
                        firstPageRemindTv.setVisibility(View.INVISIBLE);
                        initData();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    firstPageRemindIv.setVisibility(View.INVISIBLE);
                    firstPageRemindTv.setVisibility(View.INVISIBLE);
                    adapter2 = new FirstPageListAdapter2(getActivity(), getDatas2(0, PAGE_COUNT), getDatas2(0, PAGE_COUNT).size() > 0);
                    adapter2.notifyDataSetChanged();
                }
                recyclerView.setAdapter(adapter2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    static class DataAsyncTask extends AsyncTask<Void, Void, List<Map<String, String>>> {

        private List<Map<String, String>> list;

        DataAsyncTask() {
            super();
            list = new ArrayList<>();
        }

        @Override
        protected List<Map<String, String>> doInBackground(Void... params) {
            try {
                for (int count = 0; count < 14; count += 7) {
                    String path = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=" + count + "&n=7";
                    URL url = new URL(path);
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setConnectTimeout(5000);
                    urlConn.connect();
                    if (urlConn.getResponseCode() == 200) {
                        int i;
                        String data = readStream(urlConn.getInputStream());
                        Type type = new TypeToken<BingPic>() {
                        }.getType();
                        Gson gson = new Gson();
                        BingPic bingPic = gson.fromJson(data, type);
                        for (i = 0; i < 7; i++) {
                            Map<String, String> map;
                            map = new HashMap<>();
                            map.put("picUri", "http://cn.bing.com" + bingPic.getImages().get(i).getUrl());
                            map.put("text", bingPic.getImages().get(i).getCopyright());
                            map.put("time", bingPic.getImages().get(i).getEnddate());
                            list.add(map);
                        }
                        Log.i(TAG, "请求成功");
                    } else {
                        Log.i(TAG, "请求失败");
                    }
                    urlConn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        private String readStream(InputStream inputStream) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            inputStream.close();
            return outputStream.toString();
        }
    }

    private void getSelectedCircles() {
        swipeRefresh.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "circle/getCircleListWithLabel";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(1)
                        .addParams("label", "精选")
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    private void getMyFocusCircles() {
        swipeRefresh.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "circle/getMyFocusCircleList";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(1)
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    private void getSearchCircles(final String text) {
        swipeRefresh.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "circle/getSearchCircleList";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(2)
                        .addParams("searchContent", "%" + text + "%")
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            if (Constants.ERROR.equals(response)) {
                mCircleList = null;
            } else {
                try {
                    mCircleList = new Gson().fromJson(response, new TypeToken<ArrayList<CircleList>>() {
                    }.getType());
                } catch (Exception e) {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            switch (id) {
                case 1:
                    if (mCircleList == null || mCircleList.size() == 0) {
                        adapter0 = new FirstPageListAdapter0(mContext, new ArrayList<CircleList>(), false);
                        recyclerView.setAdapter(adapter0);
                        firstPageRemindIv.setVisibility(View.VISIBLE);
                        firstPageRemindTv.setVisibility(View.VISIBLE);
                    } else {
                        setAdapter();
                    }
                    break;
                case 2:
                    if (mCircleList != null && mCircleList.size() > 0) {
                        setAdapter();
                    } else {
                        Toast.makeText(mContext, "暂时没有搜索到相关的内容", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(mContext, "what！", Toast.LENGTH_SHORT).show();
                    break;
            }
            swipeRefresh.setRefreshing(false);
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            adapter0 = new FirstPageListAdapter0(mContext, new ArrayList<CircleList>(), false);
            recyclerView.setAdapter(adapter0);
            firstPageRemindIv.setImageResource(R.drawable.default_remind_nosignal);
            firstPageRemindTv.setText(R.string.no_network_remind);
            firstPageRemindIv.setVisibility(View.VISIBLE);
            firstPageRemindTv.setVisibility(View.VISIBLE);
            swipeRefresh.setRefreshing(false);
            Toast.makeText(mContext, "FirstPageFragment网络链接出错！" + arg1, Toast.LENGTH_SHORT).show();
        }
    }

    private void setAdapter() {
        firstPageRemindIv.setVisibility(View.INVISIBLE);
        firstPageRemindTv.setVisibility(View.INVISIBLE);
        adapter0 = new FirstPageListAdapter0(mContext, getDatas(0, PAGE_COUNT), getDatas(0, PAGE_COUNT).size() > 0);
        recyclerView.setAdapter(adapter0);
    }

    private List<CircleList> getDatas(final int firstIndex, final int lastIndex) {
        List<CircleList> resList = new ArrayList<>();
        if (mCircleList != null && !mCircleList.isEmpty()) {
            for (int i = firstIndex; i < lastIndex; i++) {
                if (i < mCircleList.size()) {
                    resList.add(mCircleList.get(i));
                }
            }
        }
        return resList;
    }

    private void updateRecyclerView(int fromIndex, int toIndex) {
        List<CircleList> newDatas = getDatas(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            adapter0.updateList(newDatas, true);
        } else {
            adapter0.updateList(null, false);
        }
    }

    private List<Map<String, String>> getDatas2(final int firstIndex, final int lastIndex) {
        List<Map<String, String>> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < mList.size()) {
                resList.add(mList.get(i));
            }
        }
        return resList;
    }

    private void updateRecyclerView2(int fromIndex, int toIndex) {
        List<Map<String, String>> newDatas = getDatas2(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            adapter2.updateList(newDatas, true);
        } else {
            adapter2.updateList(null, false);
        }
    }
}
