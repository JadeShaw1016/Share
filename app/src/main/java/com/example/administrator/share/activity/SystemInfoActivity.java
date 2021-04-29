package com.example.administrator.share.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;

public class SystemInfoActivity extends BaseActivity implements View.OnClickListener{

    private TextView titleText;
    private View title_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_info);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        titleText = $(R.id.titleText);
        title_back = $(R.id.title_back);
    }

    @Override
    protected void initView() {
        titleText.setText("系统通知");
        title_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
        }
    }
}