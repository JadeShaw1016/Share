package com.example.administrator.share.activity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.util.DataResource;

public class FirstPageDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backIv;
    private ImageView imageIv;
    private TextView descriptionTv;
    private TextView timeTv;

    private int position;
    private DataResource data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getIntent().getIntExtra("position", 0);
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
        data=new DataResource();
    }

    @Override
    protected void initView() {
        backIv.setOnClickListener(this);
        data.getData();
        imageIv.setImageBitmap((Bitmap) data.getList().get(position).get("pic"));
        descriptionTv.setText((CharSequence) data.getList().get(position).get("text"));
        String time = (String) data.getList().get(position).get("time");
        String formattime = time.substring(0,4)+"-"+time.substring(4,6)+"-"+time.substring(6,8);
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
