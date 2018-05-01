package com.lingxiao.mvp.huanxinmvp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.view.AcceptCallActivity;

public class CallReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        // 拨打方username
        String from = intent.getStringExtra("from");
        // call type
        String type = intent.getStringExtra("type");
        //跳转到通话页面
        //存在多次跳转，设置一下启动模式为singletop栈顶复用模式 onNewIntent方法会被调用
        Intent callIntent = new Intent(context,AcceptCallActivity.class);
        callIntent.putExtra("from",from);
        callIntent.putExtra("type",type);
        context.startActivity(callIntent);

    }

}
