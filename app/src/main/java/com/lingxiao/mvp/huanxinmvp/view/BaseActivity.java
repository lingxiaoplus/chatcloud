package com.lingxiao.mvp.huanxinmvp.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.DateUtils;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.event.ExitEvent;
import com.lingxiao.mvp.huanxinmvp.event.SkinChangeEvent;
import com.lingxiao.mvp.huanxinmvp.global.ActivityManager;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.receiver.CallReceiver;
import com.lingxiao.mvp.huanxinmvp.receiver.NetworkReceiver;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.SpUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.liuguangqiang.cookie.CookieBar;

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

    private PackageManager mPmanager;
    private int versionCode;
    private String versionName = "1.0.0";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = LocalBroadcastManager.getInstance(this);
        reciver = new MyExitReciver();
        //注册
        manager.registerReceiver(reciver,new IntentFilter("com.lingxiao.finishactivity"));

        ActivityManager.getAppManager().addActivity(this);


        if (isRegisterEventBus()){
            EventBus.getDefault().register(this);
        }

        mPmanager = getPackageManager();
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

        if (isRegisterEventBus()){
            EventBus.getDefault().unregister(this);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetExitEvent(ExitEvent exitEvent){
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

    /**
     * @param windowToken View，一般为edittext  getWindowToken()
     * @param flag 软键盘隐藏时的控制参数  一般为0即可
     * @param isShow 显示还是隐藏
     */
    public void toggleSoftInput(View windowToken, int flag, boolean isShow){
        //隐藏软键盘
        InputMethodManager input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (null != input){
            if (isShow){
                windowToken.requestFocus();
                input.showSoftInput(windowToken,0);
            }else {
                input.hideSoftInputFromWindow(windowToken.getWindowToken(),flag);
            }
        }

    }

    /**
     *检查更新
     */
    public boolean checkUpdate(){
        int serverVersion = SpUtils
                .getInt(BaseActivity.this, ContentValue.VERSION_CODE,1);
        try {
            PackageInfo info = mPmanager.getPackageInfo(getPackageName(),0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionCode = serverVersion;
        }
        if (versionCode < serverVersion){
            //服务器上面有新版本
            String url = SpUtils.getString(BaseActivity.this,ContentValue.DOWNLOAD_URL,"");
            showDialog(url);
            return true;
        }else {
            return false;
        }
    }

    public String getVersionName(){
        try {
            PackageInfo info = mPmanager.getPackageInfo(getPackageName(),0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private void showDialog(final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("检测到新版本");
        builder.setMessage(SpUtils.getString(this,ContentValue.VERSION_DES,""));
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //下载
                goToInternet(UIUtils.getContext(),url);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    //跳转到网页
    public void goToInternet(Context context, String marketUrl){
        Uri uri = Uri.parse(marketUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
     */
    protected boolean isRegisterEventBus() {
        return false;
    }
}
