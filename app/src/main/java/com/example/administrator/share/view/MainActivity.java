package com.example.administrator.share.view;

import util.pubFun;
import dao.DBOpenHelper;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;


public class MainActivity extends AppCompatActivity {

    private Button mBtnLogin;
    private EditText editPhone;
    private EditText editPwd;
    private TextView txtForgetPwd;
    private TextView txtStartRegist;
    /**
     * 退出时间
     */
    private long exitTime;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editPhone = (EditText) findViewById(R.id.editPhone);
        editPwd = (EditText) findViewById(R.id.editPwd);
        mBtnLogin = (Button) findViewById(R.id.btn_login);

        txtForgetPwd = (TextView) findViewById(R.id.txtForgetPwd);
        txtForgetPwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线

        txtStartRegist = (TextView) findViewById(R.id.txtStartRegist);
        txtStartRegist.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线


        //控制登录用户名图标大小
        EditText editText1 = (EditText) findViewById(R.id.editPhone);
        Drawable drawable1 = getResources().getDrawable(R.drawable.icon_register);
        drawable1.setBounds(0, 0, 50, 50);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        editText1.setCompoundDrawables(drawable1, null, null, null);//只放左边

        //控制登录用户名图标大小
        EditText editText2 = (EditText) findViewById(R.id.editPwd);
        Drawable drawable2 = getResources().getDrawable(R.drawable.icon_password);
        drawable2.setBounds(0, 0, 50, 45);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        editText2.setCompoundDrawables(drawable2, null, null, null);//只放左边
    }


    public void OnMyLoginClick(View view){
        if(pubFun.isEmpty(editPhone.getText().toString()) || pubFun.isEmpty(editPwd.getText().toString())){
            Toast.makeText(this, "手机号或密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        //call DBOpenHelper
        DBOpenHelper helper = new DBOpenHelper(this,"qianbao.db",null,1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.query("user_tb",null,"userID=? and pwd=?",new String[]{editPhone.getText().toString(),editPwd.getText().toString()},null,null,null);
        if(c!=null && c.getCount() >= 1){
            String[] cols = c.getColumnNames();
            while(c.moveToNext()){
                for(String ColumnName:cols){
                    Log.i("info",ColumnName+":"+c.getString(c.getColumnIndex(ColumnName)));
                }
            }
            c.close();
            db.close();

            //将登陆用户信息存储到SharedPreferences中
            SharedPreferences mySharedPreferences= getSharedPreferences("setting", Activity.MODE_PRIVATE); //实例化SharedPreferences对象
            SharedPreferences.Editor editor = mySharedPreferences.edit();//实例化SharedPreferences.Editor对象
            editor.putString("userID", editPhone.getText().toString()); //用putString的方法保存数据
            editor.commit(); //提交当前数据
            Intent intent=new Intent(MainActivity.this,MainMenuActivity.class);
            MainActivity.this.startActivity(intent);
            this.finish();
        }
        else{
            Toast.makeText(this, "手机号或密码输入错误！", Toast.LENGTH_SHORT).show();
        }
    }


    public void OnMyRegistClick(View view)  {
        Toast.makeText(MainActivity.this, "开始注册", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this,RegistActivity.class);
        //intent.putExtra("info", "No66778899");
        MainActivity.this.startActivity(intent);
    }


    public void OnMyResPwdClick(View view){
        Intent intent=new Intent(MainActivity.this,ResPwdActivity.class);
        MainActivity.this.startActivity(intent);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            // 重写键盘事件分发，onKeyDown方法某些情况下捕获不到，只能在这里写
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        mBtnLogin = (Button) findViewById(R.id.btn_login);
//        mBtnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//                Intent intent;
//                intent = new Intent(MainActivity.this, MainMenuActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        editPhone = (EditText) findViewById(R.id.editPhone);
//        editPhone.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.d("edittext", charSequence.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//    }



}
