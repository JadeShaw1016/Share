package com.example.administrator.share.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.administrator.share.image.ImageLoaderConfig;
import com.example.administrator.share.util.ActivityManager;
import com.example.administrator.share.util.Constants;
import com.example.administrator.share.util.MyDialogHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String TAG = BaseActivity.class.getSimpleName();
	protected InputMethodManager imm;
	protected Handler mHandler = null;
	private TelephonyManager tManager;
	
	protected final int SHOW_LOADING_DIALOG = 0x0102;
	protected final int DISMISS_LOADING_DIALOG = 0x0103;
	protected MyDialogHandler uiFlusHandler;
	
	public void DisplayToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	public void DisplayToastLong(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	protected abstract void findViewById();

	protected String getClientOs() {
		return Build.ID;
	}

	protected String getClientOsVer() {
		return Build.VERSION.RELEASE;
	}

	protected String getCountry() {
		return Locale.getDefault().getCountry();
	}

//	protected String getDeviceId() throws Exception {
//		return this.tManager.getDeviceId();
//	}

	protected String getLanguage() {
		return Locale.getDefault().getLanguage();
	}

//	protected String getToken() {
//		return this.tManager.getSimSerialNumber();
//	}

	protected String getVersionName() throws Exception {
		return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
	}

	@SuppressLint("NewApi")
	protected void hideOrShowSoftInput(boolean paramBoolean,
			EditText paramEditText) {
		if (paramBoolean) {
			this.imm.showSoftInput(paramEditText, 0);
			return;
		}
		this.imm.hideSoftInputFromWindow(paramEditText.getWindowToken(), 0);
	}

	protected abstract void initView();

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		getWindow().setStatusBarColor(Color.TRANSPARENT);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		ActivityManager.getInstance().addActivity(this);
		if (!ImageLoader.getInstance().isInited())
			ImageLoaderConfig.initImageLoader(this, Constants.BASE_IMAGE_CACHE);
		this.tManager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
		this.imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
		
	}
	
	protected void onDestroy() {
		super.onDestroy();
	}

	protected void onPause() {
		super.onPause();
	}

	protected void onRestart() {
		super.onRestart();
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onStart() {
		super.onStart();
	}

	protected void onStop() {
		super.onStop();
	}

	protected void openActivity(Class<?> paramClass) {
		Intent localIntent = new Intent(this, paramClass);
		startActivity(localIntent);
	}

	protected void openActivity(Class<?> paramClass, Bundle bundle) {
		Intent localIntent = new Intent(this, paramClass);
		if (bundle != null)
			localIntent.putExtras(bundle);
		startActivity(localIntent);
	}
	
	protected void openActivity(Class<?> paramClass, String action) {
		Intent intent = new Intent(this, paramClass);
		if (action != null) {
			intent.setAction(action);
		}
		
		startActivity(intent);
	}

	protected void openActivity(Class<?> paramClass, String action,
			Bundle bundle) {
		Intent intent = new Intent(this, paramClass);
		if (action != null) {
			intent.setAction(action);
		}
		if (bundle != null)
			intent.putExtras(bundle);

		startActivity(intent);
	}

	protected void openActivity(String paramString) {
		openActivity(paramString, null);
	}

	protected void openActivity(String paramString, Bundle paramBundle) {
		Intent localIntent = new Intent(paramString);
		if (paramBundle != null)
			localIntent.putExtras(paramBundle);
		startActivity(localIntent);
	}

	protected void showKeyboard(final EditText et) {
		et.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
	}

	protected void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm.isActive()) {
			if (this.getCurrentFocus().getWindowToken() != null) {
				imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}
    
	/**
	 * Find the view by id in this activity
	 * 
	 * @param viewID
	 *            the view what you want to instantiation
	 * @return the view's instantiation
	 */
	@SuppressWarnings("unchecked")
	protected <T> T $(int viewID) {
		return (T) findViewById(viewID);
	}

	/**
	 * Find the view by id in appointed view
	 * 
	 * @param viewID
	 *            the view what you want to instantiation
	 * @return the view's instantiation
	 */
	@SuppressWarnings("unchecked")
	protected <T> T $with(View view, int viewID) {
		return (T) view.findViewById(viewID);
	}
	
}