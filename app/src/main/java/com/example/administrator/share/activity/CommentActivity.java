package com.example.administrator.share.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.CommentListAdapter;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.CommentMsgListItem;
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
    private List<CommentMsgListItem> mList;
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
     * 获取所有对我的评论消息
     */
    private void getComments() {
        new AsyncTask<Void,Void,Integer>(){

            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "comments/getCommentsList";
                OkHttpUtils
                        .get()
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
        String url = Constants.BASE_URL + "comments/addNewComment";
        OkHttpUtils
                .post()
                .url(url)
                .id(2)
                .addParams("newsId", String.valueOf(newsId))
                .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                .addParams("authorName",Constants.USER.getUsername())
                .addParams("comment", commentText)
                .addParams("replyUser", replyUsername)
                .build()
                .execute(new MyStringCallback());
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 1:
                    if (Constants.ERROR.equals(response)) {
                        mList = null;
                    } else {
                        try {
                            mList = new Gson().fromJson(response, new TypeToken<ArrayList<CommentMsgListItem>>() {
                            }.getType());
                        } catch (Exception e) {
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (mList == null || mList.isEmpty()) {
                        commentRemindIv.setVisibility(View.VISIBLE);
                        msgRemindTv.setVisibility(View.VISIBLE);
                    } else {
                        commentRemindIv.setVisibility(View.INVISIBLE);
                        msgRemindTv.setVisibility(View.INVISIBLE);
                        adapter = new CommentListAdapter(mContext, getDatas(0, PAGE_COUNT));
                        mListView.setLayoutManager(layoutManager);
                        mListView.setAdapter(adapter);
                        adapter.setOnCommentButtonClickListner(new CommentListAdapter.OnCommentButtonClickListner() {
                            @Override
                            public void OnCommentButtonClicked(CommentMsgListItem commentMsgListItem) {
                                showCommemtPane(commentMsgListItem);
                            }
                        });
                    }
                    break;
                case 2:
                    if (Constants.ERROR.equals(response)) {
                        DisplayToast("请稍后再试..");
                    } else {
                        DisplayToast("评论成功！");
                        hideKeyboard();
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
            commentRemindIv.setImageResource(R.drawable.default_remind_nosignal);
            msgRemindTv.setText(R.string.no_network_remind);
            commentRemindIv.setVisibility(View.VISIBLE);
            msgRemindTv.setVisibility(View.VISIBLE);
            DisplayToast("网络链接出错!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getComments();
    }

    private List<CommentMsgListItem> getDatas(final int firstIndex, final int lastIndex) {
        List<CommentMsgListItem> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < mList.size()) {
                resList.add(mList.get(i));
            }
        }
        return resList;
    }

    private void updateRecyclerView(int fromIndex, int toIndex) {
        List<CommentMsgListItem> newDatas = getDatas(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            adapter.updateList(newDatas);
            refreshLayout.finishLoadmore();
        } else {
            refreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    private void showCommemtPane(CommentMsgListItem commentMsgListItem) {
        isShowCommentPane = !isShowCommentPane;
        if (isShowCommentPane) {
            newsId = commentMsgListItem.getNewsId();
            replyUsername = commentMsgListItem.getNickname();
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