package com.example.administrator.share.activity;

import static com.example.administrator.share.util.Constants.BASE_IMAGE_DOWNLOAD;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.administrator.share.R;
import com.example.administrator.share.adapter.CircleDetailCommentsAdapter;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.CircleDetail;
import com.example.administrator.share.entity.CommentListItem;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.MyDialogHandler;
import com.example.administrator.share.util.ObservableScrollView;
import com.example.administrator.share.util.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;

public class CircleDetailActivity extends BaseActivity implements View.OnClickListener {

    private CircleDetailCommentsAdapter adapter;
    private List<CommentListItem> mList;
    private String TITLE_NAME = "圈子详情";
    private View title_back;
    private TextView titleText;
    private TextView nicknameTV;
    private TextView titleTV;
    private TextView releaseTimeTV;
    private ImageView imageIV;
    private TextView contentTV;
    private FloatingActionButton commentFab;
    private FloatingActionButton collectFab;
    private FloatingActionButton favorFab;
    private Button focusBtn;
    private LinearLayout commentPane;
    private EditText addCommentET;
    private ImageView addCommentIV;
    private Context mContext;
    private int newsId;
    private int authorId;
    private String replyUsername;
    private MyDialogHandler uiFlusHandler;
    private Dialog dialog;
    private ImageView dialogIv;
    private ImageView faceIv;
    private RecyclerView commentsRv;
    private LinearLayoutManager layoutManager;
    private TextView clickTimesTv;
    private TextView collectTimesTv;
    private TextView commentTimesTv;
    private int CURRENT_COLLECTTIMES;
    private int CURRENT_COMMENTTIMES;
    private ObservableScrollView observableScrollView;
    private TextView topicTv;
    private CircleDetail circleDetail;

    private static final ThreadPoolExecutor THREADPOOL = new ThreadPoolExecutor(2, 4, 3,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        newsId = getIntent().getIntExtra("newsId", 0);
        authorId = getIntent().getIntExtra("authorId", 0);
        setContentView(R.layout.activity_circle_detail);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        title_back = $(R.id.title_back);
        titleText = $(R.id.titleText);
        nicknameTV = $(R.id.news_detail_nickname);
        releaseTimeTV = $(R.id.news_detail_time);
        imageIV = $(R.id.news_detail_image);
        contentTV = $(R.id.news_detail_content);
        titleTV = $(R.id.detail_title);
        commentFab = $(R.id.fab_cirlce_detail_comment);
        collectFab = $(R.id.fab_cirlce_detail_collection);
        favorFab = $(R.id.fab_cirlce_detail_favor);
        commentPane = $(R.id.news_detail_add_commment_pane);
        addCommentET = $(R.id.news_detail_add_commment_text);
        addCommentIV = $(R.id.news_detail_add_commment_btn);
        commentsRv = $(R.id.news_detail_comment);
        focusBtn = $(R.id.btn_focus);
        dialogIv = new ImageView(this);
        faceIv = $(R.id.iv_circle_detail_face);
        clickTimesTv = $(R.id.tv_circle_detail_click_times);
        collectTimesTv = $(R.id.tv_circle_detail_collect_times);
        commentTimesTv = $(R.id.tv_circle_detail_comment_times);
        observableScrollView = $(R.id.osv_circle_detail);
        topicTv = $(R.id.tv_circle_detail_topic);
    }

    @Override
    protected void initView() {
        mContext = this;
        this.titleText.setText(TITLE_NAME);
        this.title_back.setOnClickListener(this);
        addCommentIV.setOnClickListener(this);
        focusBtn.setOnClickListener(this);
        imageIV.setOnClickListener(this);
        dialogIv.setOnClickListener(this);
        commentFab.setOnClickListener(this);
        collectFab.setOnClickListener(this);
        favorFab.setOnClickListener(this);
        faceIv.setOnClickListener(this);
        layoutManager = new LinearLayoutManager(this);
        adapter = new CircleDetailCommentsAdapter(mContext, new ArrayList<CommentListItem>());
        commentsRv.setLayoutManager(layoutManager);
        commentsRv.setAdapter(adapter);
        uiFlusHandler = new MyDialogHandler(mContext, "加载中...");
        observableScrollViewListener();
        refreshData();
        addClickTimes();
        isCollected();
        isFavored();
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
            case R.id.fab_cirlce_detail_comment:
                showCommemtPane();
                break;
            case R.id.fab_cirlce_detail_collection:
                addNewCollection();
                break;
            case R.id.fab_cirlce_detail_favor:
                addNewFavor();
                break;
            case R.id.news_detail_add_commment_btn:
                addNewComment();
                break;
            case R.id.btn_focus:
                if (authorId == Constants.USER.getUserId()) {
                    DisplayToast("不能关注自己哦！");
                } else {
                    addFocus();
                }
                break;
            case R.id.news_detail_image:
                dialog.show();
                break;
            case R.id.iv_circle_detail_face:
                Intent intent = new Intent(this, PersonalHomepageActivity.class);
                if (circleDetail == null) {
                    circleDetail = new CircleDetail();
                }
                intent.putExtra("userId", circleDetail.getUserId());
                intent.putExtra("nickname", circleDetail.getNickname());
                intent.putExtra("face", circleDetail.getFace());
                intent.putExtra("signature", circleDetail.getSignature());
                startActivity(intent);
                break;
        }
    }

    private void observableScrollViewListener() {
        observableScrollView.setOnScrollStatusListener(new ObservableScrollView.OnScrollStatusListener() {
            @Override
            public void onScrollStop() {
                showFloatButton();
            }

            @Override
            public void onScrolling() {
                commentPane.setVisibility(View.GONE);
                hideFloatButton();
            }
        });
    }

    private void dialogListener() {
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
        hideFloatButton();
        addCommentET.setHint(R.string.new_detail_add_comment_hint);
        commentPane.setVisibility(View.VISIBLE);
        replyUsername = "";
        showKeyboard(addCommentET);
    }

    private void showFloatButton() {
        commentFab.show();
        collectFab.show();
        favorFab.show();
    }

    private void hideFloatButton() {
        commentFab.hide();
        collectFab.hide();
        favorFab.hide();
    }

    private void refreshData() {
        uiFlusHandler.sendEmptyMessage(SHOW_LOADING_DIALOG);
        THREADPOOL.execute(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "circle/getCircleDetail";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(1)
                        .addParams("newsId", String.valueOf(newsId))
                        .build()
                        .execute(new MyStringCallback());
            }
        });
    }

    //添加新收藏
    private void addNewCollection() {
        String url = Constants.BASE_URL + "collections/addNewCollection";
        OkHttpUtils
                .post()
                .url(url)
                .id(2)
                .addParams("newsId", String.valueOf(newsId))
                .addParams("userId", String.valueOf(Constants.USER.getUserId()))
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
        String url = Constants.BASE_URL + "comments/addNewComment";
        OkHttpUtils
                .post()
                .url(url)
                .id(3)
                .addParams("newsId", String.valueOf(newsId))
                .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                .addParams("authorName", nicknameTV.getText().toString())
                .addParams("comment", commentText)
                .addParams("replyUser", replyUsername)
                .build()
                .execute(new MyStringCallback());
    }

    private void isCollected() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "collections/isCollected";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(4)
                        .addParams("newsId", String.valueOf(newsId))
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    private void addFocus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "follows/addFocus";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(5)
                        .addParams("fansId", String.valueOf(Constants.USER.getUserId()))
                        .addParams("userId", String.valueOf(authorId))
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    private void isFocused() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "follows/isFocused";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(6)
                        .addParams("fansId", String.valueOf(Constants.USER.getUserId()))
                        .addParams("userId", String.valueOf(authorId))
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    //添加新点赞
    private void addNewFavor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "favors/addNewFavor";
                OkHttpUtils
                        .post()
                        .url(url)
                        .id(7)
                        .addParams("newsId", String.valueOf(newsId))
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .addParams("authorId", String.valueOf(authorId))
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    private void isFavored() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constants.BASE_URL + "favors/isFavored";
                OkHttpUtils
                        .get()
                        .url(url)
                        .id(8)
                        .addParams("newsId", String.valueOf(newsId))
                        .addParams("userId", String.valueOf(Constants.USER.getUserId()))
                        .build()
                        .execute(new MyStringCallback());
            }
        }).start();
    }

    private void addClickTimes() {
        if (authorId != Constants.USER.getUserId()) {
            THREADPOOL.execute(new Runnable() {
                @Override
                public void run() {
                    String url = Constants.BASE_URL + "circle/addClickTimes";
                    OkHttpUtils
                            .post()
                            .url(url)
                            .id(9)
                            .addParams("newsId", String.valueOf(newsId))
                            .build()
                            .execute(new MyStringCallback());
                }
            });
        }
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 1:
                    if (Constants.ERROR.equals(response)) {
                        circleDetail = null;
                    } else {
                        try {
                            circleDetail = new Gson().fromJson(response, CircleDetail.class);
                        } catch (Exception e) {
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (circleDetail != null) {
                        CURRENT_COLLECTTIMES = circleDetail.getCollectTimes();
                        CURRENT_COMMENTTIMES = circleDetail.getCommentTimes();
                        nicknameTV.setText(circleDetail.getNickname());
                        releaseTimeTV.setText(circleDetail.getReleaseTime());
                        titleTV.setText(circleDetail.getTitle());
                        contentTV.setText(circleDetail.getContent());
                        clickTimesTv.setText(String.valueOf(circleDetail.getClickTimes()));
                        collectTimesTv.setText(String.valueOf(CURRENT_COLLECTTIMES));
                        commentTimesTv.setText(String.valueOf(CURRENT_COMMENTTIMES));
                        topicTv.setText(circleDetail.getTopic());
                        // 加载图片
                        if (!TextUtils.isEmpty(circleDetail.getImage())) {
                            imageIV.setVisibility(View.VISIBLE);
                            getNewsImage(circleDetail.getImage());
                            getFaceImage(circleDetail.getFace());
                        } else {
                            imageIV.setVisibility(View.GONE);
                        }
                        mList = circleDetail.getCommentListItems();
                    }
                    if (mList != null && mList.size() > 0) {
                        adapter = new CircleDetailCommentsAdapter(mContext, mList);
                        adapter.setOnCommentButtonClickListner(new CircleDetailCommentsAdapter.OnCommentButtonClickListner() {
                            @Override
                            public void OnCommentButtonClicked(String replyUser) {
                                commentFab.hide();
                                collectFab.hide();
                                favorFab.hide();
                                commentPane.setVisibility(View.VISIBLE);
                                addCommentET.setHint("回复 " + replyUser + " 的评论");
                                replyUsername = replyUser;
                                showKeyboard(addCommentET);
                            }
                        });
                        adapter.setOnCommentDeleteClickListner(new CircleDetailCommentsAdapter.OnCommentDeleteClickListner() {
                            @Override
                            public void OnCommentDeleteClicked(int position) {
                                if (mList != null && !mList.isEmpty()) {
                                    mList.remove(position);
                                    adapter = new CircleDetailCommentsAdapter(mContext, mList);
                                    commentsRv.setLayoutManager(layoutManager);
                                    commentsRv.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                        commentsRv.setLayoutManager(layoutManager);
                        commentsRv.setAdapter(adapter);
                    }
                    uiFlusHandler.sendEmptyMessage(DISMISS_LOADING_DIALOG);
                    break;
                case 2:
                    if (response.equals("收藏成功")) {
                        collectFab.setImageResource(R.drawable.ic_is_collected);
                        collectTimesTv.setText(String.valueOf(CURRENT_COLLECTTIMES + 1));
                        CURRENT_COLLECTTIMES++;
                    } else if (response.equals("取消收藏")) {
                        collectFab.setImageResource(R.drawable.ic_is_not_collected);
                        collectTimesTv.setText(String.valueOf(CURRENT_COLLECTTIMES - 1));
                        CURRENT_COLLECTTIMES--;
                    }
                    break;
                case 3:
                    if (Constants.ERROR.equals(response)) {
                        DisplayToast("请稍后再试..");
                    } else {
                        DisplayToast("评论成功！");
                        hideKeyboard();
                        CURRENT_COMMENTTIMES++;
                        commentTimesTv.setText(String.valueOf(CURRENT_COMMENTTIMES));
                        CommentListItem commentListItem = new CommentListItem();
                        commentListItem.setFace(Constants.USER.getFace());
                        commentListItem.setCommentTime(Utils.getCurrentDatetime());
                        commentListItem.setComment(addCommentET.getText().toString());
                        commentListItem.setReplyUser(replyUsername);
                        commentListItem.setNickname(Constants.USER.getNickname());
                        commentListItem.setAuthorname(nicknameTV.getText().toString());
                        if (mList == null) {
                            mList = new ArrayList<>();
                        }
                        mList.add(0, commentListItem);
                        adapter = new CircleDetailCommentsAdapter(mContext, mList);
                        commentsRv.setLayoutManager(layoutManager);
                        commentsRv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        replyUsername = "";
                        addCommentET.getText().clear();
                        commentPane.setVisibility(View.GONE);
                        showFloatButton();
                    }
                    break;
                case 4:
                    if (Constants.OK.equals(response)) {
                        collectFab.setImageResource(R.drawable.ic_is_collected);
                    } else {
                        collectFab.setImageResource(R.drawable.ic_is_not_collected);
                    }
                    break;
                case 5:
                    if (response.equals("关注成功")) {
                        focusBtn.setText("已关注");
                    } else if (response.equals("取消关注")) {
                        focusBtn.setText("关注");
                    }
                    DisplayToast(response);
                    break;
                case 6:
                    if (response.equals("已关注")) {
                        focusBtn.setText("已关注");
                    } else {
                        focusBtn.setText("关注");
                    }
                    break;
                case 7:
                    if (response.equals("点赞成功")) {
                        favorFab.setImageResource(R.drawable.ic_is_favored);
                    } else if (response.equals("取消点赞")) {
                        favorFab.setImageResource(R.drawable.ic_is_not_favored);
                    }
                    break;
                case 8:
                    if (response.equals("已点赞")) {
                        favorFab.setImageResource(R.drawable.ic_is_favored);
                    } else {
                        favorFab.setImageResource(R.drawable.ic_is_not_favored);
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
            DisplayToast("网络链接出错！");
        }
    }

    private void getNewsImage(final String imageName) {
        Uri uri = Uri.parse(Constants.BASE_URL + "download/getImage?imageName=" + imageName);
        Glide.with(mContext).load(uri).into(imageIV);
        Glide.with(mContext).load(uri).into(dialogIv);
        dialog.setContentView(dialogIv);
    }

    private void getFaceImage(final String face) {
        Uri uri = Uri.parse(Constants.BASE_URL + "download/getImage?face=" + face);
        Glide.with(mContext).load(uri).into(faceIv);
    }

    //保存文件到指定路径
    public void saveImageToGallery(Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(BASE_IMAGE_DOWNLOAD);
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
     *
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
                if (imm != null && v != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            int[] leftTop = {0, 0};
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

    @Override
    protected void onResume() {
        isFocused();
        super.onResume();
    }
}