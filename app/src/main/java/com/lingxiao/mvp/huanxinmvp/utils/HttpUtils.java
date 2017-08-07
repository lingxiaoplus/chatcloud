package com.lingxiao.mvp.huanxinmvp.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by lingxiao on 17-7-16.
 */

public class HttpUtils {
    public static void doGet(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
