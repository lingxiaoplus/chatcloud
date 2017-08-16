package com.lingxiao.mvp.huanxinmvp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lingxiao on 2017/8/15.
 */

public class CacheNewsOpenHelper extends SQLiteOpenHelper{
    public CacheNewsOpenHelper(Context context) {
        super(context, "cache_info.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //用于缓存新闻的数据库
        sqLiteDatabase.execSQL("create table cache_info(_id integer primary key autoincrement,title " +
                "varchar(20),content varchar(20)," +
                "press varchar(20),createtime varchar(20),headpic varchar(20),presspic varchar(20),originurl varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
