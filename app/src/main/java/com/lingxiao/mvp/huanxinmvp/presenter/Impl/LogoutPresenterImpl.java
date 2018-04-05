package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import com.avos.avoscloud.AVUser;
import com.hyphenate.chat.EMClient;
import com.lingxiao.mvp.huanxinmvp.callback.MyEmCallBack;
import com.lingxiao.mvp.huanxinmvp.presenter.LogoutPresenter;
import com.lingxiao.mvp.huanxinmvp.view.MineView;

/**
 * Created by lingxiao on 17-7-16.
 */

public class LogoutPresenterImpl implements LogoutPresenter{
    private MineView mineView;
    public LogoutPresenterImpl(MineView mineView){
        this.mineView = mineView;
    }
    @Override
    public void logOut() {
        EMClient.getInstance().logout(true, new MyEmCallBack() {
            @Override
            public void success() {
                mineView.onLogOut(true,null);
            }

            @Override
            public void error(int i, String s) {
                mineView.onLogOut(false,s);
            }
        });
    }

    @Override
    public void getUserInfo() {
        final AVUser user = new AVUser();// 新建 AVUser 对象实例

    }
}
