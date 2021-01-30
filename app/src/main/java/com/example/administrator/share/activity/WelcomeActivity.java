package com.example.administrator.share.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;


public class WelcomeActivity extends BaseActivity {

    @Override
    protected void findViewById() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        int time = 2000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finish();
            }
        }, time);

        //从必应每日一图获取图片并显示
//        welcomeIv = findViewById(R.id.iv_welcome);
//        data=new DataResource();
//        data.getData();
//        welcomeIv.setImageBitmap((Bitmap)data.getList().get(0).get("pic"));
    }


}
