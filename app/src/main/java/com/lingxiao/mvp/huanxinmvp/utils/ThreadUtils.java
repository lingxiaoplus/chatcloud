package com.lingxiao.mvp.huanxinmvp.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 线程池
 */

public class ThreadUtils {
    //传一个Looper.getMainLooper()，保证在主线程中运行
    private static Handler handler = new Handler(Looper.getMainLooper());
    //单线程的线程池
    private static Executor executor = Executors.newSingleThreadExecutor();

    //在子线程中运行
    public static void runOnSonUIThread(Runnable r){
        executor.execute(r);
    }

    //在主线程中运行
    public static void runOnMainThread(Runnable r){
        handler.post(r);
    }
}
