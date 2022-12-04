package com.example.administrator.share.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.util.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;

public class BeforeDateCheckActivity extends BaseActivity {

    private TextView recorddaysTv;
    private static final ThreadPoolExecutor THREADPOOL = new ThreadPoolExecutor(2, 4, 3,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_before_date_check);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        recorddaysTv = findViewById(R.id.tv_record_days);
    }

    @Override
    protected void initView() {
        new Thread() {
            @Override
            public void run() {
                getRecords();
                SystemClock.sleep(1000);
                getCheckedList();
            }
        }.start();
    }


    private void getCheckedList() {
        THREADPOOL.execute(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "dailycheck/getCheckedList";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(1)
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .build()
                        .execute(new MyStringCallback());
            }
        });
    }

    private void getRecords() {
        THREADPOOL.execute(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "dailycheck/getTotalCheckRecord";
                OkHttpUtils
                        .get()
                        .url(url)
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .id(2)
                        .build()
                        .execute(new MyStringCallback());
            }
        });
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 1:
                    if (Constants.DAILYCHECKEDLIST == null) {
                        Constants.DAILYCHECKEDLIST = new ArrayList<>();
                    } else {
                        Constants.DAILYCHECKEDLIST.clear();
                    }
                    if (response.length() != 0) {
                        String[] dates = response.split(",");
                        for (String s : dates) {
                            String[] split = s.split("-");
                            s = split[0] + "-" + removeHeadingZero(split[1]) + "-" + removeHeadingZero(split[2]);
                            Constants.DAILYCHECKEDLIST.add(s);
                        }
                    }
                    openActivity(DateCheckActivity.class);
                    finish();
                    break;
                case 2:
                    recorddaysTv.setText("已打卡" + response + "天");
                    break;
            }
        }

        /**
         * 去除头部的0
         *
         * @param str
         * @return
         */
        public String removeHeadingZero(String str) {
            if (str.startsWith("0")) {
                return str.substring(1);
            } else {
                return str;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            DisplayToast("网络链接出错！" + arg1);
        }
    }
}
