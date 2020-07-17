package com.example.administrator.share.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.administrator.share.R;

public class RecordActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature( Window.FEATURE_NO_TITLE );//去掉标题栏，不明白删掉跑一遍就明白了
        setContentView(R.layout.activity_record);
//        findViewById( R.id.but ).setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();//销毁
//            }
//        } );
    }
}
