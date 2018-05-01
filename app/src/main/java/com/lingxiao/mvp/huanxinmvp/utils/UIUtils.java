package com.lingxiao.mvp.huanxinmvp.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.lingxiao.mvp.huanxinmvp.global.MyApplication;

/**
 * Created by lingxiao on 17-6-28.
 */

public class UIUtils {
    public static Context getContext(){
        return MyApplication.getContext();
    }
    public static int getMainThreadId(){
        return MyApplication.getMainThreadId();
    }
    public static Handler getHandler(){
        return MyApplication.getmHandler();
    }
    public static View inflateView(int inflate){
        View view = View.inflate(getContext(),inflate,null);
        return view;
    }

    //////////////////判断是否运行在主线程///////////
    public static boolean isRunOnUIThread(){
        //在android6.0系统中不行
      /*  int mypid = android.os.Process.myPid();
        if (mypid == getMainThreadId()){
            return true;
        }
        return false;*/
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
    //运行在主线程
    public static void runOnUIThread(Runnable r){
        if(isRunOnUIThread()){
            //如果是主线程，就直接运行
            r.run();
        }else{
            //如果在子线程，就借助handler让其在主线程运行
            getHandler().post(r);
        }
    }
}
