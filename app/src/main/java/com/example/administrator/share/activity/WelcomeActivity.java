package com.example.administrator.share.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.User;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.SharedPreferencesUtils;


public class WelcomeActivity extends BaseActivity {

    private Context mContext;

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
        mContext = this;
        int time = 2000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                User user = SharedPreferencesUtils.getUserInfo(mContext);//获取用户名密码
                if (user == null) {
                    openActivity(LoginActivity.class);
                    Log.d("WelcomeActivity", "用户存储为空");
                    finish();
                } else {
                    if (!TextUtils.isEmpty(user.getUsername()) && !TextUtils.isEmpty(user.getPassword())) {
                        Constants.USER = user;
                        boolean result = SharedPreferencesUtils.saveUserInfo(mContext, user);
                        if (result) {
                            Log.d("WelcomeActivity", "登录成功");
                        } else {
                            DisplayToast("用户存储失败");
                        }
                        openActivity(MainMenuActivity.class);
                        finish();
                    }
                }
            }
        }, time);

        //从必应每日一图获取图片并显示
//        welcomeIv = findViewById(R.id.iv_welcome);
//        data=new DataResource();
//        data.getData();
//        welcomeIv.setImageBitmap((Bitmap)data.getList().get(0).get("pic"));
    }
}
