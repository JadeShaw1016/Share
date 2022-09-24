package com.example.administrator.share.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.MyDialogHandler;
import com.example.administrator.share.util.SharedPreferencesUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.FormBody;

public class ResPwdActivity extends BaseActivity implements View.OnClickListener {

    private TextView titleText;
    private View title_back;
    private Context mContext;

    private EditText usernameEt;
    private EditText pwdEt;
    private EditText respwdEt;

    private Button confrimBtn;

    private String username;
    private String password;

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
        titleText = $(R.id.titleText);
        title_back = $(R.id.title_back);

    }

    @Override
    protected void initView() {
        mContext = this;
        confrimBtn.setOnClickListener(this);
        title_back.setOnClickListener(this);
        titleText.setText("重置密码");
        uiFlusHandler = new MyDialogHandler(mContext, "更新中...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                checkInfo();
                break;
            case R.id.title_back:
                finish();
                break;
        }
    }

    private void checkInfo() {
        username = usernameEt.getText().toString().trim();
        password = pwdEt.getText().toString().trim();
        String repassword = respwdEt.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repassword)) {
            DisplayToast("用户名或密码不能为空！");
            return;
        }
        if (!password.equals(repassword)) {
            DisplayToast("重复输入密码不正确！");
            return;
        }
        update();

    }

    private void update() {
        uiFlusHandler.sendEmptyMessage(SHOW_LOADING_DIALOG);
        String url = Constants.BASE_URL + "user/updatePassword";
        FormBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        OkHttpUtils
                .put()
                .url(url)
                .requestBody(formBody)
                .id(1)
                .build()
                .execute(new MyStringCallback());
    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 1:
                    uiFlusHandler.sendEmptyMessage(DISMISS_LOADING_DIALOG);
                    if ("更新失败".equals(response)) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("提示")
                                .setMessage("该用户不存在，请用手机号注册登录！")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        setResult(RESULT_OK);
                                        Intent intent = new Intent(ResPwdActivity.this, LoginActivity.class);
                                        ResPwdActivity.this.startActivity(intent);
                                        finish();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        return;
                                    }
                                })
                                .show();
                        return;
                    } else {
                        // 更新用户密码
                        Constants.USER.setPassword(response);
                        boolean result = SharedPreferencesUtils.saveUserInfo(mContext, Constants.USER);
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
            DisplayToast("网络链接出错！"+arg1);
        }
    }
}
