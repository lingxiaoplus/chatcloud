package com.lingxiao.mvp.huanxinmvp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lingxiao on 17-7-13.
 */

public class PhoneSqlHelper extends SQLiteOpenHelper {
    public PhoneSqlHelper(Context context) {
        super(context, "phone.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table contact_info(_id integer" +
                " primary key autoincrement," +
                "username varchar(20),contact varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
