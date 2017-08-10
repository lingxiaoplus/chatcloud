package com.lingxiao.mvp.huanxinmvp.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by lingxiao on 2017/8/10.
 */

public class NetworkReceiver extends BroadcastReceiver{
    private Handler mHandler;
    public NetworkReceiver(Handler mHandler){
        this.mHandler = mHandler;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Message message = new Message();
        // 这个监听wifi的打开与关闭，与wifi的连接无关
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    break;
                default:
                    break;
            }
        }

        // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager
        // .WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
        // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
        // 当然刚打开wifi肯定还没有连接到有效的无线
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State state = networkInfo.getState();
                boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                if (isConnected) {
                    Log.i("code", "onReceive: 连接成功了");
                    //message.what = 1;
                } else {
                    Log.i("code", "onReceive: 没有连接");
                    //message.what = 0;
                }
            }
        }
        // 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。.
        // 最好用的还是这个监听。wifi如果打开，关闭，以及连接上可用的连接都会接到监听。见log
        // 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.isConnected()) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi
                        Log.e("code", "当前WiFi连接可用 ");
                        message.what = 2;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        // connected to the mobile provider's data plan
                        Log.e("code", "当前移动网络连接可用 ");
                        message.what = 3;
                    }
                } else {
                    Log.e("code", "当前没有网络连接，请确保你已经打开网络 ");
                    message.what = 4;
                }
                Log.e("code", "info.getTypeName()" + activeNetwork.getTypeName());
                Log.e("code", "getSubtypeName()" + activeNetwork.getSubtypeName());
                Log.e("code", "getState()" + activeNetwork.getState());
                Log.e("code", "getDetailedState()"
                        + activeNetwork.getDetailedState().name());
                Log.e("code", "getDetailedState()" + activeNetwork.getExtraInfo());
                Log.e("code", "getType()" + activeNetwork.getType());
            } else {   // not connected to the internet
                Log.e("code", "当前没有网络连接，请确保你已经打开网络 ");
                message.what = 4;
            }
        }
        mHandler.sendMessage(message);
    }
}
