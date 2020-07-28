package com.example.administrator.share.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.User;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.MyDialogHandler;
import com.example.administrator.share.util.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by djzhao on 17/05/01.
 */

public class ResPwdActivity extends BaseActivity implements View.OnClickListener {


    private Context mContext;

    private EditText usernameEt;
    private EditText pwdEt;
    private EditText respwdEt;

    private Button confrimBtn;

    private String username;
    private String password;
    private String repassword;

    private MyDialogHandler uiFlusHandler;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_respwd);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {

        confrimBtn = $(R.id.btn_confirm);
        pwdEt = $(R.id.edit_pwd);
        respwdEt = $(R.id.edit_respwd);
        usernameEt = $(R.id.edit_name);

    }

    @Override
    protected void initView() {
        mContext = this;

        confrimBtn.setOnClickListener(this);
        uiFlusHandler = new MyDialogHandler(mContext, "更新中...");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                checkInfo();
                break;
        }
    }


    private void checkInfo() {
        System.out.println("检查信息");
        username = usernameEt.getText().toString().trim();
        password = pwdEt.getText().toString().trim();
        repassword = respwdEt.getText().toString().trim();

        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repassword)){
            DisplayToast("用户名或密码不能为空！");
            return;
        }
        if(!password.equals(repassword)){
            DisplayToast("重复输入密码不正确！");
            return;
        }
        update();

    }

    private void update() {
        System.out.println("检查信息2");
        uiFlusHandler.sendEmptyMessage(SHOW_LOADING_DIALOG);
        String url = Constants.BASE_URL + "User?method=update";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("username", username)
                .addParams("password", password)
                .id(1)
                .build()
                .execute(new MyStringCallback());
    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            switch (id) {
                case 1:
                    uiFlusHandler.sendEmptyMessage(DISMISS_LOADING_DIALOG);
                    User user = null;
                    try {
                        System.out.println("检查信息3"+response);
                        user = gson.fromJson(response, User.class);
                    } catch (JsonSyntaxException e) {
                        user = null;
                    }
                    if (user == null) {
                        DisplayToast(response);
                        return;
                    } else {
                        // 存储用户
                        Constants.USER.setPassword(user.getPassword());
                        user.setHeight(170);
                        user.setWeight(70);
                        user.setSex("男");
                        user.setUserId(3);
                        boolean result = SharedPreferencesUtils.saveUserInfo(mContext, user);
                        if (result) {
                            Toast.makeText(mContext, "更新成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "用户信息存储失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    finish();
                    break;
                case 2:

                    break;
                default:
                    DisplayToast("what?");
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            DisplayToast("网络链接出错！");
        }
    }
}
