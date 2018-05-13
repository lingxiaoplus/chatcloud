package com.lingxiao.mvp.huanxinmvp.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;

import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.lingxiao.mvp.huanxinmvp.global.MyApplication;

import java.util.ArrayList;
import java.util.List;

import skin.support.SkinCompatManager;

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

    /**
     * 根据color后缀加载替换皮肤
     */
    public static void changeSkinDef(String skinName){
        SkinCompatManager
                .getInstance()
                .loadSkin(skinName,
                        SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN); // 后缀加载
        //SkinCompatManager
        // .getInstance().
        // loadSkin("night",
        // SkinCompatManager.SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN); // 前缀加载
    }

    public static void restoreDefaultTheme(){
        // 恢复应用默认皮肤
        SkinCompatManager.getInstance().restoreDefaultTheme();
    }

    /**
     * 改变SVG图片着色
     * @param imageView
     * @param iconResId svg资源id
     * @param color 期望的着色
     */
    public static void changeSVGColor(ImageView imageView, int iconResId, int color){
        @SuppressLint("RestrictedApi") Drawable drawable =  AppCompatDrawableManager.get().getDrawable(getContext(), iconResId);
        imageView.setImageDrawable(drawable);
        Drawable.ConstantState state = drawable.getConstantState();
        Drawable drawable1 = DrawableCompat.wrap(state == null ? drawable : state.newDrawable()).mutate();
        drawable1.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        DrawableCompat.setTint(drawable1, ContextCompat.getColor(getContext(), color));
        imageView.setImageDrawable(drawable1);
    }

    public static void changeSVGColor(AppCompatImageView imageView, int iconResId, int color){
        @SuppressLint("RestrictedApi") Drawable drawable =  AppCompatDrawableManager.get().getDrawable(getContext(), iconResId);
        imageView.setImageDrawable(drawable);
        Drawable.ConstantState state = drawable.getConstantState();
        Drawable drawable1 = DrawableCompat.wrap(state == null ? drawable : state.newDrawable()).mutate();
        drawable1.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        DrawableCompat.setTint(drawable1, ContextCompat.getColor(getContext(), color));
        imageView.setImageDrawable(drawable1);
    }

    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningService = myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }
}
