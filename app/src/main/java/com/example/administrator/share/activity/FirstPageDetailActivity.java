package com.example.administrator.share.activity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;

public class FirstPageDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backIv;
    private ImageView imageIv;
    private TextView descriptionTv;
    private TextView timeTv;

    private Bitmap bitmap;
    private CharSequence charSequence;
    private String formattime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bitmap = getIntent().getParcelableExtra("bitmap");
        charSequence = getIntent().getCharSequenceExtra("charSequence");
        formattime = getIntent().getStringExtra("formattime");
        setContentView(R.layout.activity_firstpage_detail);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        backIv = (ImageView) findViewById(R.id.title_back);
        imageIv = (ImageView) findViewById(R.id.iv_1);
        descriptionTv = (TextView) findViewById(R.id.tv_description);
        timeTv = (TextView) findViewById(R.id.tv_time);
    }

    @Override
    protected void initView() {
        backIv.setOnClickListener(this);
        imageIv.setImageBitmap(bitmap);
        descriptionTv.setText(charSequence);
        timeTv.setText(formattime);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
        }
    }
}
