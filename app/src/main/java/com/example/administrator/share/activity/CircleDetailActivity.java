package com.example.administrator.share.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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
import com.example.administrator.share.util.DateUtils;
import com.example.administrator.share.util.MyDialogHandler;
import com.example.administrator.share.widgets.ListViewWithScrollView;
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

/**
 * Created by djzhao on 17/05/02.
 */

public class CircleDetailActivity extends BaseActivity implements View.OnClickListener {

    private CircleDetailCommnetsAdapter adapter;
    private List<Comment> mList;

    private String TITLE_NAME = "圈子详情";
    private View title_back;
    private TextView titleText;

    private TextView usernameTV;
    private TextView titleTV;
    private TextView releaseTimeTV;
    private ImageView imageIV;
    private TextView contentTV;
    private ImageView collectionTv;
    private TextView authornameTv;
    private Button focusBtn;

    private LinearLayout commentPane;
    private EditText addCommentET;
    private ImageView addCommentIV;
    private boolean isShowCommentPane;


    private ListViewWithScrollView commentsLV;
    private LinearLayout commentLL;
    private LinearLayout favorLL;

    private Context mContext;

    private int newsId;
    private int fansId;
    private String replyUsername;

    private MyDialogHandler uiFlusHandler;
    private Dialog dialog;
    private ImageView dialogIv;


    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        newsId = getIntent().getIntExtra("newsId", 0);
        fansId = getIntent().getIntExtra("fansId", 0);
        setContentView(R.layout.activity_circle_detail);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        this.title_back = $(R.id.title_back);
        this.titleText = $(R.id.titleText);

        usernameTV = $(R.id.news_detail_username);
        releaseTimeTV = $(R.id.news_detail_time);
        imageIV = $(R.id.news_detail_image);
        contentTV = $(R.id.news_detail_content);
        titleTV = $(R.id.detail_title);

        commentLL = $(R.id.news_detail_add_comment);
        favorLL = $(R.id.news_detail_add_favor);

        commentPane = $(R.id.news_detail_add_commment_pane);
        addCommentET = $(R.id.news_detail_add_commment_text);
        addCommentIV = $(R.id.news_detail_add_commment_btn);

        commentsLV = $(R.id.news_detail_comment);
        collectionTv = $(R.id.iv_collection);
        authornameTv = $(R.id.news_detail_username);
        focusBtn = $(R.id.btn_focus);
        dialogIv = new ImageView(this);
    }

    @Override
    protected void initView() {
        mContext = this;
        this.titleText.setText(TITLE_NAME);

        this.title_back.setOnClickListener(this);
        commentLL.setOnClickListener(this);
        favorLL.setOnClickListener(this);
        addCommentIV.setOnClickListener(this);
        focusBtn.setOnClickListener(this);
        imageIV.setOnClickListener(this);
        dialogIv.setOnClickListener(this);
        uiFlusHandler = new MyDialogHandler(mContext, "加载中...");
        refreshData();
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
            case R.id.news_detail_add_favor:
                addNewFavor();
                break;
            case R.id.news_detail_add_commment_btn:
                addNewComment();
                break;
            case R.id.btn_focus:
                addFocus();
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

    private void refreshData() {

        uiFlusHandler.sendEmptyMessage(SHOW_LOADING_DIALOG);

        String url = Constants.BASE_URL + "News?method=getNewsDetail";
        OkHttpUtils
                .post()
                .url(url)
                .id(1)
                .addParams("newsId", newsId + "")
                .build()
                .execute(new MyStringCallback());
    }


    private void showCommemtPane() {
        isShowCommentPane = !isShowCommentPane;
        if (isShowCommentPane) {
            commentPane.setVisibility(View.VISIBLE);
            addCommentET.setHint("发表新评论");
            replyUsername = "";
            addCommentET.requestFocus();
        } else {
            commentPane.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(addCommentET.getWindowToken(), 0);
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private void addNewFavor() {
        String url = Constants.BASE_URL + "Favor?method=addNewFavor";
        OkHttpUtils
                .post()
                .url(url)
                .id(2)
                .addParams("newsId", newsId + "")
                .addParams("userId", Constants.USER.getUserId() + "")
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
        String url = Constants.BASE_URL + "Comment?method=addNewComment";
        OkHttpUtils
                .post()
                .url(url)
                .id(3)
                .addParams("newsId", newsId + "")
                .addParams("userId", Constants.USER.getUserId() + "")
                .addParams("authorName",authornameTv.getText().toString())
                .addParams("comment", commentText)
                .addParams("replyUser", replyUsername)
                .addParams("commentTime",DateUtils.getCurrentDatetime())
                .build()
                .execute(new MyStringCallback());
    }

    private void isFavored(){
        String url = Constants.BASE_URL + "Favor?method=isFavored";
        OkHttpUtils
                .post()
                .url(url)
                .id(4)
                .addParams("newsId", newsId + "")
                .addParams("userId", Constants.USER.getUserId() + "")
                .build()
                .execute(new MyStringCallback());
    }

    private void addFocus(){
        String url = Constants.BASE_URL + "Follows?method=addFocus";
        OkHttpUtils
                .post()
                .url(url)
                .id(5)
                .addParams("userId", Constants.USER.getUserId() + "")
                .addParams("fansId",fansId+"")
                .build()
                .execute(new MyStringCallback());
    }

    private void isFocused(){
        String url = Constants.BASE_URL + "Follows?method=isFocused";
        OkHttpUtils
                .post()
                .url(url)
                .id(6)
                .addParams("userId", Constants.USER.getUserId() + "")
                .addParams("fansId",fansId+"")
                .build()
                .execute(new MyStringCallback());
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
                            usernameTV.setText(circleDetail.getUsername());
                            releaseTimeTV.setText(circleDetail.getReleaseTime());
                            titleTV.setText(circleDetail.getTitle());
                            contentTV.setText(circleDetail.getContent());
                            // 加载图片
                            if (!TextUtils.isEmpty(circleDetail.getImage())) {
                                imageIV.setVisibility(View.VISIBLE);
                                imageIV.setImageResource(R.drawable.default_image);
                                getNewsImage(circleDetail.getImage());
                            } else {
                                imageIV.setVisibility(View.GONE);
                            }
                        }
                        mList = circleDetail.getComments();
                    } catch (Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        commentsLV.setAdapter(adapter);
                    }
                    uiFlusHandler.sendEmptyMessage(DISMISS_LOADING_DIALOG);
                    break;
                case 2:
                    isFavored();
                    DisplayToast(response);
                    break;
                case 3:
                    if (response.contains("error")) {
                        DisplayToast("请稍后再试..");
                    } else {
                        DisplayToast(response);
                        Comment comment = new Comment();
                        comment.setCommentTime(DateUtils.getCurrentDatetime());
                        comment.setComment(addCommentET.getText().toString());
                        comment.setReplyUser(replyUsername);
                        comment.setUsername(Constants.USER.getUsername());
                        comment.setAuthorname(authornameTv.getText().toString());
                        if (mList == null) {
                            mList = new ArrayList<Comment>();
                        }
                        mList.add(0, comment);
                        if (adapter == null) {
                            adapter = new CircleDetailCommnetsAdapter(mContext, mList);
                            commentsLV.setAdapter(adapter);
                        }
                        adapter.notifyDataSetChanged();
                        replyUsername = "";
                        addCommentET.setText("");
                        commentPane.setVisibility(View.GONE);
                    }
                    break;
                case 4:
                    if(response.equals("已收藏")){
                        collectionTv.setImageResource(R.drawable.ic_feed_is_fav);
                    } else{
                        collectionTv.setImageResource(R.drawable.ic_feed_is_not_fav);
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

    private void getNewsImage(String imageName) {
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
//        return false;
    }
}
