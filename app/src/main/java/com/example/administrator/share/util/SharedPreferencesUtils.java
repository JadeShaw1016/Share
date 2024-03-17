package com.example.administrator.share.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.example.administrator.share.entity.User;

public class SharedPreferencesUtils {

    /**
     * 保存用户名密码
     *
     * @param context
     * @param user
     * @return
     */
    public static boolean saveUserInfo(Context context, User user) {
        try {
            //1.通过Context对象创建一个SharedPreference对象
            //name:sharedpreference文件的名称    mode:文件的操作模式
            SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            //2.通过sharedPreferences对象获取一个Editor对象
            Editor editor = sharedPreferences.edit();
            //3.往Editor中添加数据
            editor.putInt("userId", user.getUserId());
            editor.putString("face", user.getFace());
            editor.putString("username", user.getUsername());
            editor.putString("nickname", user.getNickname());
            editor.putString("password", user.getPassword());
            editor.putString("sex", user.getSex());
            editor.putString("signature", user.getSignature());
            //4.提交Editor对象
            editor.apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateUserInfo(Context context, String key, String value) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取用户名密码
     *
     * @param context
     * @return
     */
    public static User getUserInfo(Context context) {
        User user = new User();
        try {
            //1.通过Context对象创建一个SharedPreference对象
            SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            //2.通过sharedPreference获取存放的数据
            //key:存放数据时的key   defValue: 默认值,根据业务需求来写
            int userId = sharedPreferences.getInt("userId", 0);
            String password = sharedPreferences.getString("password", "");
            String username = sharedPreferences.getString("username", "");
            String nickname = sharedPreferences.getString("nickname", "");
            String sex = sharedPreferences.getString("sex", "男");
            String face = sharedPreferences.getString("face", "");
            String signature = sharedPreferences.getString("signature", "");

            user.setUserId(userId);
            user.setPassword(password);
            user.setUsername(username);
            user.setNickname(nickname);
            user.setSex(sex);
            user.setFace(face);
            user.setSignature(signature);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存储服务器信息
     *
     * @param context 上下文环境
     * @param ip ip地址
     * @param port 端口
     * @return 是否存储成功
     */
    public static boolean saveIPConfig(Context context, String ip, String port) {
        try {
            SharedPreferences preferences = context.getSharedPreferences("serverConnect", Context.MODE_PRIVATE);
            Editor edit = preferences.edit();
            edit.putString("ip", ip);
            edit.putString("port", port);
            return edit.commit();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取服务器配置信息
     *
     * @param context 上下文环境
     * @return 服务器url地址
     */
    public static String getIPConfig(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("serverConnect", Context.MODE_PRIVATE);
        String ip = preferences.getString("ip", "");
        if (TextUtils.isEmpty(ip)) {
            return Constants.DEFAULT_URL;
        } else {
            return "http://" + ip + ":" + preferences.getString("port", "") + "/";
        }
    }

    /**
     * 清除sp缓存
     *
     * @param context 上下文环境
     */
    public static void clear(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
