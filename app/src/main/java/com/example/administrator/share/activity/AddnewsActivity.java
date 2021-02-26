package com.example.administrator.share.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.DateUtils;
import com.example.administrator.share.util.MyDialogHandler;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.UUID;

import okhttp3.Call;

public class AddnewsActivity extends BaseActivity implements View.OnClickListener{

    private ImageView mIv1;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView mTakePhoto, mChoosePhoto;
    private ImageView picture;
    private Uri imageUri;
    private EditText titleEt;
    private EditText contentEt;
    private TextView titlelLenTv;
    private Button releaseBtn;
    private File imageFile;
    private LinearLayout remindBgLl;
    private TextView remindTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnews);
        findViewById();
        initView();
        textListener();
    }

    @Override
    protected void findViewById() {
        titleEt = (EditText) findViewById(R.id.add_news_et_share_title);
        contentEt = (EditText) findViewById(R.id.add_news_et_share_content);
        titlelLenTv = (TextView) findViewById(R.id.add_news_tv_title_length);
        mIv1 = (ImageView) findViewById(R.id.title_back);
        mTakePhoto = (ImageView) findViewById(R.id.add_news_iv_photo);
        mChoosePhoto = (ImageView) findViewById(R.id.choose_from_album);
        picture = (ImageView) findViewById(R.id.iv_picture);
        releaseBtn = (Button) findViewById(R.id.add_news_btn_release);
        remindBgLl = (LinearLayout) findViewById(R.id.ll_remind_bg);
        remindTv = (TextView) findViewById(R.id.tv_remind);
    }

    @Override
    protected void initView() {
        mIv1.setOnClickListener(this);
        mTakePhoto.setOnClickListener(this);
        mChoosePhoto.setOnClickListener(this);
        releaseBtn.setOnClickListener(this);
        uiFlusHandler = new MyDialogHandler(this, "登录中...");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                this.finish();
                break;
            case R.id.add_news_iv_photo:
                takePhoto();
                break;
            case R.id.choose_from_album:
                choosePhoto();
                break;
            case R.id.add_news_btn_release:
                checkInfo();
                break;
            default:
                break;
        }

    }

    private void textListener(){
        titleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length =charSequence.length();
                titlelLenTv.setText(length+"/"+12);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void releaseNews() {
        String titleStr = titleEt.getText().toString();
        String contentStr = contentEt.getText().toString();
        uiFlusHandler.sendEmptyMessage(DISMISS_LOADING_DIALOG);
        String url = Constants.BASE_URL + "News?method=releaseNewsWithImage";
        OkHttpUtils
                .post()
                .addFile("image", imageFile.getName(), imageFile)
                .url(url)
                .id(1)
                .addHeader("content-Type", "multipart/form-data; boundary=" + UUID.randomUUID().toString())
                .addParams("title", titleStr)
                .addParams("content", contentStr)
                .addParams("userId", Constants.USER.getUserId() + "")
                .addParams("releaseTime", DateUtils.getCurrentDatetime())
                .build()
                .execute(new MyStringCallback());
    }

    private void takePhoto(){
        //创建file对象，用于存储拍照后的图片；
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");

        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(AddnewsActivity.this,
                    "com.example.administrator.share.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }

        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void choosePhoto(){
        if (ContextCompat.checkSelfPermission(AddnewsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddnewsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
        remindBgLl.setVisibility(View.INVISIBLE);
        remindTv.setVisibility(View.INVISIBLE);
    }

    private void checkInfo() {
        String titleStr = titleEt.getText().toString();
        String contentStr = contentEt.getText().toString();
        if (TextUtils.isEmpty(titleStr)) {
            DisplayToast("请输入一个标题");
            titleEt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(contentStr)) {
            DisplayToast("请输入想说的话");
            contentEt.requestFocus();
            return;
        }
        if (imageFile == null || !imageFile.exists()) {
            DisplayToast("请上传你的作品图片");
            return;
        }
        releaseNews();
    }


    //打开相册
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bm);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {  //4.4及以上的系统使用这个方法处理图片；
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);  //4.4及以下的系统使用这个方法处理图片
                    }
                }
                break;
            default:
                break;
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }


    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    /**
     * 4.4及以上的系统使用这个方法处理图片
     *
     * @param data
     */
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果document类型的Uri,则通过document来处理
            String docID = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docID.split(":")[1];     //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/piblic_downloads"), Long.valueOf(docID));
                imagePath = getImagePath(contentUri, null);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通方式使用
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri，直接获取路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
        imageFile = new File(imagePath);
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {
            uiFlusHandler.sendEmptyMessage(DISMISS_LOADING_DIALOG);
            switch (id) {
                case 1:
                    if (response.contains("success")) {
                        DisplayToast("圈子发布成功");
                        finish();
                    } else {
                        DisplayToast("请稍后再试");
                    }
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
