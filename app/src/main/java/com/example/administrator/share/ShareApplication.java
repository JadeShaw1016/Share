package com.example.administrator.share;

import android.app.Application;
import android.content.Context;


public class ShareApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        xcrash.XCrash.init(this);
    }
}
