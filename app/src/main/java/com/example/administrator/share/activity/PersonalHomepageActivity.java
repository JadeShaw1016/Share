package com.example.administrator.share.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.administrator.share.R;
import com.example.administrator.share.adapter.FragmentAdapter;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.FollowsListItem;
import com.example.administrator.share.fragment.MyCollectionFragment;
import com.example.administrator.share.fragment.MyFavoFragment;
import com.example.administrator.share.fragment.MyWorkFragment;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;


public class PersonalHomepageActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "PersonalHomepageActivity";
    private Context mContext;
    private TabLayout tabLayout;
    private ViewPager pager;
    private String[] title = {"TA的作品", "TA的点赞", "TA的收藏"};
    private TextView nicknameTv;
    private TextView fansTv;
    private TextView focusTv;
    private TextView popularityTv;
    private ImageView faceIv;
    private TextView signatureTv;
    private List<FollowsListItem> mFocusList;
    private List<FollowsListItem> mFansList;
    private LinearLayout fansLl;
    private LinearLayout focusLl;
    private String userId;
    private String nickname;
    private String face;
    private String signature;
    private TextView titleTv;
    private View title_back;
    private Button focusBtn;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        userId = String.valueOf(getIntent().getIntExtra("userId",0));
        nickname = getIntent().getStringExtra("nickname");
        face = getIntent().getStringExtra("face");
        signature = getIntent().getStringExtra("signature");
        setContentView(R.layout.activity_personal_homepage);
        findViewById();
        initView();
        setAdapter();
    }

    @Override
    protected void findViewById() {
        pager = findViewById(R.id.page);
        tabLayout = findViewById(R.id.tab_layout);
        nicknameTv = findViewById(R.id.me_homepage_nickname);
        fansTv = findViewById(R.id.me_fans);
        fansLl = findViewById(R.id.ll_fans);
        focusLl = findViewById(R.id.ll_focus);
        focusTv = findViewById(R.id.tv_focus);
        popularityTv = findViewById(R.id.me_popularity);
        faceIv = findViewById(R.id.me_face);
        signatureTv = findViewById(R.id.me_signature);
        titleTv = findViewById(R.id.titleText);
        title_back = findViewById(R.id.title_back);
        focusBtn = findViewById(R.id.btn_focus);
    }

    @Override
    protected void initView() {
        mContext = this;
        fansLl.setOnClickListener(this);
        focusLl.setOnClickListener(this);
        faceIv.setOnClickListener(this);
        title_back.setOnClickListener(this);
        focusBtn.setOnClickListener(this);
        echo();
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.ll_fans:
                intent = new Intent(this, FansActivity.class);
                intent.putParcelableArrayListExtra("mFansList", (ArrayList<? extends Parcelable>) mFansList);
                break;
            case R.id.ll_focus:
                intent = new Intent(this, FocusActivity.class);
                intent.putParcelableArrayListExtra("mFocusList", (ArrayList<? extends Parcelable>) mFocusList);
                break;
            case R.id.me_face:
                final Dialog dialog = new Dialog(mContext, R.style.MyDialogStyle_fullScreen_black);
                ImageView imageView = new ImageView(this);
                imageView.setImageDrawable(faceIv.getDrawable());
                dialog.setContentView(imageView);
                dialog.show();
                //大图的点击事件（点击让他消失）
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.btn_focus:
                if (String.valueOf(Constants.USER.getUserId()).equals(userId)) {
                    DisplayToast("不能关注自己哦！");
                } else {
                    addFocus();
                }
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private void setAdapter() {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> mTitles = new ArrayList<>();
        Collections.addAll(mTitles, title);
        fragmentList.add(MyWorkFragment.newInstance(userId));
        fragmentList.add(MyFavoFragment.newInstance(userId));
        fragmentList.add(MyCollectionFragment.newInstance(userId));
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList, mTitles);
        pager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(pager);//与ViewPage建立关系
    }

    private void echo() {
        nicknameTv.setText(nickname);
        titleTv.setText(nickname + "的主页");
        Uri uri = Uri.parse(Constants.BASE_URL + "Download?method=getUserFaceImage&face=" + face);
        Glide.with(mContext).load(uri).into(faceIv);
        getFans();
        getFocus();
        getPopularity();
        isFocused();
        if (signature != null) {
            signatureTv.setText(signature);
        }
    }

    /**
     * 获取粉丝数
     */
    private void getFans() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "Follows?method=getCurrentFansList";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("userId", userId)
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    /**
     * 获取关注数
     */
    private void getFocus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "Follows?method=getFocusList";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(2)
                        .addParams("userId", userId)
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    /**
     * 获取人气数
     */
    private void getPopularity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "Follows?method=getPopularity";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(3)
                        .addParams("userId", userId)
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    private void addFocus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "Follows?method=addFocus";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(4)
                        .addParams("fansId", String.valueOf(Constants.USER.getUserId()))
                        .addParams("userId", String.valueOf(userId))
                        .addParams("followTime", Utils.getCurrentDatetime())
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    private void isFocused() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "Follows?method=isFocused";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(5)
                        .addParams("fansId", String.valueOf(Constants.USER.getUserId()))
                        .addParams("userId", String.valueOf(userId))
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<FollowsListItem>>() {
            }.getType();
            switch (id) {
                case 1:
                    mFansList = gson.fromJson(response, type);
                    fansTv.setText(String.valueOf(mFansList.size()));
                    break;
                case 2:
                    mFocusList = gson.fromJson(response, type);
                    focusTv.setText(String.valueOf(mFocusList.size()));
                    break;
                case 3:
                    popularityTv.setText(response);
                    break;
                case 4:
                    isFocused();
                    DisplayToast(response);
                    break;
                case 5:
                    if (response.equals("已关注")) {
                        focusBtn.setText("已关注");
                    } else {
                        focusBtn.setText("关注");
                    }
                    break;
                default:
                    Toast.makeText(mContext, "What?", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            Toast.makeText(mContext, "网络链接出错!", Toast.LENGTH_SHORT).show();
        }
    }
}
