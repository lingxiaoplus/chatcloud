package com.lingxiao.mvp.huanxinmvp.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.hyphenate.EMError;
import com.hyphenate.util.DateUtils;
import com.lingxiao.mvp.huanxinmvp.event.ExitEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

/**
 * Created by lingxiao on 17-6-26.
 */

public class BaseActivity extends AppCompatActivity{

    private ProgressDialog progressDialog;
    private LocalBroadcastManager manager;
    private MyExitReciver reciver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = LocalBroadcastManager.getInstance(this);
        reciver = new MyExitReciver();
        //注册
        manager.registerReceiver(reciver,new IntentFilter("com.lingxiao.finishactivity"));
    }
    public void StartActivity(Class clzz,boolean isFinish){
        startActivity(new Intent(getApplicationContext(),clzz));
        if (isFinish){
            finish();
        }
    }
    /**
     *显示进度条
     */
    public void showProgressDialog(String msg){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    public void cancleProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //可能activity被销毁了，对话框还在
        if (progressDialog != null){
            progressDialog.dismiss();
        }
        //注销广播接受者
        if (reciver != null){
            manager.unregisterReceiver(reciver);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    void onGetExitEvent(ExitEvent exitEvent){
        String msg = null;
        switch (exitEvent.exitType){
            case EMError.USER_REMOVED:
                msg = "您的账号已经被服务端删除......";
                break;
            case EMError.USER_LOGIN_ANOTHER_DEVICE:
                msg = "您的账号于"+ DateUtils.getTimestampString(new Date())+"已经在其它设备登录,请重新登录,如非本人操作" +
                        ",请及时修改密码";
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("下线通知");
        builder.setMessage(msg);
        builder.setPositiveButton("重新登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //发广播关闭所有activity  LocalBroadcastManager 通过它发送的广播 只能被自己的应用受到
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
                manager.sendBroadcast(new Intent("com.lingxiao.finishactivity"));
                //打开登录页面
                StartActivity(LoginActivity.class,true);
            }
        });
        builder.show();
    }
    private class MyExitReciver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.lingxiao.finishactivity")){
                finish();
            }
        }
    }

}
