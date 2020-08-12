package com.example.administrator.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.administrator.share.R;


public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Integer time = 2000;
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
