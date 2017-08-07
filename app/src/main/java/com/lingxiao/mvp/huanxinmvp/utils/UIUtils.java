package com.lingxiao.mvp.huanxinmvp.utils;

import android.content.Context;
import android.os.Handler;
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
}
