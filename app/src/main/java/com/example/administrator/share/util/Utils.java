package com.example.administrator.share.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String getCurrentDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 正则匹配手机号码:
     */
    public static boolean checkTel(String tel){
        Pattern p = Pattern.compile("^[1][3,4,5,7,8,9][0-9]{9}$");
        Matcher matcher = p.matcher(tel);
        return matcher.matches();
    }

    public static String hidePhoneNum(String phoneNum){
        return phoneNum.replaceAll("(?<=[\\d]{3})\\d(?=[\\d]{4})", "*");
    }
}
