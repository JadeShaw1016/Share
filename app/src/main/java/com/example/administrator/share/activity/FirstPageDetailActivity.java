//package com.example.administrator.share.activity;
//
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.example.administrator.share.R;
//import com.example.administrator.share.base.BaseActivity;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class FirstPageDetailActivity extends BaseActivity implements View.OnClickListener {
//
//    private ImageView backIv;
//    private ImageView imageIv;
//    private TextView descriptionTv;
//    private TextView timeTv;
//
//    private String uri;
//    private CharSequence charSequence;
//    private String formattime;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        uri = getIntent().getStringExtra("url");
//        charSequence = getIntent().getCharSequenceExtra("charSequence");
//        formattime = getIntent().getStringExtra("formattime");
//        setContentView(R.layout.activity_firstpage_detail);
//        findViewById();
//        initView();
//    }
//
//    @Override
//    protected void findViewById() {
//        backIv = (ImageView) findViewById(R.id.title_back);
//        imageIv = (ImageView) findViewById(R.id.iv_1);
//        descriptionTv = (TextView) findViewById(R.id.tv_description);
//        timeTv = (TextView) findViewById(R.id.tv_time);
//    }
//
//    @Override
//    protected void initView() {
//        backIv.setOnClickListener(this);
//        try {
//            imageIv.setImageBitmap(getBitmap(uri));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        descriptionTv.setText(charSequence);
//        timeTv.setText(formattime);
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.title_back:
//                finish();
//                break;
//        }
//    }
//
//    private static Bitmap getBitmap(String path) throws IOException {
//        URL url = new URL(path);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setConnectTimeout(5000);
//        conn.setRequestMethod("GET");
//        if (conn.getResponseCode() == 200){
//            InputStream inputStream = conn.getInputStream();
////            Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(inputStream),150,150,true);
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            conn.disconnect();
//            return bitmap;
//        }
//        conn.disconnect();
//        return null;
//    }
//}
