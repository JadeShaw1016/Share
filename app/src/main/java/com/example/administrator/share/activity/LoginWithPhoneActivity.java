package com.example.administrator.share.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.User;
import com.example.administrator.share.util.ActivityManager;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.SharedPreferencesUtils;
import com.example.administrator.share.util.Utils;
import com.google.gson.Gson;
import com.mob.MobSDK;
import com.mob.OperationCallback;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;

public class LoginWithPhoneActivity extends BaseActivity implements View.OnClickListener {
    private Button buttonCode;
    private EditText editTextPhoneNum;
    private EventHandler eh;
    private ImageView backIv;
    private ImageView clearPhoneIv;
    private String phoneNum;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_phone);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        buttonCode = findViewById(R.id.buttonCode);
        editTextPhoneNum = findViewById(R.id.editTextPhoneNum);
        backIv = findViewById(R.id.iv_login_with_phone_back);
        clearPhoneIv = findViewById(R.id.tv_login_with_vercode_close);
        submitPrivacyGrantResult(true);
    }

    @Override
    protected void initView() {
        mContext = this;
        EventHandlerListener();
        //注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eh);
        buttonCode.setOnClickListener(this);
        backIv.setOnClickListener(this);
        clearPhoneIv.setOnClickListener(this);
        editTextPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                clearPhoneIv.setVisibility(View.VISIBLE);
            }
        });
    }

    private void EventHandlerListener() {
        eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        isExistUsername();
                    } else if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginWithPhoneActivity.this, "语音验证发送", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginWithPhoneActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    Throwable throwable = (Throwable) data;
                    throwable.printStackTrace();
                    Log.i("LoginWithPhoneActivity", throwable.toString());
                    try {
                        JSONObject obj = new JSONObject(throwable.getMessage());
                        final String des = obj.optString("detail");
                        //TODO 因为SMSSDK不免费了，所以这里直接跳过验证了，随便输什么验证码都能通过
                        if ("账户余额不足".equals(des)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginWithPhoneActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if ("需要校验的验证码错误".equals(des)) {
                            isExistUsername();
                        }
//                        if (!TextUtils.isEmpty(des)) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    LoginWithVercodeActivity.editTextCode.clear();
//                                    LoginWithVercodeActivity.buttonLogin.setBackground(getDrawable(R.color.smssdk_gray_press));
//                                    DisplayToast(des);
//                                }
//                            });
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonCode:
                phoneNum = editTextPhoneNum.getText().toString();
                if (!phoneNum.isEmpty()) {
                    if (Utils.checkTel(phoneNum)) { //利用正则表达式获取检验手机号
                        // 获取验证码
                        SMSSDK.getVerificationCode("86", phoneNum);
                        Intent intent = new Intent(this, LoginWithVercodeActivity.class);
                        intent.putExtra("phoneNum", phoneNum);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "请输入有效的手机号", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "请输入手机号", Toast.LENGTH_LONG).show();
                    break;
                }
                break;
            case R.id.iv_login_with_phone_back:
                finish();
                break;
            case R.id.tv_login_with_vercode_close:
                editTextPhoneNum.setText("");
                clearPhoneIv.setVisibility(View.GONE);
                break;
        }
    }

    // 使用完EventHandler需注销，否则可能出现内存泄漏
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

    private void submitPrivacyGrantResult(boolean granted) {
        MobSDK.submitPolicyGrantResult(granted, new OperationCallback<Void>() {
            @Override
            public void onComplete(Void data) {
                Log.d("LoginWithPhoneActivity", "隐私协议授权结果提交：成功");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("LoginWithPhoneActivity", "隐私协议授权结果提交：失败");
            }
        });
    }

    private void isExistUsername() {
        LoginWithVercodeActivity.uiFlusHandler.sendEmptyMessage(SHOW_LOADING_DIALOG);
        String url = Constants.BASE_URL + "user/isExistUsername";
        OkHttpUtils
                .get()
                .url(url)
                .id(1)
                .addParams("username", phoneNum)
                .build()
                .execute(new MyStringCallback());
    }

    private void loginWithPhone() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "user/loginWithPhone";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(2)
                        .addParams("username", phoneNum)
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 1:
                    if (response.equals("true")) {
                        loginWithPhone();
                    } else {
                        Intent intent = new Intent(mContext, RegisterActivity.class);
                        intent.putExtra("username", phoneNum);
                        LoginWithVercodeActivity.uiFlusHandler.sendEmptyMessage(DISMISS_LOADING_DIALOG);
                        startActivity(intent);
                    }
                    break;
                case 2:
                    User user = new Gson().fromJson(response, User.class);
                    // 存储用户
                    Constants.USER = user;
                    boolean result = SharedPreferencesUtils.saveUserInfo(mContext, user);
                    if (result) {
                        Log.d("LoginActivity", "登录成功");
                    } else {
                        DisplayToast("用户存储失败");
                    }
                    LoginWithVercodeActivity.uiFlusHandler.sendEmptyMessage(DISMISS_LOADING_DIALOG);
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
            DisplayToast("网络链接出错！");
        }
    }

}