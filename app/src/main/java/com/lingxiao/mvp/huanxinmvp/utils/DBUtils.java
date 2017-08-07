package com.lingxiao.mvp.huanxinmvp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lingxiao.mvp.huanxinmvp.db.PhoneSqlHelper;

import java.util.ArrayList;

/**
 * Created by lingxiao on 17-7-13.
 */

public class DBUtils {
    public static Context context = null;
    public static void InitDBUtils(Context context){
        DBUtils.context = context;
    }
    /*
    *初始化联系人
    */
    public static ArrayList<String> initContact(String username){
        ArrayList<String> result = new ArrayList<>();
        if (context == null){
            new RuntimeException("请调用initDBUtils 初始化之后再使用!");
        }else {
            PhoneSqlHelper sqlHelper = new PhoneSqlHelper(context);
            SQLiteDatabase db = sqlHelper.getReadableDatabase();
            Cursor cursor = db.query("contact_info",new String[]{"contact"},"username=?",
                    new String[]{username},null,null,null);
            while (cursor.moveToNext()){
                String name = cursor.getString(0);
                result.add(name);
            }
            cursor.close();
            db.close();
        }
        return result;
    }
    /*
    *联网获取环信的数据，并保存到本地数据库
    */
    public static void upDateContactFromServer(String username,ArrayList<String> contactList){
        if (context == null){
            new RuntimeException("请调用initDBUtils 初始化之后再使用!");
        }else {
            PhoneSqlHelper sqlHelper = new PhoneSqlHelper(context);
            SQLiteDatabase db = sqlHelper.getReadableDatabase();
            db.beginTransaction();
            try {
                //把事物标记为成功
                //先删除本地所有联系人
                db.delete("contact_info","username = ?",new String[]{username});
                //把环信返回的数据保存下来
                ContentValues values = new ContentValues();
                values.put("username",username);
                for (String contact:contactList) {
                    values.put("contact",contact);
                    db.insert("contact_info",null,values);
                }
                db.setTransactionSuccessful();
            }finally {
                //结束事物，如果没有发现成功标记，会回滚数据
                db.endTransaction();
            }

        }
    }
}
