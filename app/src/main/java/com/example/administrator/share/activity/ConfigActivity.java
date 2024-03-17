package com.example.administrator.share.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.SharedPreferencesUtils;

public class ConfigActivity extends BaseActivity implements View.OnClickListener {

    private View title_back;
    private TextView titleText;

    private Button saveButton;
    private EditText ipEditText;
    private EditText portEditText;

    private Context mContext;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_config);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        this.title_back = $(R.id.title_back);
        this.titleText = $(R.id.titleText);

        saveButton = $(R.id.config_btn_save);
        ipEditText = $(R.id.config_et_ip);
        portEditText = $(R.id.config_et_port);
    }

    @Override
    protected void initView() {
        mContext = this;
        this.titleText.setText("配置");
        this.title_back.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        // 回显
        echo();
    }

    /**
     * 数据回显
     */
    private void echo() {
        SharedPreferences preferences = mContext.getSharedPreferences("serverConnect", Context.MODE_PRIVATE);
        ipEditText.setText(preferences.getString("ip", ""));
        portEditText.setText(preferences.getString("port", ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back: {
                this.finish();
            }
            break;
            case R.id.config_btn_save:
                saveConfig();
                break;
        }
    }

    private void saveConfig() {
        String ip = ipEditText.getText().toString().trim();
        String port = portEditText.getText().toString().trim();
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            DisplayToast("请勿留空！");
            return;
        }
        if(SharedPreferencesUtils.saveIPConfig(mContext, ip, port)){
            Constants.BASE_URL = "http://" + ip + ":" + port + "/";
            DisplayToast("修改成功:"+Constants.BASE_URL);
            this.finish();
        } else {
            DisplayToast("修改失败！");
        }
    }
}
