package com.example.administrator.share.util;

import android.graphics.Bitmap;
import android.os.Environment;

import com.example.administrator.share.ShareApplication;
import com.example.administrator.share.entity.User;

import java.io.File;
import java.util.List;

public class Constants {

    public final static String ERROR = "error";

    public final static String OK = "ok";

    // 用户对象
    public static User USER = SharedPreferencesUtils.getUserInfo(ShareApplication.getContextObject());

    //用户头像
    public static Bitmap FACEIMAGE;

    public static List<String> DAILYCHECKEDLIST;

    // 服务器地址
    public static String DEFAULT_URL = "http://192.168.1.4:8080/";
    public static String BASE_URL = SharedPreferencesUtils.getIPConfig(ShareApplication.getContextObject());

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
}
