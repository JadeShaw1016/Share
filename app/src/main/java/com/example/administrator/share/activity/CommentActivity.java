package com.example.administrator.share.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.CommentListAdapter;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.CommentListItem;
import com.example.administrator.share.entity.CommonListItem;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.Utils;
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

public class CommentActivity extends BaseActivity implements View.OnClickListener{

    private TextView titleText;
    private RefreshLayout refreshLayout;
    private RecyclerView mListView;
    private ImageView commentRemindIv;
    private TextView msgRemindTv;
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private FrameLayout messageLl;
    private View title_back;
    private List<CommonListItem> mList;
    private CommentListAdapter adapter;
    private final int PAGE_COUNT = 10;
    private LinearLayout commentPane;
    private EditText addCommentET;
    private ImageView addCommentIV;
    private int newsId;
    private String replyUsername;
    private boolean isShowCommentPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_normal_list);
        findViewById();
        initView();
        refreshListener();
    }

    @Override
    protected void findViewById(){
        titleText = $(R.id.titleText);
        title_back = $(R.id.title_back);
        mListView = $(R.id.normal_list_lv);
        refreshLayout = $(R.id.refreshLayout);
        commentRemindIv = $(R.id.iv_normal_list_remind);
        msgRemindTv = $(R.id.tv_msg_remind);
        messageLl = $(R.id.layout_message);
        commentPane = $(R.id.ll_commment_pane);
        addCommentET = $(R.id.ll_add_commment_text);
        addCommentIV = $(R.id.iv_add_commment_btn);
    }

    @Override
    protected void initView(){
        mContext = this;
        titleText.setText("评论");
        title_back.setOnClickListener(this);
        addCommentIV.setOnClickListener(this);
        layoutManager = new LinearLayoutManager(this);
        messageLl.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.iv_add_commment_btn:
                addNewComment();
                break;
        }
    }

    /**
     * 获取评论
     */
    private void getComments() {
        new AsyncTask<Void,Void,Integer>(){

            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "Message?method=getCommentsList";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("authorName", Constants.USER.getNickname())
                        .build()
                        .execute(new MyStringCallback());
                return null;
            }
        }.execute();
    }

    // 添加新评论
    private void addNewComment() {
        String commentText = addCommentET.getText().toString().trim();
        if (TextUtils.isEmpty(commentText)) {
            DisplayToast("请先输入内容");
            return;
        }
        String url = Constants.BASE_URL + "Message?method=addNewComment";
        OkHttpUtils
                .post()
                .url(url)
                .id(2)
                .addParams("newsId", String.valueOf(newsId))
                .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                .addParams("authorName",Constants.USER.getUsername())
                .addParams("comment", commentText)
                .addParams("replyUser", replyUsername)
                .addParams("commentTime", Utils.getCurrentDatetime())
                .build()
                .execute(new MyStringCallback());
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            switch (id) {
                case 1:
                    Type type = new TypeToken<ArrayList<CommonListItem>>() {}.getType();
                    mList = gson.fromJson(response, type);
                    if(mContext != null){
                        if (mList.size() == 0) {
                            commentRemindIv.setVisibility(View.VISIBLE);
                            msgRemindTv.setVisibility(View.VISIBLE);
                        } else {
                            commentRemindIv.setVisibility(View.INVISIBLE);
                            msgRemindTv.setVisibility(View.INVISIBLE);
                        }
                        // 存储用户
                        adapter = new CommentListAdapter(mContext, getDatas(0, PAGE_COUNT));
                        mListView.setLayoutManager(layoutManager);
                        mListView.setAdapter(adapter);
                        adapter.setOnCommentButtonClickListner(new CommentListAdapter.OnCommentButtonClickListner() {
                            @Override
                            public void OnCommentButtonClicked(CommonListItem commonListItem) {
                                showCommemtPane(commonListItem);
                            }
                        });
                    }
                    break;
                case 2:
                    if (response.contains("error")) {
                        DisplayToast("请稍后再试..");
                    } else {
                        DisplayToast(response);
                        hideKeyboard();
                        CommentListItem commentListItem = new CommentListItem();
                        commentListItem.setFace(Constants.USER.getFace());
                        commentListItem.setCommentTime(Utils.getCurrentDatetime());
                        commentListItem.setComment(addCommentET.getText().toString());
                        commentListItem.setReplyUser(replyUsername);
                        commentListItem.setNickname(Constants.USER.getNickname());
                        commentListItem.setAuthorname(Constants.USER.getNickname());
                        commentPane.setVisibility(View.GONE);
                    }
                    break;
                default:
                    DisplayToast("what?");
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            DisplayToast("网络链接出错!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getComments();
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

    private void showCommemtPane(CommonListItem commonListItem) {
        isShowCommentPane = !isShowCommentPane;
        if (isShowCommentPane) {
            newsId = commonListItem.getNewsId();
            replyUsername = commonListItem.getNickname();
            commentPane.setVisibility(View.VISIBLE);
            addCommentET.setHint("回复 " + replyUsername + " 的评论");
            showKeyboard(addCommentET);
        } else {
            commentPane.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(addCommentET.getWindowToken(), 0);
        }
    }

}