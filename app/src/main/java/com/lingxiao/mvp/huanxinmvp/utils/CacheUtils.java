package com.lingxiao.mvp.huanxinmvp.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by lingxiao on 2017/7/3.
 */
public class CacheUtils {

    //写缓存       以url为key    以json为value
    public static void setCache(String url,String json){
        File fileCacheDir = UIUtils.getContext().getCacheDir(); //获取应用缓存文件夹
        //生成缓存文件
        File file = new File(fileCacheDir,url);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            //写入缓存有效期   半个小时
            long deadLine = System.currentTimeMillis() +30*1000*60;
            writer.write(deadLine+"\n");    //在第一行写入缓存时间    换行
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.close(writer);
        }
    }

    //读缓存
    public static String getCache(String url){
        File fileCacheDir = UIUtils.getContext().getCacheDir(); //获取应用缓存文件夹
        //生成缓存文件
        File file = new File(fileCacheDir,url);
        if (file.exists()){
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                long time = System.currentTimeMillis();
                String deadLine = reader.readLine();
                long deadTime = Long.parseLong(deadLine);
                if (time < deadTime){
                    //缓存有效
                    StringBuffer sb = new StringBuffer();
                    String line;
                    while((line = reader.readLine()) != null){
                        sb.append(line);
                    }
                    return sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                IOUtils.close(reader);
            }
        }
        return null;
    }
}
