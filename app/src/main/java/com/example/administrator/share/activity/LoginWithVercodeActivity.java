package com.example.administrator.share.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.util.MyDialogHandler;
import com.example.administrator.share.util.PhoneCode;
import com.example.administrator.share.util.Utils;

import cn.smssdk.SMSSDK;

public class LoginWithVercodeActivity extends BaseActivity implements View.OnClickListener{

    protected static Button buttonLogin;
    protected static PhoneCode editTextCode;
    private String phoneNum;
    private ImageView backIv;
    private TextView phoneNumTv;
    private TextView timeTv;
    private MyCountDownTimer myCountDownTimer;
    protected static MyDialogHandler uiFlusHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phoneNum = getIntent().getStringExtra("phoneNum");
        setContentView(R.layout.activity_login_with_vercode);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        editTextCode = findViewById(R.id.editTextCode);
        buttonLogin = findViewById(R.id.buttonLogin);
        backIv = findViewById(R.id.iv_login_with_vercode_back);
        phoneNumTv = findViewById(R.id.tv_login_with_phone_number);
        timeTv = findViewById(R.id.tv_login_with_vercode_time);
    }

    @Override
    protected void initView() {
        uiFlusHandler = new MyDialogHandler(this, "登录中...");
        phoneNumTv.setText("+86 "+ Utils.hidePhoneNum(phoneNum));
        timeTv.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
        backIv.setOnClickListener(this);
        myCountDownTimer = new MyCountDownTimer(60000,1000);
        myCountDownTimer.start();
        editTextCode.setOnInputListener(new PhoneCode.OnInputListener() {
            @Override
            public void onSucess(String code) {
                //底部【登录】按钮可点击
                buttonLogin.setBackground(getDrawable(R.drawable.selector_orange));
                buttonLogin.setClickable(true);
            }

            @Override
            public void onInput() {
                //底部【登录】按钮不可点击
                buttonLogin.setBackground(getDrawable(R.color.smssdk_gray_press));
                buttonLogin.setClickable(false);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonLogin:
                String code = editTextCode.getPhoneCode();
                if(!code.isEmpty()){
                    //提交验证码
                    SMSSDK.submitVerificationCode("86", phoneNum, code);
                }else{
                    Toast.makeText(getApplicationContext(),"请输入验证码",Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            case R.id.iv_login_with_vercode_back:
                finish();
                break;
            case R.id.tv_login_with_vercode_time:
                SMSSDK.getVerificationCode("86", phoneNum);
                myCountDownTimer.start();
                break;
        }
    }

    //倒计时函数
    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            timeTv.setClickable(false);
            timeTv.setText(l/1000+"秒");

        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            timeTv.setText("重新获取");
            //设置可点击
            timeTv.setClickable(true);
        }
    }

}