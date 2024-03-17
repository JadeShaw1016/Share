package com.example.administrator.share;

import android.app.Application;
import android.content.Context;


public class ShareApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        //获取Context
        context = getApplicationContext();
    }

    //返回
    public static Context getContextObject(){
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        xcrash.XCrash.init(this);
    }
}
