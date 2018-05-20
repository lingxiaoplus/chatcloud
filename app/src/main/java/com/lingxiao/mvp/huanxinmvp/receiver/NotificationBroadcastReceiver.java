package com.lingxiao.mvp.huanxinmvp.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lingxiao.mvp.huanxinmvp.MainActivity;
import com.lingxiao.mvp.huanxinmvp.view.ChatActivity;

public class NotificationBroadcastReceiver extends BroadcastReceiver{
    public static final String TYPE = "type"; //这个type是为了Notification更新信息的
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int type = intent.getIntExtra(TYPE, -1);

        if (type != -1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(type);
        }

        if (action.equals("notification_clicked")) {
            //处理点击事件
            //创建要打开的activity意图
            Intent main = new Intent(context, MainActivity.class);
            Intent chat = new Intent(context, ChatActivity.class);
            //chat.putExtra("name",msg.getUserName());
            //延期意图  处理通知的点击事件
            Intent[] intents = new Intent[]{main,chat};
            PendingIntent pendingIntent = PendingIntent.getActivities(
                    context,
                    1,intents,
                    PendingIntent.FLAG_UPDATE_CURRENT);

        }

        if (action.equals("notification_cancelled")) {
            //处理滑动清除和点击删除事件
        }
    }
}
