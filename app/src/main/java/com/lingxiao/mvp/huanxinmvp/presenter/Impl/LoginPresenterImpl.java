package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.lingxiao.mvp.huanxinmvp.callback.MyEmCallBack;
import com.lingxiao.mvp.huanxinmvp.presenter.LoginPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.view.LoginView;

/**
 * Created by lingxiao on 17-6-28.
 */

public class LoginPresenterImpl implements LoginPresenter{
    private LoginView loginView;
    public LoginPresenterImpl(LoginView loginView){
        this.loginView = loginView;
    }
    @Override
    public void login(final String username, final String pwd) {
        EMClient.getInstance().login(username, pwd, new MyEmCallBack() {
            @Override
            public void success() {
                loginView.onGetLoginState(username,true,null);
            }

            @Override
            public void error(int i, String s) {
                loginView.onGetLoginState(username,false,s);
            }
        });
    }
}
