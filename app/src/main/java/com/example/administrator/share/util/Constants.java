package com.example.administrator.share.util;

import android.graphics.Bitmap;
import android.os.Environment;

import com.example.administrator.share.ShareApplication;
import com.example.administrator.share.entity.User;

import java.io.File;
import java.util.List;

public class Constants {

    public final static String ERROR = "error";

    // 用户对象
    public static User USER = SharedPreferencesUtils.getUserInfo(ShareApplication.getContextObject());

    //用户头像
    public static Bitmap FACEIMAGE;

    public static List<String> DAILYCHECKEDLIST;

    // 应用名称
    public static String APP_NAME = "";

    // 服务器地址
//	public static String BASE_URL = "http://81.70.145.250:8080/ShareServer/";
    public static String BASE_URL = "http://192.168.1.4:8080/";

    // 保存参数文件夹名称
    public static final String SHARED_PREFERENCE_NAME = "share_prefs";

    // SDCard路径
    public static final String SD_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + File.separator
            + "com.share";

    // 图片存储路径
    public static final String BASE_PATH = SD_PATH + "/images";

    // 缓存图片路径
    public static final String BASE_IMAGE_CACHE = BASE_PATH + "/cache";

    // 下载保存图片路径
    public static final String BASE_IMAGE_DOWNLOAD = BASE_PATH + "/download";

    // 需要分享的图片
    public static final String SHARE_FILE = BASE_PATH + "/QrShareImage.png";

}
