package com.example.administrator.share.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private Button buttonLogin;
    private TextView pwdLoginTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        buttonLogin = findViewById(R.id.btn_login_phone);
        pwdLoginTv = findViewById(R.id.tv_login_pwd);
    }

    @Override
    protected void initView() {
        buttonLogin.setOnClickListener(this);
        pwdLoginTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login_phone:
                openActivity(LoginWithPhoneActivity.class);
                break;
            case R.id.tv_login_pwd:
                openActivity(LoginWithPwdActivity.class);
                break;
        }
    }
}