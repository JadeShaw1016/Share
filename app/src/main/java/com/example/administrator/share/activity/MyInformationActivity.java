package com.example.administrator.share.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.entity.User;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.MyDialogHandler;
import com.example.administrator.share.util.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileWithBitmapCallback;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;

public class MyInformationActivity extends BaseActivity implements View.OnClickListener {

    private TextView titleText;
    private View title_back;
    private Context mContext;
    private ImageView faceIv;
    private TextView usernameTv;
    private TextView nicknameTv;
    private EditText passwordEt;
    private EditText confirmPwEt;
    private EditText signatureEt;
    private TextView sexTv;
    private Button confrimBtn;
    private File imageFile;
    private Bitmap faceBitmap;
    private LinearLayout myinfoLl;
    private AlertDialog dialog;

    //调取系统摄像头的请求码
    private static final int MY_ADD_CASE_CALL_PHONE = 6;
    //打开相册的请求码
    private static final int MY_ADD_CASE_CALL_PHONE2 = 7;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_my_information);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        faceIv = $(R.id.myinfo_face);
        confrimBtn = $(R.id.myinfo_btn_confirm);
        usernameTv = $(R.id.myinfo_tv_username);
        nicknameTv = $(R.id.myinfo_tv_nickname);
        passwordEt = $(R.id.myinfo_et_password);
        confirmPwEt = $(R.id.myinfo_et_repassword);
        signatureEt = $(R.id.myinfo_et_signature);
        sexTv = $(R.id.myinfo_tv_sex);
        titleText = $(R.id.titleText);
        title_back = $(R.id.title_back);
        myinfoLl = $(R.id.myinfo_ll);
    }

    @Override
    protected void initView() {
        mContext = this;
        confrimBtn.setOnClickListener(this);
        title_back.setOnClickListener(this);
        faceIv.setOnClickListener(this);
        titleText.setText("个人资料");
        echo();
        uiFlusHandler = new MyDialogHandler(mContext, "更新中...");
    }

    /**
     * 回显
     */
    private void echo() {
        faceIv.setImageBitmap(Constants.FACEIMAGE);
        usernameTv.setText(Constants.USER.getUsername());
        nicknameTv.setText(Constants.USER.getNickname());
        passwordEt.setText(Constants.USER.getPassword());
        confirmPwEt.setText(Constants.USER.getPassword());
        sexTv.setText(Constants.USER.getSex());
        if(Constants.USER.getSignature() != null){
            signatureEt.setText(Constants.USER.getSignature());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myinfo_btn_confirm:
                checkInfo();
                break;
            case R.id.title_back:
                finish();
                break;
            case R.id.myinfo_face:
                viewInit();
                break;
            case R.id.photograph:
                //"点击了照相";
                //  6.0之后动态申请权限 摄像头调取权限,SD卡写入权限
                //判断是否拥有权限，true则动态申请
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_ADD_CASE_CALL_PHONE);
                }
                else {
                    try {
                        //有权限,去打开摄像头
                        takePhoto();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
                break;
            case R.id.photo:
                //"点击了相册";
                //  6.0之后动态申请权限 SD卡写入权限
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_ADD_CASE_CALL_PHONE2);
                } else {
                    //打开相册
                    choosePhoto();
                }
                dialog.dismiss();
                break;
            case R.id.cancel:
                dialog.dismiss();//关闭对话框
                break;
            default:break;
        }
    }

    public void viewInit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//创建对话框
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_select_photo, null);//获取自定义布局
        builder.setView(layout);//设置对话框的布局
        dialog = builder.create();//生成最终的对话框
        dialog.show();//显示对话框

        TextView takePhotoTV = layout.findViewById(R.id.photograph);
        TextView choosePhotoTV = layout.findViewById(R.id.photo);
        TextView cancelTV = layout.findViewById(R.id.cancel);
        //设置监听
        takePhotoTV.setOnClickListener(this);
        choosePhotoTV.setOnClickListener(this);
        cancelTV.setOnClickListener(this);
    }

    private void checkInfo() {
        String password = passwordEt.getText().toString().trim();
        String repassword = confirmPwEt.getText().toString().trim();
        if( TextUtils.isEmpty(password) || TextUtils.isEmpty(repassword)){
            DisplayToast("密码不能为空！");
            return;
        }
        if(!password.equals(repassword)){
            DisplayToast("重复输入密码不正确！");
            return;
        }
        if(imageFile == null){
            updateNoFace();
        }
        else{
            updateWithFace();
        }
    }

    private void updateNoFace() {
        uiFlusHandler.sendEmptyMessage(SHOW_LOADING_DIALOG);
        String url = Constants.BASE_URL + "User?method=updateNoFace";
        OkHttpUtils
                .post()
                .url(url)
                .id(1)
                .addParams("username",Constants.USER.getUsername())
                .addParams("password", passwordEt.getText().toString().trim())
                .addParams("signature",signatureEt.getText().toString().trim())
                .build()
                .execute(new MyStringCallback());
    }

    private void updateWithFace() {
        uiFlusHandler.sendEmptyMessage(SHOW_LOADING_DIALOG);
        String url = Constants.BASE_URL + "User?method=updateWithFace";
        OkHttpUtils
                .post()
                .addFile("face", imageFile.getName(), imageFile)
                .url(url)
                .id(2)
                .addHeader("content-Type", "multipart/form-data; boundary=" + UUID.randomUUID().toString())
                .addParams("username",Constants.USER.getUsername())
                .addParams("password", passwordEt.getText().toString().trim())
                .addParams("signature",signatureEt.getText().toString().trim())
                .build()
                .execute(new MyStringCallback());
    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            User user = null;
            switch (id) {
                case 1:
                    uiFlusHandler.sendEmptyMessage(DISMISS_LOADING_DIALOG);
                    user = gson.fromJson(response, User.class);
                    // 存储用户
                    Constants.USER.setPassword(user.getPassword());
                    Constants.USER.setSignature(user.getSignature());
                    break;
                case 2:
                    uiFlusHandler.sendEmptyMessage(DISMISS_LOADING_DIALOG);
                    user = gson.fromJson(response, User.class);
                    // 存储用户
                    Constants.USER.setPassword(user.getPassword());
                    Constants.USER.setSignature(user.getSignature());
                    Constants.USER.setFace(imageFile.getName());
                    Constants.FACEIMAGE = faceBitmap;
                    break;    
                default:
                    DisplayToast("what?");
                    break;
            }
            boolean result = SharedPreferencesUtils.saveUserInfo(mContext, user);
            if (result) {
                myinfoLl.clearFocus();
                Toast.makeText(mContext, "更新成功！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "用户信息存储失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            DisplayToast("网络链接出错！");
        }
    }

    public void takePhoto() throws IOException {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        // 获取文件
        File file = createFileIfNeed("UserIcon.png");
        //拍照后原图回存入此路径下
        Uri uri;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            uri = Uri.fromFile(file);
        } else {
            /**
             * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
             * 并且这样可以解决MIUI系统上拍照返回size为0的情况
             */
            uri = FileProvider.getUriForFile(this, "com.example.administrator.share.fileprovider", file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, 1);
    }

    // 在sd卡中创建一保存图片（原图和缩略图共用的）文件夹
    private File createFileIfNeed(String fileName) throws IOException {
        String fileA = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/nbinpic";
        File fileJA = new File(fileA);
        if (!fileJA.exists()) {
            fileJA.mkdirs();
        }
        File file = new File(fileA, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 打开相册
     */
    private void choosePhoto() {
        //这是打开系统默认的相册(就是你系统怎么分类,就怎么显示,首先展示分类列表)
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, 2);
    }
    /**
     * 申请权限回调方法
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_ADD_CASE_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    takePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this,"你拒绝了请求",Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == MY_ADD_CASE_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
                Toast.makeText(this,"你拒绝了请求",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    /**
     * startActivityForResult执行后的回调方法，接收返回的图片
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode != Activity.RESULT_CANCELED) {
            String state = Environment.getExternalStorageState();
            if (!state.equals(Environment.MEDIA_MOUNTED)) return;
            // 把原图显示到界面上
            Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
            Tiny.getInstance().source(readpic()).asFile().withOptions(options).compress(new FileWithBitmapCallback() {
                @Override
                public void callback(boolean isSuccess, Bitmap bitmap, String outfile, Throwable t) {
                    saveImageToServer(bitmap, outfile);//显示图片到imgView上
                }
            });
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK
                && null != data) {
            try {
                Uri selectedImage = data.getData();//获取路径
                Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                Tiny.getInstance().source(selectedImage).asFile().withOptions(options).compress(new FileWithBitmapCallback() {
                    @Override
                    public void callback(boolean isSuccess, Bitmap bitmap, String outfile, Throwable t) {
                        saveImageToServer(bitmap, outfile);
                    }
                });
            } catch (Exception e) {
                //"上传失败");
            }
        }
    }
    /**
     * 从保存原图的地址读取图片
     */
    private String readpic() {
        String filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/nbinpic/" + "UserIcon.png";
        return filePath;
    }

    private void saveImageToServer(final Bitmap bitmap, String outfile) {
        // 这里就可以将图片文件 file 上传到服务器,上传成功后可以将bitmap设置给你对应的图片展示
        imageFile= new File(outfile);
        faceBitmap = bitmap;
        faceIv.setImageBitmap(bitmap);
    }
}