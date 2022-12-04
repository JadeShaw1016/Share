package com.example.administrator.share.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.util.ActivityManager;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.SharedPreferencesUtils;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backIv;
    private Button exitBtn;
    private LinearLayout myInfoLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        backIv =  findViewById(R.id.title_back);
        exitBtn = findViewById(R.id.btn_exit);
        myInfoLl = findViewById(R.id.ll_myinfo);
    }

    @Override
    protected void initView() {
        backIv.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
        myInfoLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_exit:
                SystemClock.sleep(500);
                ActivityManager.getInstance().killAllActivity();
                SharedPreferencesUtils.clear(this);
                Constants.FACEIMAGE = null;
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.ll_myinfo:
                startActivity(new Intent(this, MyInformationActivity.class));
                break;
        }
    }
}
