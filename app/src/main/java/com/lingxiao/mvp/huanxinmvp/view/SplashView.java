package com.lingxiao.mvp.huanxinmvp.view;

/**
 * Created by lingxiao on 17-6-26.
 */

public interface SplashView {
    //获取当前的设备登录信息之后，要处理界面的跳转逻辑
    void onGetLoginState(boolean isLogin);
}
