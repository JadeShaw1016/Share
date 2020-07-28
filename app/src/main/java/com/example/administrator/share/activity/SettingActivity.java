package com.example.administrator.share.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.util.AppManager;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backIv;
    private Button exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        backIv = (ImageView) findViewById(R.id.title_back);
        exitBtn = (Button) findViewById(R.id.btn_exit);
    }

    @Override
    protected void initView() {
        backIv.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_exit:
                SystemClock.sleep(500);
                AppManager.getInstance().killAllActivity();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
