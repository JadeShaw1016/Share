package com.example.administrator.share.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.FansListAdapter;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.FansListItem;

import java.util.List;

public class FansActivity extends BaseActivity implements View.OnClickListener{

    private TextView titleText;
    private TextView fansRemindTv;
    private RecyclerView mListView;
    private View title_back;
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private List<FansListItem> mFansList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFansList = getIntent().getParcelableArrayListExtra("mFansList");
        setContentView(R.layout.activity_fans_focus);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        mContext = this;
        titleText = $(R.id.titleText);
        title_back = $(R.id.title_back);
        mListView = findViewById(R.id.normal_list_lv);
        fansRemindTv = findViewById(R.id.tv_fans_remind);
    }

    @Override
    protected void initView() {
        titleText.setText("我的粉丝");
        title_back.setOnClickListener(this);
        layoutManager = new LinearLayoutManager(this);
        getFans();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
        }
    }

    /**
     * 获取粉丝数
     */
    private void getFans() {
        if (mFansList.size() == 0) {
            fansRemindTv.setVisibility(View.VISIBLE);
        } else {
            fansRemindTv.setVisibility(View.INVISIBLE);
        }
        // 存储用户
        FansListAdapter adapter = new FansListAdapter(mContext, mFansList,1);
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(adapter);
    }

}