package com.example.administrator.share.activity;//这里换成你自己的

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.base.BaseActivity;
import com.example.administrator.share.dialog.BottomDialog;
import com.example.administrator.share.fragment.CircleFragment;
import com.example.administrator.share.fragment.FirstPageFragment;
import com.example.administrator.share.fragment.MeFragment;
import com.example.administrator.share.fragment.MessageFragment;
import com.startsmake.mainnavigatetabbar.widget.BadgeView;
import com.startsmake.mainnavigatetabbar.widget.MainNavigateTabBar;


public class MainMenuActivity extends BaseActivity implements View.OnClickListener, BottomDialog.OnCenterItemClickListener{

    /**
     * 退出时间
     */
    private long exitTime;
    private BottomDialog bottomDialog;

    private static final String TAG_PAGE_HOME = "首页";
    private static final String TAG_PAGE_CIRCLE = "圈子";
    private static final String TAG_PAGE_PUBLISH = "发布";
    private static final String TAG_PAGE_MESSAGE = "消息";
    private static final String TAG_PAGE_PERSON = "我的";


    private MainNavigateTabBar mNavigateTabBar;

    @Override
    protected void findViewById() {

        mNavigateTabBar = findViewById(R.id.mainTabBar);

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        findViewById();
        initView();
        BadgeView badgeView = new BadgeView(this);

        mNavigateTabBar.onRestoreInstanceState(savedInstanceState);
        mNavigateTabBar.addTab(FirstPageFragment.class, new MainNavigateTabBar.TabParam(R.drawable.icon_unselected_firstpage, R.drawable.icon_selected_firstpage, TAG_PAGE_HOME));
        mNavigateTabBar.addTab(CircleFragment.class, new MainNavigateTabBar.TabParam(R.drawable.icon_unselected_circle, R.drawable.icon_selected_circle, TAG_PAGE_CIRCLE));
        mNavigateTabBar.addTab(null, new MainNavigateTabBar.TabParam(0, 0, TAG_PAGE_PUBLISH));
        mNavigateTabBar.addTab(MessageFragment.class, new MainNavigateTabBar.TabParam(R.drawable.icon_unselected_message, R.drawable.icon_selected_message, TAG_PAGE_MESSAGE,badgeView));
        mNavigateTabBar.addTab(MeFragment.class, new MainNavigateTabBar.TabParam(R.drawable.icon_unselected_my, R.drawable.icon_selected_my, TAG_PAGE_PERSON));

        mNavigateTabBar.disPlayBadgeCount(2, 2);

    }


    public void onClickPublish(View v) {
        bottomDialog = new BottomDialog(this, R.layout.dialog_layout,
                new int[]{R.id.dialog_cancel,R.id.dialog_release,R.id.dialog_signin});
        bottomDialog.setOnCenterItemClickListener(this);
        bottomDialog.show();

    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void OnCenterItemClick(BottomDialog dialog, View view) {
        Intent intent;
        switch (view.getId())
        {
            case R.id.dialog_release:
                intent = new Intent(MainMenuActivity.this,AddnewsActivity.class);
                startActivity(intent);
                break;
            case R.id.dialog_signin:
                intent = new Intent(MainMenuActivity.this,BeforeDateCheckActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
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

}
