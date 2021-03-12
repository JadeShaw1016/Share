package com.example.administrator.share.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.SplashAdapter;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends BaseActivity {

    private Button mBtn_skip;
    private Button mBtn_start;
    private ViewPager mVp_Guide;
    private View mGuideRedPoint;
    private LinearLayout mLlGuidePoints;

    private int disPoints;
    private int currentItem;
    private SplashAdapter adapter;
    private List<ImageView> guids;

    //向导界面的图片
    private int[] mPics = new int[]{R.drawable.start0,R.drawable.start1,R.drawable.start2,R.drawable.start3};

    boolean isFirstIn = false;
    private SharedPreferences.Editor edit;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sharedPreferences = getSharedPreferences("is_first_in_data", Context.MODE_PRIVATE);
        edit =  sharedPreferences.edit();
        isFirstIn = sharedPreferences.getBoolean("isFirstIn",true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFirstIn) {
                    setContentView(R.layout.activity_splash);
                    findViewById();
                    initView();
                    initData();
                    initEvent();
                    //之前错误没有这两句，没有设置Boolean类型，并提交
                    edit.putBoolean("isFirstIn",false);
                    edit.apply();
                } else {
                    Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 0);
        verifyStoragePermissions(this);
    }

    protected void findViewById() {
        mBtn_skip = (Button)findViewById(R.id.btn_skip);
        mBtn_start = (Button)findViewById(R.id.btn_start);
    }


    protected void initView() {
        mVp_Guide = (ViewPager) findViewById(R.id.vp_guide);
        mGuideRedPoint = findViewById(R.id.v_guide_redpoint);
        mLlGuidePoints = (LinearLayout) findViewById(R.id.ll_guide_points);

    }

    private void initData() {
        // viewpaper adapter适配器
        guids = new ArrayList<ImageView>();

        //创建viewpager的适配器
        for (int i = 0; i < mPics.length; i++) {
            ImageView iv_temp = new ImageView(getApplicationContext());
            iv_temp.setBackgroundResource(mPics[i]);

            //添加界面的数据
            guids.add(iv_temp);

            //灰色的点在LinearLayout中绘制：
            //获取点
            View v_point = new View(getApplicationContext());
            v_point.setBackgroundResource(R.drawable.point_simple);//灰点背景色
            //设置灰色点的显示大小
            int dip = 10;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UIUtils.dp2px(this,dip), UIUtils.dp2px(this,dip));
            //设置点与点的距离,第一个点除外
            if (i != 0)
                params.leftMargin = 47;
            v_point.setLayoutParams(params);

            mLlGuidePoints.addView(v_point);
        }

        // 创建viewpager的适配器
        adapter = new SplashAdapter(getApplicationContext(), guids);
        // 设置适配器
        mVp_Guide.setAdapter(adapter);
    }

    private void initEvent() {
        //监听界面绘制完成
        mGuideRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //取消注册界面而产生的回调接口
                mGuideRedPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //计算点于点之间的距离
                disPoints = (mLlGuidePoints.getChildAt(1).getLeft() - mLlGuidePoints.getChildAt(0).getLeft());
            }
        });

        //滑动事件监听滑动距离，点更随滑动。
        mVp_Guide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //当前viewpager显示的页码
                //如果viewpager滑动到第三页码（最后一页），显示进入的button
                if (position == guids.size() - 1) {
                    mBtn_skip.setVisibility(View.INVISIBLE);
                    mLlGuidePoints.setVisibility(View.INVISIBLE);
                    mGuideRedPoint.setVisibility(View.INVISIBLE);
                    mBtn_start.setVisibility(View.VISIBLE);
                } else {
                    mBtn_skip.setVisibility(View.VISIBLE);
                    mLlGuidePoints.setVisibility(View.VISIBLE);
                    mGuideRedPoint.setVisibility(View.VISIBLE);
                    mBtn_start.setVisibility(View.INVISIBLE);
                }
                currentItem = position;
            }

            /**
             *页面滑动调用，拿到滑动距离设置视图的滑动状态
             * @param position 当前页面位置
             * @param positionOffset 移动的比例值
             * @param positionOffsetPixels 便宜的像素
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //计算红点的左边距
                float leftMargin = disPoints * (position + positionOffset);
                //设置红点的左边距
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mGuideRedPoint.getLayoutParams();
                //对folat类型进行四舍五入，
                layoutParams.leftMargin = Math.round(leftMargin);
                //设置位置
                mGuideRedPoint.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mBtn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mBtn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //给页面设置触摸事件
        mVp_Guide.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            float endX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        endX = event.getX();
                        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                        //获取屏幕的宽度
                        Point size = new Point();
                        windowManager.getDefaultDisplay().getSize(size);
                        int width = size.x;
                        //首先要确定的是，是否到了最后一页，然后判断是否向左滑动，并且滑动距离是否符合，我这里的判断距离是屏幕宽度的4分之一（这里可以适当控制）
                        if (currentItem == (guids.size() - 1) && startX - endX >= (width / 4)) {
                            //进入主页
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            //这部分代码是切换Activity时的动画，看起来就不会很生硬
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            finish();
                        }
                        break;
                }
                return false;
            }
        });
    }

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

}
