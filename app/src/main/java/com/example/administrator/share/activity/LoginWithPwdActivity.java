package com.example.administrator.share.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.User;
import com.example.administrator.share.util.ActivityManager;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.MyDialogHandler;
import com.example.administrator.share.util.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;


public class LoginWithPwdActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_username;
    private EditText et_password;
    private Button bt_login;
    private TextView repwdTv;
    private Button bt_config;
    private Context mContext;
    private int flag = 0;
    private ImageView backIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_pwd);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        et_username = $(R.id.login_et_username);
        et_password = $(R.id.login_et_password);
        bt_login = $(R.id.login_btn_login);
        repwdTv = $(R.id.txtForgetPwd);
        bt_config = $(R.id.login_bt_config);
        backIv = $(R.id.iv_login_with_pwd_back);
    }

    @Override
    protected void initView() {
        mContext = this;
        bt_login.setOnClickListener(this);
        repwdTv.setOnClickListener(this);
        bt_config.setOnClickListener(this);
        backIv.setOnClickListener(this);
        echo();
        uiFlusHandler = new MyDialogHandler(mContext, "登录中...");
        if(!TextUtils.isEmpty(et_username.getText().toString())){
            flag = 1;
            login();
        }
    }

    private void login() {
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        //d.判断用户名密码是否为空，不为空请求服务器（省略，默认请求成功）
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(mContext, "不可留空", Toast.LENGTH_SHORT).show();
            return;
        }
        // 服务端验证
        checkUser();
        // openActivity(MainMenuActivity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_login:
                login();
                break;
            case R.id.txtForgetPwd:
                openActivity(ResPwdActivity.class);
                break;
            case R.id.login_bt_config:
                openActivity(ConfigActivity.class);
                break;
            case R.id.iv_login_with_pwd_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            openActivity(ConfigActivity.class);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 回显
     */
    private void echo() {
        User user = SharedPreferencesUtils.getUserInfo(mContext);//获取用户名密码
        if (user != null) {
            String username = user.getUsername();
            String password = user.getPassword();
            et_username.setText(username);
            et_password.setText(password);
        }
    }

    private void checkUser() {
        uiFlusHandler.sendEmptyMessage(SHOW_LOADING_DIALOG);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "user/loginWithPwd";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(1)
                        .addParams("username", et_username.getText().toString().trim())
                        .addParams("password", et_password.getText().toString().trim())
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            switch (id) {
                case 1:
                    User user;
                    if(response.equals("error")){
                        DisplayToast("用户名或者密码错误");
                        return;
                    }else{
                        user = gson.fromJson(response, User.class);
                        // 存储用户
                        boolean result = SharedPreferencesUtils.saveUserInfo(mContext, user);
                        if (result) {
                            if(flag == 0){
                                Log.d("LoginWithPwdActivity","登录成功");
                            }
                        } else {
                            DisplayToast("用户存储失败");
                        }
                    }
                    uiFlusHandler.sendEmptyMessage(DISMISS_LOADING_DIALOG);
                    openActivity(MainMenuActivity.class);
                    ActivityManager.getInstance().killAllActivity();
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
