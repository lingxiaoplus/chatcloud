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
        //初始化环信sdk
        initEasyMobe();
    }


    private void initEasyMobe() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        boolean isaccept = SpUtils
                .getBoolean(this,ContentValue.FRIEND,true);
        options.setAcceptInvitationAlways(isaccept);

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null ||!processAppName.equalsIgnoreCase(this.getPackageName())) {
            // Log.e(TAG, "enter the service process!");

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        //初始化环信sdk
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

        //添加好友监听
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
                EventBus.getDefault().post(new PhoneChangedEvent(username,false));
            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时调用此方法   通过eventbus发布消息
                EventBus.getDefault().post(new PhoneChangedEvent(username,true));
            }

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                try {
                    //接受邀请
                    EMClient.getInstance().contactManager().acceptInvitation(username);
                    //拒绝邀请
                    //EMClient.getInstance().contactManager().declineInvitation(username);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFriendRequestAccepted(String s) {
                //好友请求被同意
            }

            @Override
            public void onFriendRequestDeclined(String s) {
                //好友请求被拒绝
            }
        });
    }
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
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
