package com.example.administrator.share.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.startsmake.mainnavigatetabbar.widget.BadgeView;


/**
 * Created by Administrator on 2017/3/29 0029.
 * 测试BadgeView效果
 * 方式1：jar包使用，  badgeview.jar
 * 方式2：复制代码使用 BadgeView.java
 */

public class BadgeViewTwo extends Activity implements View.OnClickListener {
    private Button button9;
    private Button button10;
    private BadgeView badgeViewTwo;
    private ListView listview;
    private Button button11;
    private MyBaseAdapter listAdapter;
    private Button button12;
    private Button button13;
    private Button button14;
    private BadgeView badgeView;
    private Button button15;
    private ImageButton imagebutton16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_view_two);
        initView();
        badgeViewTwo = new BadgeView(this);
        badgeViewTwo.setBadgeCount(1);
        badgeViewTwo.setTargetView(button10);

        badgeView = new BadgeView(this);
        badgeView.setBadgeCount(22);
        badgeView.setBadgeGravity(Gravity.RIGHT | Gravity.TOP);
        badgeView.setTargetView(button9);
    }

    private void initView() {
        button9 = (Button) findViewById(R.id.button9);

        button9.setOnClickListener(this);
        button10 = (Button) findViewById(R.id.button10);
        button10.setOnClickListener(this);
        listview = (ListView) findViewById(R.id.listview);
        //listview.setOnClickListener(this);
        button11 = (Button) findViewById(R.id.button11);
        button11.setOnClickListener(this);
        button12 = (Button) findViewById(R.id.button12);
        button12.setOnClickListener(this);
        button13 = (Button) findViewById(R.id.button13);
        button13.setOnClickListener(this);
        button14 = (Button) findViewById(R.id.button14);
        button14.setOnClickListener(this);
        button15 = (Button) findViewById(R.id.button15);
        button15.setOnClickListener(this);
        imagebutton16 = (ImageButton) findViewById(R.id.imagebutton16);
        imagebutton16.setOnClickListener(this);
    }

    boolean flag;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //button需要设置android:background="@null",防止徽章角标被遮挡
            case R.id.button9:
                //模拟点赞和取消点赞
                if (flag) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //减少点赞
                            badgeView.decrementBadgeCount(1);

                        }
                    }, 1000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //增加点赞
                            badgeView.incrementBadgeCount(1);

                        }
                    }, 1000);
                }
                flag = !flag;

                break;
            case R.id.button10:
                //思路：判断上一次状态，如果是隐藏，则显示
                badgeViewTwo.setVisibility(badgeViewTwo.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                break;
            case R.id.button11:
                //展示listview列表
                listAdapter = new MyBaseAdapter();
                listview.setAdapter(listAdapter);
                break;
            case R.id.button12:
                final BadgeView badgeView2 = new BadgeView(this);
                //badgeView2.setText("文字");
                badgeView2.setText("文字");
                //设置字体
                badgeView2.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC));
                //设置阴影
                badgeView2.setShadowLayer(5, -1, -1, Color.GREEN);
                badgeView2.setTargetView(button12);
                //2秒后更换字体
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        badgeView2.setText("new", TextView.BufferType.EDITABLE);

                    }
                }, 2000);
                break;

            case R.id.button13:
                BadgeView badgeView3 = new BadgeView(this);

                //badgeView3.setText("0");
                //设置0无效果，不会展示。。。
                //通过" "+"0"代替
                badgeView3.setText(" " + "0");
                //badgeView3.setBackground(12,Color.BLUE);
                //设置具体的颜色
                badgeView3.setBackground(12, Color.parseColor("#9b2eef"));
                badgeView3.setTargetView(button13);
                break;
            case R.id.button14:
                //修改背景图片
                final BadgeView badgeView4 = new BadgeView(this);
                badgeView4.setTargetView(button14);
                badgeView4.setText("1");

                badgeView4.setBackgroundResource(R.drawable.icon_add);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        badgeView4.setBackgroundResource(R.drawable.icon_album);
                    }
                }, 2000);

                break;
            case R.id.button15:
                //显示没有文字的徽章
                BadgeView badgeView5 = new BadgeView(this);
                badgeView5.setTargetView(button15);
                badgeView5.setText("  ");
                break;
            case R.id.imagebutton16:
                //适配@style/AppTheme：用ImageButton代替Button
                BadgeView badgeView6 = new BadgeView(this);
                badgeView6.setText("99+");
                badgeView6.setTargetView(imagebutton16);
                break;
        }
    }

    class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 15;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = View.inflate(parent.getContext(), R.layout.item_message, null);
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) convertView.findViewById(R.id.tv_badge);
                //                viewHolder.text.setText(position);
                //报错
                viewHolder.badge = new BadgeView(parent.getContext());
                viewHolder.badge.setTargetView(viewHolder.text);
                viewHolder.badge.setBadgeGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                viewHolder.badge.setBadgeMargin(8);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.text.setText("111");
            viewHolder.badge.setBadgeCount(position);

            return convertView;
        }
    }

    static class ViewHolder {
        TextView text;
        //需要在这里定义
        BadgeView badge;
    }
}