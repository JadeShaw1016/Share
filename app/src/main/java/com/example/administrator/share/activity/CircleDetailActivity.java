package com.example.administrator.share.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.adapter.CircleDetailCommnetsAdapter;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.CircleDetail;
import com.example.administrator.share.entity.Comment;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.MyDialogHandler;
import com.example.administrator.share.util.Utils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.example.administrator.share.util.Constants.BASE_IMAGE_DOWNLOAD;

public class CircleDetailActivity extends BaseActivity implements View.OnClickListener {

    private CircleDetailCommnetsAdapter adapter;
    private List<Comment> mList;
    private String TITLE_NAME = "圈子详情";
    private View title_back;
    private TextView titleText;
    private TextView nicknameTV;
    private TextView titleTV;
    private TextView releaseTimeTV;
    private ImageView imageIV;
    private TextView contentTV;
    private ImageView collectionIv;
    private TextView collectionTv;
    private ImageView favorIv;
    private TextView favorTv;
    private Button focusBtn;
    private LinearLayout commentPane;
    private EditText addCommentET;
    private ImageView addCommentIV;
    private boolean isShowCommentPane;
    private LinearLayout commentLL;
    private LinearLayout collectionLL;
    private LinearLayout favorLL;
    private Context mContext;
    private int newsId;
    private int be_focused_personId;
    private String replyUsername;
    private MyDialogHandler uiFlusHandler;
    private Dialog dialog;
    private ImageView dialogIv;
    private ImageView faceIv;
    private RecyclerView commentsRv;
    private LinearLayoutManager layoutManager;
    private TextView clickTimes;
    private TextView collectTimes;
    private TextView commentTimes;
    private int CURRENT_COLLECTTIMES;
    private int CURRENT_COMMENTTIMES;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        newsId = getIntent().getIntExtra("newsId", 0);
        be_focused_personId = getIntent().getIntExtra("be_focused_personId", 0);
        setContentView(R.layout.activity_circle_detail);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        this.title_back = $(R.id.title_back);
        this.titleText = $(R.id.titleText);
        nicknameTV = $(R.id.news_detail_nickname);
        releaseTimeTV = $(R.id.news_detail_time);
        imageIV = $(R.id.news_detail_image);
        contentTV = $(R.id.news_detail_content);
        titleTV = $(R.id.detail_title);
        commentLL = $(R.id.news_detail_add_comment);
        collectionLL = $(R.id.news_detail_add_collection);
        favorLL = $(R.id.news_detail_add_favor);
        commentPane = $(R.id.news_detail_add_commment_pane);
        addCommentET = $(R.id.news_detail_add_commment_text);
        addCommentIV = $(R.id.news_detail_add_commment_btn);
        commentsRv = $(R.id.news_detail_comment);
        collectionIv = $(R.id.iv_collection);
        collectionTv = $(R.id.tv_collection);
        favorIv = $(R.id.iv_favo);
        favorTv = $(R.id.tv_favo);
        focusBtn = $(R.id.btn_focus);
        dialogIv = new ImageView(this);
        faceIv = $(R.id.iv_circle_detail_face);
        clickTimes = $(R.id.tv_circle_detail_click_times);
        collectTimes = $(R.id.tv_circle_detail_collect_times);
        commentTimes = $(R.id.tv_circle_detail_comment_times);
    }

    @Override
    protected void initView() {
        mContext = this;
        this.titleText.setText(TITLE_NAME);
        this.title_back.setOnClickListener(this);
        commentLL.setOnClickListener(this);
        collectionLL.setOnClickListener(this);
        favorLL.setOnClickListener(this);
        addCommentIV.setOnClickListener(this);
        focusBtn.setOnClickListener(this);
        imageIV.setOnClickListener(this);
        dialogIv.setOnClickListener(this);
        layoutManager = new LinearLayoutManager(this);
        uiFlusHandler = new MyDialogHandler(mContext, "加载中...");
        refreshData();
        addClickTimes();
        isCollected();
        isFavored();
        isFocused();
        //大图所依附的dialog
        dialog = new Dialog(mContext, R.style.MyDialogStyle_fullScreen_black);
        dialogListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.news_detail_add_comment:
                showCommemtPane();
                break;
            case R.id.news_detail_add_collection:
                addNewCollection();
                break;
            case R.id.news_detail_add_favor:
                addNewFavor();
                break;
            case R.id.news_detail_add_commment_btn:
                addNewComment();
                break;
            case R.id.btn_focus:
                if(be_focused_personId == Constants.USER.getUserId()){
                    DisplayToast("不能关注自己哦！");
                }
                else{
                    addFocus();
                }
                break;
            case R.id.news_detail_image:
                dialog.show();
                break;
        }
    }

    private void dialogListener(){
        //大图的点击事件（点击让他消失）
        dialogIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //大图的长按监听
        dialogIv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //弹出的“保存图片”的Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setItems(new String[]{getResources().getString(R.string.save_picture)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveImageToGallery(((BitmapDrawable) dialogIv.getDrawable()).getBitmap());
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    private void showCommemtPane() {
        isShowCommentPane = !isShowCommentPane;
        if (isShowCommentPane) {
            commentPane.setVisibility(View.VISIBLE);
            replyUsername = "";
            showKeyboard(addCommentET);
        } else {
            commentPane.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(addCommentET.getWindowToken(), 0);
        }
    }

    private void refreshData() {
        uiFlusHandler.sendEmptyMessage(SHOW_LOADING_DIALOG);
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "News?method=getNewsDetail";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(1)
                        .addParams("newsId", String.valueOf(newsId))
                        .build()
                        .execute(new MyStringCallback());
                return 0;
            }
        }.execute();
    }

    //添加新收藏
    private void addNewCollection() {
        String url = Constants.BASE_URL + "GetCircleList?method=addNewCollection";
        OkHttpUtils
                .post()
                .url(url)
                .id(2)
                .addParams("newsId", String.valueOf(newsId))
                .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                .addParams("collectionTime", Utils.getCurrentDatetime())
                .build()
                .execute(new MyStringCallback());
    }

    // 添加新评论
    private void addNewComment() {
        String commentText = addCommentET.getText().toString().trim();
        if (TextUtils.isEmpty(commentText)) {
            DisplayToast("请先输入内容");
            return;
        }
        String url = Constants.BASE_URL + "Message?method=addNewComment";
        OkHttpUtils
                .post()
                .url(url)
                .id(3)
                .addParams("newsId", String.valueOf(newsId))
                .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                .addParams("authorName",nicknameTV.getText().toString())
                .addParams("comment", commentText)
                .addParams("replyUser", replyUsername)
                .addParams("commentTime", Utils.getCurrentDatetime())
                .build()
                .execute(new MyStringCallback());
    }

    private void isCollected(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "GetCircleList?method=isCollected";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(4)
                        .addParams("newsId", String.valueOf(newsId))
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    private void addFocus(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "Follows?method=addFocus";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(5)
                        .addParams("fansId", String.valueOf(Constants.USER.getUserId()))
                        .addParams("userId",String.valueOf(be_focused_personId))
                        .addParams("followTime", Utils.getCurrentDatetime())
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    private void isFocused(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "Follows?method=isFocused";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(6)
                        .addParams("fansId", String.valueOf(Constants.USER.getUserId()))
                        .addParams("userId",String.valueOf(be_focused_personId))
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    //添加新点赞
    private void addNewFavor() {
        String url = Constants.BASE_URL + "GetCircleList?method=addNewFavor";
        OkHttpUtils
                .post()
                .url(url)
                .id(7)
                .addParams("newsId", String.valueOf(newsId))
                .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                .addParams("be_focused_personId",String.valueOf(be_focused_personId))
                .addParams("favorTime", Utils.getCurrentDatetime())
                .build()
                .execute(new MyStringCallback());
    }

    private void isFavored(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "GetCircleList?method=isFavored";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(8)
                        .addParams("newsId", String.valueOf(newsId))
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    private void addClickTimes(){
        if(be_focused_personId != Constants.USER.getUserId()){
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    String url = Constants.BASE_URL + "News?method=addClickTimes";
                    OkHttpUtils
                            .post()
                            .url(url)
                            .id(9)
                            .addParams("newsId", String.valueOf(newsId))
                            .build()
                            .execute(new MyStringCallback());
                    return 0;
                }
            }.execute();
        }
    }

    private void addCollectTimes(){
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "News?method=addCollectTimes";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(9)
                        .addParams("newsId", String.valueOf(newsId))
                        .build()
                        .execute(new MyStringCallback());
                return 0;
            }
        }.execute();
    }

    private void reduceCollectTimes(){
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "News?method=reduceCollectTimes";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(9)
                        .addParams("newsId", String.valueOf(newsId))
                        .build()
                        .execute(new MyStringCallback());
                return 0;
            }
        }.execute();
    }

    private void addCommentTimes(){
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "News?method=addCommentTimes";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(9)
                        .addParams("newsId", String.valueOf(newsId))
                        .build()
                        .execute(new MyStringCallback());
                return 0;
            }
        }.execute();
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {

            switch (id) {
                case 1:
                    Gson gson = new Gson();
                    try {
                        CircleDetail circleDetail = gson.fromJson(response, CircleDetail.class);
                        if (circleDetail != null) {
                            CURRENT_COLLECTTIMES = circleDetail.getCollectTimes();
                            CURRENT_COMMENTTIMES = circleDetail.getCommentTimes();
                            nicknameTV.setText(circleDetail.getNickname());
                            releaseTimeTV.setText(circleDetail.getReleaseTime());
                            titleTV.setText(circleDetail.getTitle());
                            contentTV.setText(circleDetail.getContent());
                            clickTimes.setText(String.valueOf(circleDetail.getClickTimes()));
                            collectTimes.setText(String.valueOf(CURRENT_COLLECTTIMES));
                            commentTimes.setText(String.valueOf(CURRENT_COMMENTTIMES));
                            // 加载图片
                            if (!TextUtils.isEmpty(circleDetail.getImage())) {
                                imageIV.setVisibility(View.VISIBLE);
                                getNewsImage(circleDetail.getImage());
                                getFaceImage(circleDetail.getFace());
                            } else {
                                imageIV.setVisibility(View.GONE);
                            }
                        }
                        mList = circleDetail.getComments();
                    } catch (Exception e) {
                        Toast.makeText(mContext, "错误："+e.getMessage(), Toast.LENGTH_SHORT).show();
                        mList = null;
                    }
                    if (mList != null && mList.size() > 0) {
                        adapter = new CircleDetailCommnetsAdapter(mContext, mList);
                        adapter.setOnCommentButtonClickListner(new CircleDetailCommnetsAdapter.OnCommentButtonClickListner() {
                            @Override
                            public void OnCommentButtonClicked(String replyUser) {
                                commentPane.setVisibility(View.VISIBLE);
                                addCommentET.setHint("回复 " + replyUser + " 的评论");
                                replyUsername = replyUser;
                            }
                        });
                        commentsRv.setLayoutManager(layoutManager);
                        commentsRv.setAdapter(adapter);
                    }
                    uiFlusHandler.sendEmptyMessage(DISMISS_LOADING_DIALOG);
                    break;
                case 2:
                    isCollected();
                    if(response.equals("收藏成功")){
                        addCollectTimes();
                        collectTimes.setText(String.valueOf(CURRENT_COLLECTTIMES+1));
                        CURRENT_COLLECTTIMES++;
                    }
                    else if(response.equals("取消收藏")){
                        reduceCollectTimes();
                        collectTimes.setText(String.valueOf(CURRENT_COLLECTTIMES-1));
                        CURRENT_COLLECTTIMES--;
                    }
                    break;
                case 3:
                    if (response.contains("error")) {
                        DisplayToast("请稍后再试..");
                    } else {
                        DisplayToast(response);
                        addCommentTimes();
                        hideKeyboard();
                        CURRENT_COMMENTTIMES++;
                        commentTimes.setText(String.valueOf(CURRENT_COMMENTTIMES));
                        Comment comment = new Comment();
                        comment.setFace(Constants.USER.getFace());
                        comment.setCommentTime(Utils.getCurrentDatetime());
                        comment.setComment(addCommentET.getText().toString());
                        comment.setReplyUser(replyUsername);
                        comment.setNickname(Constants.USER.getUsername());
                        comment.setAuthorname(nicknameTV.getText().toString());
                        if (mList == null) {
                            mList = new ArrayList<>();
                        }
                        mList.add(0, comment);
                        if (adapter == null) {
                            adapter = new CircleDetailCommnetsAdapter(mContext, mList);
                            commentsRv.setLayoutManager(layoutManager);
                            commentsRv.setAdapter(adapter);
                        }
                        adapter.notifyDataSetChanged();
                        replyUsername = "";
                        addCommentET.setText("");
                        commentPane.setVisibility(View.GONE);
                    }
                    break;
                case 4:
                    if(response.equals("已收藏")){
                        collectionIv.setImageResource(R.drawable.ic_is_colleted);
                        collectionTv.setText("已收藏");
                    } else{
                        collectionIv.setImageResource(R.drawable.ic_is_not_collected);
                        collectionTv.setText("收藏");
                    }
                    break;
                case 5:
                    isFocused();
                    DisplayToast(response);
                    break;
                case 6:
                    if(response.equals("已关注")){
                        focusBtn.setText("已关注");
                    }else{
                        focusBtn.setText("关注");
                    }
                    break;
                case 7:
                    isFavored();
                    break;
                case 8:
                    if(response.equals("已点赞")){
                        favorIv.setImageResource(R.drawable.ic_is_favor);
                        favorTv.setText("已点赞");
                    } else{
                        favorIv.setImageResource(R.drawable.ic_is_not_favor);
                        favorTv.setText("点赞");
                    }
                    break;
                case 9:
                    break;
                default:
                    Toast.makeText(mContext, "what！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            Toast.makeText(mContext, "网络链接出错！", Toast.LENGTH_SHORT).show();
        }
    }

    private void getNewsImage(final String imageName) {
        new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "Download?method=getNewsImage";
                OkHttpUtils
                        .get()//
                        .url(url)//
                        .addParams("imageName", imageName)
                        .build()//
                        .execute(new BitmapCallback() {
                            @Override
                            public void onError(Call call, Exception e, int i) {
                                DisplayToast("无法获取图片");
                            }

                            @Override
                            public void onResponse(Bitmap bitmap, int i) {
                                imageIV.setImageBitmap(bitmap);
                                dialogIv.setImageBitmap(bitmap);
                                dialog.setContentView(dialogIv);
                            }
                        });
                return 0;
            }
        }.execute();
    }

    private void getFaceImage(final String face) {
        new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                String url = Constants.BASE_URL + "Download?method=getUserFaceImage";
                OkHttpUtils
                        .get()//
                        .url(url)//
                        .addParams("face", face)
                        .build()//
                        .execute(new BitmapCallback() {
                            @Override
                            public void onError(Call call, Exception e, int i) {
                            }
                            @Override
                            public void onResponse(Bitmap bitmap, int i) {
                                faceIv.setImageBitmap(bitmap);
                            }
                        });
                return 0;
            }
        }.execute();
    }

    //保存文件到指定路径
    public void saveImageToGallery(Bitmap bmp) {
        // 首先保存图片
        String storePath = BASE_IMAGE_DOWNLOAD;
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //把文件插入到系统图库
            MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
//            Uri uri = Uri.fromFile(file);
//            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                DisplayToast("保存成功");
            } else {
                DisplayToast("保存失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击输入框以外的位置隐藏软键盘
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            //当isShouldHideInput(v, ev)为true时，表示的是点击输入框区域，则需要显示键盘，同时显示光标，反之，需要隐藏键盘、光标
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    //处理Editext的光标隐藏、显示逻辑
//                    mEdtFind.clearFocus();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

}
