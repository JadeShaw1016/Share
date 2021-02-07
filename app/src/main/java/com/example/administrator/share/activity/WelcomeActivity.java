package com.example.administrator.share.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.User;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;


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
                Map<String, String> map = SharedPreferencesUtils.getUserInfo(mContext);//获取用户名密码
                checkUser(map);
            }
        }, time);

        //从必应每日一图获取图片并显示
//        welcomeIv = findViewById(R.id.iv_welcome);
//        data=new DataResource();
//        data.getData();
//        welcomeIv.setImageBitmap((Bitmap)data.getList().get(0).get("pic"));
    }

    /**
     * 回显
     */
    private void checkUser(Map<String, String> map) {
        final String username = map.get("username");
        final String password = map.get("password");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "User?method=login";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("username", username)
                        .addParams("password", password)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onResponse(String response, int id) {
                                Gson gson = new Gson();
                                User user = gson.fromJson(response, User.class);
                                if (user.getUserId() == 0) {
                                    openActivity(LoginActivity.class);
                                    Log.d("WelcomeActivity","找不到用户ID");
                                } else {
                                    // 存储用户
                                    Constants.USER = user;
                                    openActivity(MainMenuActivity.class);
                                    Log.d("WelcomeActivity","登录成功");
                                }
                                finish();
                            }
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                DisplayToast("网络链接出错！");
                            }
                        });
            }
        }).start();
    }
}
