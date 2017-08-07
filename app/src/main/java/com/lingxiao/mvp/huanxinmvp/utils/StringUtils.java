package com.lingxiao.mvp.huanxinmvp.utils;

import android.text.TextUtils;

import java.sql.Time;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

/**
 * 检验字符串是否合法
 */

public class StringUtils {
    /**
     * 检验用户名是否合法
     */
    public static boolean CheckUsername(String username){
        if (TextUtils.isEmpty(username)){
            return false;
        }else {
            return username.matches("^[a-zA-Z][0-9a-zA-Z]{4,19}$");
        }
    }
    /**
    * 检验密码的长度
    */
    public static boolean CheckPsd(String psd){
        if (TextUtils.isEmpty(psd)){
            return false;
        }else{
            return psd.matches("^[0-9a-zA-Z]{4,19}$");
        }
    }

    public static String getFirstChar(String text){
        if (TextUtils.isEmpty(text)){
            return null;
        }else {
            return text.substring(0,1).toUpperCase();
        }
    }
    public static String getDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }
    /**
     *将date格式字符串转换为时间
     */
    public static String strToDate(String strDate){
        Date date = new Date(Long.valueOf(strDate)*1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
