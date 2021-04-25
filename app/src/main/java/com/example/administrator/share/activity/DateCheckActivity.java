package com.example.administrator.share.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.MyDialogHandler;

import java.util.Calendar;

import cn.aigestudio.datepicker.bizs.calendars.DPCManager;
import cn.aigestudio.datepicker.bizs.decors.DPDecor;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

public class DateCheckActivity extends BaseActivity implements View.OnClickListener {

    private View title_back;
    private TextView titleText;

    private DatePicker picker;
    private Button btnPick;

    private MyDialogHandler uiFlusHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_date_check);
        findViewById();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        echoChecked();
    }

    @Override
    protected void findViewById() {
        this.title_back = $(R.id.title_back);
        this.titleText = $(R.id.titleText);
        picker = findViewById(R.id.date_date_picker);
        btnPick = findViewById(R.id.date_btn_check);
    }

    @Override
    protected void initView() {
        Context mContext = this;
        this.title_back.setOnClickListener(this);
        String TITLE_NAME = "每日打卡";
        this.titleText.setText(TITLE_NAME);
        btnPick.setOnClickListener(this);
        uiFlusHandler = new MyDialogHandler(mContext, "刷新数据...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.date_btn_check:
                Intent intent = new Intent(this,AddnewsActivity.class);
                intent.putExtra("index",1);
                startActivity(intent);
                break;
        }
    }

    /**
     * 已经打卡数据展示
     */
    public void echoChecked() {

        DPCManager.getInstance().setDecorTR(Constants.DAILYCHECKEDLIST);
        Calendar today = Calendar.getInstance();
        picker.setDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1);
        picker.setFestivalDisplay(false);
        picker.setTodayDisplay(true);
        picker.setMode(DPMode.NONE);
        picker.setDPDecor(new DPDecor() {

            @Override
            public void drawDecorTR(Canvas canvas, Rect rect, Paint paint, String data) {
                super.drawDecorTR(canvas, rect, paint, data);
//                BitmapDrawable bmpDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_location_checked);
//                Bitmap bmp = bmpDraw.getBitmap();
//                canvas.drawBitmap(bmp, rect.centerX()-8, rect.centerY(), paint);

                paint.setColor(Color.rgb(255,165,0));
                canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, paint);
            }

        });
    }

}
