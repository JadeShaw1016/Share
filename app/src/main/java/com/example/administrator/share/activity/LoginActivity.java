package com.example.administrator.share.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private Button buttonLogin;
    private TextView pwdLoginTv;
    private TextView protocolTv;
    private CheckBox protocolCb;

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
        protocolTv = findViewById(R.id.tv_login_protocol);
        protocolCb = findViewById(R.id.cb_login);
    }

    @Override
    protected void initView() {
        buttonLogin.setOnClickListener(this);
        pwdLoginTv.setOnClickListener(this);
        protocolTv.setText(getClickableSpan());
        //设置超链接可点击
        protocolTv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login_phone:
                if(protocolCb.isChecked()){
                    openActivity(LoginWithPhoneActivity.class);
                }else{
                    DisplayToast("请先勾选同意隐私协议");
                }
                break;
            case R.id.tv_login_pwd:
                if(protocolCb.isChecked()){
                    openActivity(LoginWithPwdActivity.class);
                }else{
                    DisplayToast("请先勾选同意隐私协议");
                }
                break;
        }
    }

    /**
     * 获取可点击的SpannableString
     */
    private SpannableString getClickableSpan() {
        SpannableString spannableString = new SpannableString("同意《服务条款》和《隐私政策》");
        //设置下划线文字
        spannableString.setSpan(new UnderlineSpan(), 3, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mob.com/about/policy"));
                startActivity(intent);
            }
        }, 3, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 3, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置下划线文字
        spannableString.setSpan(new UnderlineSpan(), 10, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mob.com/about/policy"));
                startActivity(intent);
            }
        }, 10, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 10, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

}