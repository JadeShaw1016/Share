package com.example.administrator.share.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;

public class FruitActivity extends BaseActivity implements View.OnClickListener{

    private ImageView contentIv;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView fruitImageView;
    private TextView fruitContentText;
    private ActionBar actionBar;
    private String fruitName;
    private Uri uri;
    private Dialog dialog;
    private ImageView dialogIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit);
        Intent intent = getIntent();
        fruitName = intent.getStringExtra("text");
        uri = intent.getParcelableExtra("uri");
        findViewById();
        initView();
    }

    protected void findViewById(){
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        fruitImageView = findViewById(R.id.fruit_image_view);
        contentIv = findViewById(R.id.fruit_image_view2);
        fruitContentText = findViewById(R.id.fruit_content_text);
        dialogIv = new ImageView(this);
    }

    protected void initView(){
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle(fruitName);
        Glide.with(this).load(uri).into(fruitImageView);
        Glide.with(this).load(uri).into(contentIv);
        Glide.with(this).load(uri).into(dialogIv);
        fruitContentText.setText(fruitName);
        //大图所依附的dialog
        dialog = new Dialog(this, R.style.MyDialogStyle_fullScreen_black);
        dialog.setContentView(dialogIv);
        contentIv.setOnClickListener(this);
        dialogIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fruit_image_view2:
                dialog.show();
                break;
        }
    }
}
