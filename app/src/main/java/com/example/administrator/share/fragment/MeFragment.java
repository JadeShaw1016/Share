package com.example.administrator.share.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.FansActivity;
import com.example.administrator.share.activity.FocusActivity;
import com.example.administrator.share.activity.MainMenuActivity;
import com.example.administrator.share.activity.SettingActivity;
import com.example.administrator.share.adapter.FragmentAdapter;
import com.example.administrator.share.entity.FollowsListItem;
import com.example.administrator.share.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;


public class MeFragment extends Fragment implements View.OnClickListener{

    private TabLayout tabLayout;
    private ViewPager pager;
    private String [] title={"我的作品","我的点赞","我的收藏"};

    private ImageView settingIv;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        findViewById(view);
        initView();
        setAdapter();
        return view;
    }

    private void findViewById(View view){
        pager= view.findViewById(R.id.page);
        tabLayout= view.findViewById(R.id.tab_layout);
        settingIv = view.findViewById(R.id.iv1);
        nicknameTv = view.findViewById(R.id.me_homepage_nickname);
        fansTv = view.findViewById(R.id.me_fans);
        fansLl = view.findViewById(R.id.ll_fans);
        focusLl = view.findViewById(R.id.ll_focus);
        focusTv = view.findViewById(R.id.tv_focus);
        popularityTv = view.findViewById(R.id.me_popularity);
        faceIv = view.findViewById(R.id.me_face);
        signatureTv = view.findViewById(R.id.me_signature);
    }

    private void initView(){
        settingIv.setOnClickListener(this);
        fansLl.setOnClickListener(this);
        focusLl.setOnClickListener(this);
        faceIv.setOnClickListener(this);
        echo();
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.iv1:
                intent=new Intent(getActivity(), SettingActivity.class);
                break;
            case R.id.ll_fans:
                intent=new Intent(getActivity(), FansActivity.class);
                intent.putParcelableArrayListExtra("mFansList", (ArrayList<? extends Parcelable>) mFansList);
                break;
            case R.id.ll_focus:
                intent=new Intent(getActivity(), FocusActivity.class);
                intent.putParcelableArrayListExtra("mFocusList", (ArrayList<? extends Parcelable>) mFocusList);
                break;
            case R.id.me_face:
                final Dialog dialog = new Dialog(MainMenuActivity.mContext, R.style.MyDialogStyle_fullScreen_black);
                ImageView imageView = new ImageView(MainMenuActivity.mContext);
                imageView.setImageBitmap(Constants.FACEIMAGE);
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
        }
        if(intent != null){
            startActivity(intent);
        }
    }

    private void setAdapter(){
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> mTitles = new ArrayList<>();

        Collections.addAll(mTitles, title);
        fragmentList.add(MyWorkFragment.newInstance(title[0]));
        fragmentList.add(MyFavoFragment.newInstance(title[1]));
        fragmentList.add(MyCollectionFragment.newInstance(title[2]));

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getChildFragmentManager(), fragmentList, mTitles);
        pager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(pager);//与ViewPage建立关系
    }

    private void echo(){
        nicknameTv.setText(Constants.USER.getNickname());
        getUserFace();
    }

    private void getUserFace(){
        new AsyncTask<Void,Void,Integer>(){

            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "Download?method=getUserFaceImage";
                OkHttpUtils
                        .get()//
                        .url(url)//
                        .addParams("face", Constants.USER.getFace())
                        .build()//
                        .execute(new BitmapCallback() {
                            @Override
                            public void onError(Call call, Exception e, int i) {
                            }
                            @Override
                            public void onResponse(Bitmap bitmap, int i) {
                                faceIv.setImageBitmap(bitmap);
                                Constants.FACEIMAGE = bitmap;
                            }
                        });
                return null;
            }
        }.execute();
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
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .build()
                        .execute(new MeFragment.MyStringCallback());
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
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .build()
                        .execute(new MeFragment.MyStringCallback());
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
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .build()
                        .execute(new MeFragment.MyStringCallback());
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
                default:
                    Toast.makeText(getActivity(), "What?", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            Toast.makeText(getActivity(), "网络链接出错!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getFans();
        getFocus();
        getPopularity();
        faceIv.setImageBitmap(Constants.FACEIMAGE);
        if(Constants.USER.getSignature() != null){
            signatureTv.setText(Constants.USER.getSignature());
        }
    }
}
