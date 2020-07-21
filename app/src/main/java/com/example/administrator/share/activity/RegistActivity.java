package com.example.administrator.share.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.share.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import dao.DBOpenHelper;
import util.pubFun;


/**
 * @programName: RegistActivity.java
 * @programFunction: the regiter page
 * @createDate: 2018/09/19
 * @author: AnneHan
 * @version:
 * xx.   yyyy/mm/dd   ver    author    comments
 * 01.   2018/09/19   1.00   AnneHan   New Create
 */
public class RegistActivity extends AppCompatActivity {

    String TAG = MainActivity.class.getCanonicalName();
    private EditText editPhone;
    private EditText editPwd;
    private Button btnRegist;
    private HashMap<String, String> stringHashMap;
    String baseUrl = "http://pr8z38.natappfree.cc";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);
        editPhone = (EditText) findViewById(R.id.editPhone);
        editPwd = (EditText) findViewById(R.id.editPwd);
        btnRegist = (Button) findViewById(R.id.btnRegist);
        stringHashMap = new HashMap<>();
    }

    public void OnMyRegistClick(View view) {
        stringHashMap.put("Telephone", editPhone.getText().toString());
        stringHashMap.put("Password", editPwd.getText().toString());

        boolean isTrue = true;
        if(pubFun.isPhoneNumberValid(editPhone.getText().toString()) == false){
            isTrue = false;
            Toast.makeText(this, "手机号格式不正确！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pubFun.isEmpty(editPwd.getText().toString())){
            isTrue = false;
            Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isTrue = true){
            //call DBOpenHelper
            DBOpenHelper helper = new DBOpenHelper(this,"qianbao.db",null,1);
            SQLiteDatabase db = helper.getWritableDatabase();
            Cursor c = db.query("user_tb",null,"userID=?",new String[]{editPhone.getText().toString()},null,null,null);
            if(c!=null && c.getCount() >= 1){
                Toast.makeText(this, "该用户已存在", Toast.LENGTH_SHORT).show();
                c.close();
            }
            else{
                //insert data
                ContentValues values= new ContentValues();
                values.put("userID",editPhone.getText().toString());
                values.put("pwd",editPwd.getText().toString());
                long rowid = db.insert("user_tb",null,values);

                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                this.finish();
            }
            db.close();
        }else{
            return;
        }

        new Thread(getRun).start();
    }

    /**
     * get请求线程
     */
    Runnable getRun = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            requestGet(stringHashMap);
        }
    };

    /**
     * get提交数据
     *
     * @param paramsMap
     */
    private void requestGet(HashMap<String, String> paramsMap) {
        try {
            String url1=baseUrl+"/users/register?";
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }

            Log.e(TAG,"params--get-->>"+tempParams.toString());
            String requestUrl = url1 + tempParams.toString();
            // 新建一个URL对象
            URL url = new URL(requestUrl);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接主机超时时间
            urlConn.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(5 * 1000);
            // 设置是否使用缓存  默认是true
            urlConn.setUseCaches(true);
            // 设置为Post请求
            urlConn.setRequestMethod("GET");
            //urlConn设置请求头信息
            //设置请求中的媒体类型信息。
            urlConn.setRequestProperty("Content-Type", "application/json");
            //设置客户端与服务连接类型
            urlConn.addRequestProperty("Connection", "Keep-Alive");
            // 开始连接
            urlConn.connect();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                String result = streamToString(urlConn.getInputStream());
                Log.e(TAG, "Get方式请求成功，result--->" + result);
            } else {
                Log.e(TAG, "Get方式请求失败");
            }
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     * @return
     */
    public String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

//    /**
//     * register event
//     * @param v
//     */
//    public void OnMyRegistClick(View v)
//    {
//        boolean isTrue = true;
//        if(pubFun.isPhoneNumberValid(editPhone.getText().toString()) == false){
//            isTrue = false;
//            Toast.makeText(this, "手机号格式不正确！", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if(pubFun.isEmpty(editPwd.getText().toString())){
//            isTrue = false;
//            Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if(isTrue = true){
//            //call DBOpenHelper
//            DBOpenHelper helper = new DBOpenHelper(this,"qianbao.db",null,1);
//            SQLiteDatabase db = helper.getWritableDatabase();
//            Cursor c = db.query("user_tb",null,"userID=?",new String[]{editPhone.getText().toString()},null,null,null);
//            if(c!=null && c.getCount() >= 1){
//                Toast.makeText(this, "该用户已存在", Toast.LENGTH_SHORT).show();
//                c.close();
//            }
//            else{
//                //insert data
//                ContentValues values= new ContentValues();
//                values.put("userID",editPhone.getText().toString());
//                values.put("pwd",editPwd.getText().toString());
//                long rowid = db.insert("user_tb",null,values);
//
//                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
//                this.finish();
//            }
//            db.close();
//        }else{
//            return;
//        }
//    }
}
