package com.example.administrator.share.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.MainMenuActivity;
import com.example.administrator.share.adapter.CollectionListAdapter;
import com.example.administrator.share.entity.CommonListItem;
import com.example.administrator.share.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.FalsifyHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class MyFavoFragment extends Fragment{

    private List<CommonListItem> mList;
    private RefreshLayout refreshLayout;
    private ImageView favRemindIv;
    private TextView favRemindTv;
    private RecyclerView mListView;
    private LinearLayoutManager layoutManager;
    private CollectionListAdapter adapter;
    private final int PAGE_COUNT = 10;
    private static String USERID;

    public static Fragment newInstance(String userId){
        USERID = userId;
        return new MyFavoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_normal_list,container,false);
        findViewById(view);
        initView();
        refreshListener();
        return view;
    }

    private void findViewById(View view){
        mListView = view.findViewById(R.id.normal_list_lv);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        favRemindIv = view.findViewById(R.id.iv_normal_list_remind_collect);
        favRemindTv = view.findViewById(R.id.tv_fav_remind);
    }

    private void initView(){
        layoutManager = new LinearLayoutManager(getActivity());
    }

    private void refreshListener(){
        refreshLayout.setEnableLoadmoreWhenContentNotFull(false);
        refreshLayout.setDisableContentWhenLoading(true);//是否在加载的时候禁止列表的操作
        refreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
        refreshLayout.setEnableAutoLoadmore(false);//是否启用列表惯性滑动到底部时自动加载更多
        //上拉加载
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                updateRecyclerView(adapter.getItemCount(), adapter.getItemCount() + PAGE_COUNT);
            }
        });
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new FalsifyHeader(context);
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }


    /**
     * 获取点赞
     */
    private void getFavors() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "GetCircleList?method=getFavorsList";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("userId", USERID)
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            switch (id) {
                case 1:
                    Type type = new TypeToken<ArrayList<CommonListItem>>() {}.getType();
                    if(!response.equals("error")){
                        mList = gson.fromJson(response, type);
                        if(getActivity() != null){
                            if (mList == null || mList.size() == 0) {
                                favRemindIv.setVisibility(View.VISIBLE);
                                favRemindTv.setVisibility(View.VISIBLE);
                            } else {
                                favRemindIv.setVisibility(View.INVISIBLE);
                                favRemindTv.setVisibility(View.INVISIBLE);
                            }
                            // 存储用户
                            adapter = new CollectionListAdapter(getActivity(), mList);
                            mListView.setLayoutManager(layoutManager);
                            mListView.setAdapter(adapter);
                            refreshLayout.finishRefresh();
                        }
                    }
                    break;

                default:
                    Toast.makeText(MainMenuActivity.mContext, "What?", Toast.LENGTH_SHORT).show();
                    break;
            }

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            Toast.makeText(getActivity(), "网络链接出错!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getFavors();
    }

    private List<CommonListItem> getDatas(final int firstIndex, final int lastIndex) {
        List<CommonListItem> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < mList.size()) {
                resList.add(mList.get(i));
            }
        }
        return resList;
    }

    private void updateRecyclerView(int fromIndex, int toIndex) {
        List<CommonListItem> newDatas = getDatas(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            adapter.updateList(newDatas);
            refreshLayout.finishLoadmore();
        } else {
            refreshLayout.finishLoadmoreWithNoMoreData();
        }
    }
}
