package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import com.hyphenate.chat.EMClient;
import com.lingxiao.mvp.huanxinmvp.presenter.SplashPresenter;
import com.lingxiao.mvp.huanxinmvp.view.SplashView;

/**
 * Created by lingxiao on 17-6-26.
 */

public class SplashPresenterImpl implements SplashPresenter{

    //view的接口
    private SplashView splashView;

    //构造的时候传入view接口的具体实现 通过这个实现 调用View层的业务逻辑
    public SplashPresenterImpl(SplashView splashView){
        this.splashView = splashView;
    }
    @Override
    public void checkLogin() {
        //检测是否登录过
        // isLoggedInBefore 之前登陆过
        // isConnected 已经跟环信的服务器建立了连接EMClient.getInstance().isConnected()
        if (EMClient.getInstance().isLoggedInBefore()){
            splashView.onGetLoginState(true);
        }else{
            splashView.onGetLoginState(false);
        }
    }
}
