package com.example.administrator.share.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.util.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

public class BeforeDateCheckActivity extends BaseActivity {

    private TextView recorddaysTv;

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
                SystemClock.sleep(2000);
                getCheckedList();
            }
        }.start();
    }


    private void getCheckedList() {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "DailyCheck?method=getCheckedList";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .build()
                        .execute(new MyStringCallback());
                return null;
            }
        }.execute();
    }

    private void getRecords() {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "DailyCheck?method=getHomepageTotalRecord";
                OkHttpUtils
                        .post()
                        .url(url)
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .id(2)
                        .build()
                        .execute(new MyStringCallback());
                return null;
            }
        }.execute();
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 1:
                    if (response.contains("error")) {
                        DisplayToast("暂时无法获取数据");
                    } else {
                        if (response.length() == 0) {
                            Constants.DAILYCHECKEDLIST = new ArrayList<>();
                            Constants.DAILYCHECKEDLIST.add("2000-1-1");
                        } else {
                            String[] dates = response.split(",");
                            if (Constants.DAILYCHECKEDLIST == null) {
                                Constants.DAILYCHECKEDLIST = new ArrayList<String>();
                            } else {
                                Constants.DAILYCHECKEDLIST.clear();
                            }
                            for (String s : dates) {
                                String[] split = s.split("-");
                                s = split[0] + "-" + removeHeadingZero(split[1]) + "-" + removeHeadingZero(split[2]);
                                Constants.DAILYCHECKEDLIST.add(s);
                            }
                        }
                        openActivity(DateCheckActivity.class);
                    }
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
            DisplayToast("BeforeDateCheckActivity网络链接出错！" + arg1);
        }
    }
}
