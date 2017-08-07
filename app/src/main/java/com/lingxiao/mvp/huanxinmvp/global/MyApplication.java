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
import android.os.Handler;

import com.avos.avoscloud.AVOSCloud;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;
import com.lingxiao.mvp.huanxinmvp.MainActivity;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.event.ExitEvent;
import com.lingxiao.mvp.huanxinmvp.event.PhoneChangedEvent;
import com.lingxiao.mvp.huanxinmvp.utils.DBUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ThreadUtils;
import com.lingxiao.mvp.huanxinmvp.view.ChatActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.Iterator;
import java.util.List;

/**
 * 全局的application
 */

public class MyApplication extends Application{
    private EMOptions options;
    private static Handler mHandler;
    private static Context mContext;
    private static int mainThreadId;
    private SoundPool soundPool;
    private int foregroundSound;
    private int backgroundSound;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mainThreadId = android.os.Process.myPid();
        mHandler = new Handler();
        //初始化环信sdk
        initEasyMobe();

        //初始化leancloud的sdk
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"V8YIQ6I9vYpfNFUTKQPsSTGH-9Nh9j0Va","xOfaV4IJIzzCsCHIzU1zTBkE");

        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可     开启调试日志
        AVOSCloud.setDebugLogEnabled(true);

        //初始化数据库
        DBUtils.InitDBUtils(this);

        //初始化声音池
        initSoundPool();

        initGetMsgListener();

        //注册一个监听状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
        //初始化fresco
        Fresco.initialize(this);
        
    }

    private void initGetMsgListener() {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                //收到消息
                EventBus.getDefault().post(list);
                if (isInBackground()){
                    soundPool.play(backgroundSound,1,1,0,0,1);
                    sendNotify(list.get(0));
                }else {
                    soundPool.play(foregroundSound,1,1,0,0,1);
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageRead(List<EMMessage> list) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {

            }
        });
    }

    private void sendNotify(EMMessage msg){
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        //设置通知点击之后消失
        builder.setAutoCancel(true);
        //设置小图标
        builder.setSmallIcon(R.mipmap.message);
        //设置标题
        builder.setContentTitle("您有一条新的消息");
        EMTextMessageBody body = (EMTextMessageBody) msg.getBody();
        //内容加进来
        builder.setContentText(body.getMessage());
        //设置大图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.avatar3));
        builder.setContentInfo("来自"+msg.getUserName());

        //创建要打开的activity意图
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        Intent chat = new Intent(getApplicationContext(), ChatActivity.class);

        chat.putExtra("contact",msg.getUserName());
        Intent[] intents = new Intent[]{main,chat};
        //延期意图  处理通知的点击事件
        PendingIntent pendingIntent = PendingIntent.getActivities(
                getApplicationContext(),
                1,intents,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //给通知设置点击事件
        builder.setContentIntent(pendingIntent);
        //创建notifiction
        Notification notification = builder.build();
        //发送通知
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1,notification);
    }
    private void initSoundPool() {
        //soundpool 构造 第一个参数 这个池子中管理几个音频
        //第二个参数 音频的类型 一般传入AudioManager.STREAM_MUSIC
        //第三个参数 声音的采样频率 但是 没有用默认值使用0
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        foregroundSound = soundPool.load(getApplicationContext(), R.raw.duan, 1);
        backgroundSound = soundPool.load(getApplicationContext(), R.raw.yulu,1);
    }

    private void initEasyMobe() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        //options.setAcceptInvitationAlways(false);

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

    private class MyConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {

        }

        @Override
        public void onDisconnected(final int i) {
            ThreadUtils.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (i == EMError.USER_REMOVED){
                        //账号已经被移除了
                        //EventBus.getDefault().post(new e);
                        EventBus.getDefault().post(new ExitEvent(EMError.USER_REMOVED));
                    }else if (i == EMError.USER_LOGIN_ANOTHER_DEVICE){
                        //账号在其他设备登录
                        EventBus.getDefault().post(new ExitEvent(EMError.USER_LOGIN_ANOTHER_DEVICE));
                    }else {
                        if (NetUtils.hasNetwork(getApplicationContext())){
                            //连接不到聊天服务器
                        }else {
                            //网络问题，请检查是否联网
                        }
                    }
                }
            });
        }
    }
    /**
     *判断当前应用是否处于后台
     */
    private boolean isInBackground(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runTacks = manager.getRunningTasks(50);
        ActivityManager.RunningTaskInfo info = runTacks.get(0);
        //获取栈顶的activity 的包名
        ComponentName componentName = info.topActivity;
        //判断包名是否相等
        if (componentName.getPackageName().equals(getPackageName())){
            return false;
        }
        return true;
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

}
