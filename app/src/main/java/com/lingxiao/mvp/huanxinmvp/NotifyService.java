package com.lingxiao.mvp.huanxinmvp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.utils.SpUtils;
import com.lingxiao.mvp.huanxinmvp.view.ChatActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class NotifyService extends Service {
    private IBinder mBinder = new NotifyBinder();
    public NotifyService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class NotifyBinder extends Binder{
        public NotifyService getService(){
            return NotifyService.this;
        }
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
        //启动为前台服务
        startForeground(1,notification);
    }
}
