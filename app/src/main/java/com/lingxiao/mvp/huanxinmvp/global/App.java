package com.lingxiao.mvp.huanxinmvp.global;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Debug;
import android.os.Handler;
import android.os.Vibrator;

import com.avos.avoscloud.AVOSCloud;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;
import com.lingxiao.mvp.huanxinmvp.MainActivity;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.event.ExitEvent;
import com.lingxiao.mvp.huanxinmvp.event.PhoneChangedEvent;
import com.lingxiao.mvp.huanxinmvp.service.InitIalizeService;
import com.lingxiao.mvp.huanxinmvp.utils.DBUtils;
import com.lingxiao.mvp.huanxinmvp.utils.SpUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ThreadUtils;
import com.lingxiao.mvp.huanxinmvp.view.ChatActivity;
import com.mob.MobSDK;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Iterator;
import java.util.List;

import skin.support.SkinCompatManager;
import skin.support.app.SkinCardViewInflater;
import skin.support.constraint.app.SkinConstraintViewInflater;
import skin.support.design.app.SkinMaterialViewInflater;

/**
 * 全局的application
 */

public class App extends Application{

    private static Handler mHandler;
    private static Context mContext;
    private static int mainThreadId;
    /**
     * 判断app是否被杀
     */
    private static int appStatus = -1;
    @Override
    public void onCreate() {
        super.onCreate();
        //Debug.startMethodTracing("GithubApp");
        mContext = getApplicationContext();
        mainThreadId = android.os.Process.myPid();
        mHandler = new Handler();

        InitIalizeService.startInit(this);
        //Debug.stopMethodTracing();
    }



    public static Context getContext(){
        return mContext;
    }
    public static int getMainThreadId(){
        return mainThreadId;
    }
    public static Handler getmHandler(){
        return mHandler;
    }
    public static int getAppStatus(){
        return appStatus;
    }
    public static void setAppStatus(int status){
        appStatus = status;
    }
}
