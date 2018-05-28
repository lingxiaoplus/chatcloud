package com.lingxiao.mvp.huanxinmvp.service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

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
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.global.App;
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
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class InitIalizeService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.lingxiao.mvp.huanxinmvp.service.action.init";
    //private static final String ACTION_BAZ = "com.lingxiao.mvp.huanxinmvp.service.action.BAZ";
    private SoundPool soundPool;
    private int foregroundSound;
    private int backgroundSound;
    private EMOptions options;
    private static Context mContext;
    public InitIalizeService() {
        super("InitIalizeService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startInit(Context context) {
        Intent intent = new Intent(context, InitIalizeService.class);
        intent.setAction(ACTION_FOO);
        context.startService(intent);
        mContext = context;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                performInit();
            }
        }
    }

    /**
     * 初始化操作
     */
    private void performInit() {


        //初始化数据库
        DBUtils.InitDBUtils(this);

        //初始化声音池
        initSoundPool();

        //initGetMsgListener();

        //注册一个监听状态的listener
        EMClient.getInstance().addConnectionListener(new InitIalizeService.MyConnectionListener());

        //初始化dbflow
        FlowManager.init(this);

        //初始化skin皮肤
        initSkinLib((App) mContext);

        //初始化mob短信验证
        MobSDK.init(this,"255ed51d720ec","a4a3fcb876482f2cf4c6065b6f32b8ca");
    }

    private void initSkinLib(App app) {
        //初始化皮肤加载框架
        SkinCompatManager.withoutActivity(app)                         // 基础控件换肤初始化
                .addInflater(new SkinMaterialViewInflater())            // material design 控件换肤初始化[可选]
                .addInflater(new SkinConstraintViewInflater())          // ConstraintLayout 控件换肤初始化[可选]
                .addInflater(new SkinCardViewInflater())                // CardView v7 控件换肤初始化[可选]
                .setSkinStatusBarColorEnable(true)                     // 关闭状态栏换肤，默认打开[可选]
                .setSkinWindowBackgroundEnable(true)                   // 关闭windowBackground换肤，默认打开[可选]
                .loadSkin();
    }

    private void initGetMsgListener() {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                //收到消息
                EventBus.getDefault().post(list);
                boolean messageRemind = SpUtils.getBoolean(mContext, ContentValue.MESSAGE_REMIND,true);
                boolean messageVoice = SpUtils.getBoolean(mContext,ContentValue.MESSAGE_VOICE,true);
                boolean messageShake = SpUtils.getBoolean(mContext,ContentValue.MESSAGE_SNAKE,true);
                if (messageRemind){
                    //语音提示
                    if (messageVoice){
                        if (isInBackground()){
                            soundPool.play(backgroundSound,1,1,0,0,1);
                            sendNotify(list.get(0));
                        }else {
                            soundPool.play(foregroundSound,1,1,0,0,1);
                        }
                    }
                    //震动
                    if (messageShake){
                        Vibrator vibrator = (Vibrator)mContext
                                .getSystemService(mContext.VIBRATOR_SERVICE);
                        vibrator.vibrate(1000); //震动时间
                    }
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

        chat.putExtra("name",msg.getUserName());
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
}
