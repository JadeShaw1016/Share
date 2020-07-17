package com.example.administrator.share.view;//这里换成你自己的

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.administrator.share.BottomDialog;
import com.example.administrator.share.fragment.InformationFragment;
import com.example.administrator.share.fragment.MeFragment;
import com.example.administrator.share.fragment.FirstPageFragment;
import com.example.administrator.share.fragment.CircleFragment;
import com.example.administrator.share.R;


public class MainMenuActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener, View.OnClickListener, BottomDialog.OnCenterItemClickListener{

    private BottomNavigationBar bottomNavigationBar;
    int lastSelectedPosition = 0;
    private String TAG = MainActivity.class.getSimpleName();
    private FirstPageFragment mFirstPage;
    private CircleFragment mMyGame;
    private InformationFragment mCommunity;
    private MeFragment mMy;
    /**
     * 退出时间
     */
    private long exitTime;
    private BottomDialog bottomDialog;

    BottomNavigationItem bottomNavigationItem0 = new BottomNavigationItem(R.drawable.icon_selected_firstpage,"");
    BottomNavigationItem bottomNavigationItem1 = new BottomNavigationItem(R.drawable.icon_selected_circle,"");
    BottomNavigationItem bottomNavigationItem2 = new BottomNavigationItem(R.drawable.icon_main_add,"");
    BottomNavigationItem bottomNavigationItem3 = new BottomNavigationItem(R.drawable.icon_selected_message,"");
    BottomNavigationItem bottomNavigationItem4 = new BottomNavigationItem(R.drawable.icon_selected_my,"");
    BottomNavigationItem bottomNavigationItem333 = new BottomNavigationItem(R.drawable.icon_unselected_message,"");

    BadgeItem badgeItem = new BadgeItem().setBackgroundColor(Color.RED).setText("99").setHideOnSelect(true);//角标

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        /**
         * bottomNavigation 设置
         */

        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar.setAutoHideEnabled(true);//自动隐藏

        /** 导航基础设置 包括按钮选中效果 导航栏背景色等 */
        bottomNavigationBar
                .setTabSelectedListener(this)
                .setMode(BottomNavigationBar.MODE_FIXED)
                /**
                 *  setMode() 内的参数有三种模式类型：
                 *  MODE_DEFAULT 自动模式：导航栏Item的个数<=3 用 MODE_FIXED 模式，否则用 MODE_SHIFTING 模式
                 *  MODE_FIXED 固定模式：未选中的Item显示文字，无切换动画效果。
                 *  MODE_SHIFTING 切换模式：未选中的Item不显示文字，选中的显示文字，有切换动画效果。
                 */

                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                /**
                 *  setBackgroundStyle() 内的参数有三种样式
                 *  BACKGROUND_STYLE_DEFAULT: 默认样式 如果设置的Mode为MODE_FIXED，将使用BACKGROUND_STYLE_STATIC
                 *                                    如果Mode为MODE_SHIFTING将使用BACKGROUND_STYLE_RIPPLE。
                 *  BACKGROUND_STYLE_STATIC: 静态样式 点击无波纹效果
                 *  BACKGROUND_STYLE_RIPPLE: 波纹样式 点击有波纹效果
                 */

//                .setActiveColor("#2c2c2c") //选中颜色
//                .setInActiveColor("#bfbfbf") //未选中颜色
                .setBarBackgroundColor("#ffffff");//导航栏背景色

        bottomNavigationItem2
                .setActiveColor("#FFD23F")
                .setInActiveColor("#FFD23F");
        bottomNavigationItem0
                .setInactiveIcon(ContextCompat.getDrawable(this,R.drawable.icon_unselected_firstpage))
                .setActiveColor("#404040")
                .setInActiveColor("#404040");
        bottomNavigationItem1
                .setInactiveIcon(ContextCompat.getDrawable(this,R.drawable.icon_unselected_circle))
                .setActiveColor("#404040")
                .setInActiveColor("#404040");
        bottomNavigationItem3
                .setInactiveIcon(ContextCompat.getDrawable(this,R.drawable.icon_unselected_message))
                .setActiveColor("#404040")
                .setInActiveColor("#404040");
        bottomNavigationItem4
                .setInactiveIcon(ContextCompat.getDrawable(this,R.drawable.icon_unselected_my))
                .setActiveColor("#404040")
                .setInActiveColor("#404040");


        bottomNavigationItem333.setBadgeItem(badgeItem);
        /** 添加导航按钮 */
        bottomNavigationBar
                .addItem(bottomNavigationItem0)
                .addItem(bottomNavigationItem1)
                .addItem(bottomNavigationItem2)
                .addItem(bottomNavigationItem333)
                .addItem(bottomNavigationItem4)
                .setFirstSelectedPosition(lastSelectedPosition )
                .initialise(); //initialise 一定要放在 所有设置的最后一项
        setDefaultFragment();//设置默认导航栏
    }

    /**
     * 设置默认导航栏
     */
    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction =fm.beginTransaction() ;
        mFirstPage = FirstPageFragment.newInstance("首页");
        transaction.replace(R.id.fragment_container,mFirstPage);
        transaction.commit();
    }


    /**
     * 设置导航选中的事件
     */
    @Override
    public void onTabSelected(int position) {
        Log.d(TAG, "onTabSelected() called with: " + "position = [" + position + "]");
        FragmentManager fm = this.getSupportFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                if (mFirstPage == null) {
                    mFirstPage = FirstPageFragment.newInstance("首页");
                }
                transaction.replace(R.id.fragment_container, mFirstPage);
                break;
            case 1:
                if (mMyGame == null) {
                    mMyGame = CircleFragment.newInstance("圈子");
                }
                transaction.replace(R.id.fragment_container, mMyGame);
                break;
            case 2:
                bottomDialog = new BottomDialog(this, R.layout.dialog_layout,
                        new int[]{R.id.dialog_cancel,R.id.dialog_text1,R.id.dialog_text2});
                bottomDialog.setOnCenterItemClickListener(this);
                bottomDialog.show();

                break;
            case 3:
                if(mCommunity == null){
                    mCommunity = InformationFragment.newInstance("消息");
                }
                transaction.replace(R.id.fragment_container,mCommunity);
                break;
            case 4:
                if(mMy == null){
                    mMy = MeFragment.newInstance("我的");
                }
                transaction.replace(R.id.fragment_container,mMy);
                break;
            default:
                break;
        }

        transaction.commit();// 事务提交
    }

    /**
     * 设置未选中Fragment 事务
     */
    @Override
    public void onTabUnselected(int position) {

    }

    /**
     * 设置释放Fragment 事务
     */
    @Override
    public void onTabReselected(int position) {

    }




    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            // 重写键盘事件分发，onKeyDown方法某些情况下捕获不到，只能在这里写
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainMenuActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View view) {
        bottomDialog.show();
    }

    @Override
    public void OnCenterItemClick(BottomDialog dialog, View view) {
        Intent intent=null;
        switch (view.getId())
        {
            case R.id.dialog_text1:
                Toast.makeText(MainMenuActivity.this,"发布作品",Toast.LENGTH_SHORT).show();
                intent = new Intent(MainMenuActivity.this,AddnewsActivity.class);
                startActivity(intent);
                break;
            case R.id.dialog_cancel:
                Toast.makeText(MainMenuActivity.this,"取消按钮",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
