package com.lingxiao.mvp.huanxinmvp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lingxiao.mvp.huanxinmvp.model.FindBean;

import java.util.ArrayList;

/**
 * Created by lingxiao on 2017/8/15.
 */

public class CacheNewsDao {
    private CacheNewsOpenHelper cacheNewsOpenHelper;
    private CacheNewsDao(Context context){
        cacheNewsOpenHelper = new CacheNewsOpenHelper(context);
    }
    private static CacheNewsDao cacheNewsDao = null;
    public static CacheNewsDao getInstance(Context context){
        if (cacheNewsDao == null){
            cacheNewsDao = new CacheNewsDao(context);
        }
        return cacheNewsDao;
    }
    /**
     *增加一条记录
     *  title 新闻标题
     *  content 新闻内容
     *  press 新闻来源
     *  creattime 发布日期
     *  expartime 缓存有效期
     * String title,String content,String press,String creattime,String expartime
     */
    public void insert(ArrayList<FindBean.DetailMsg> msg){
        SQLiteDatabase db = cacheNewsOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < msg.size(); i++) {
            contentValues.put("title",msg.get(i).title);
            contentValues.put("content",msg.get(i).brief);
            contentValues.put("press",msg.get(i).site_info.name);
            contentValues.put("createtime",msg.get(i).pub_date);
            contentValues.put("headpic",msg.get(i).headpic);
            contentValues.put("presspic",msg.get(i).site_info.pic);
            contentValues.put("originurl",msg.get(i).origin_url);
            db.insert("cache_info",null,contentValues);
        }
        contentValues.clear();
        db.close();
    }
    /**
     *根据标题删除缓存
     * @param title
     */
    public void delete(String title){
        SQLiteDatabase db = cacheNewsOpenHelper.getWritableDatabase();
        db.delete("cache_info","title = ?",new String[]{title});
        db.close();
    }

    /**
     *查询所有记录
     */
    public ArrayList<FindBean.DetailMsg> findAll(){
        SQLiteDatabase db = cacheNewsOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("cache_info",new String[]{"title",
                "content","press","createtime","headpic","presspic","originurl"},null,null,null,null,"_id desc");
        ArrayList<FindBean.DetailMsg> cacheList = new ArrayList<>();
        while (cursor.moveToNext()){
            /*CacheNewsInfo info = new CacheNewsInfo();
            info.title = cursor.getString(0);
            info.content = cursor.getString(1);
            info.press = cursor.getString(2);
            info.createTime = cursor.getString(3);
            info.exparTime = cursor.getString(4);*/
            FindBean.DetailMsg msg = new FindBean.DetailMsg();
            msg.site_info = new FindBean.siteInfo();
            msg.title = cursor.getString(0);
            msg.brief = cursor.getString(1);
            msg.site_info.name = cursor.getString(2);
            msg.pub_date = cursor.getString(3);
            msg.headpic = cursor.getString(4);
            msg.site_info.pic = cursor.getString(5);
            msg.origin_url = cursor.getString(6);
            cacheList.add(msg);
        }
        cursor.close();
        db.close();
        return  cacheList;
    }

    /**
     *判断过期时间，如果缓存过期，要删除所有记录
     */
    public void deleteAll(){
        SQLiteDatabase db = cacheNewsOpenHelper.getWritableDatabase();
        db.execSQL("DELETE FROM "+"cache_info"+";");
        db.close();
    }
}
