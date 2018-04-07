package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import com.avos.avoscloud.AVUser;
import com.hyphenate.chat.EMClient;
import com.lingxiao.mvp.huanxinmvp.callback.MyEmCallBack;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.presenter.UserCardPresenter;
import com.lingxiao.mvp.huanxinmvp.view.UserCardView;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * Created by lingxiao on 17-7-16.
 */

public class UserCardPresenterImpl implements UserCardPresenter {
    private UserCardView mineView;
    public UserCardPresenterImpl(UserCardView mineView){
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
        UserModel model = SQLite
                .select()
                .from(UserModel.class)
                .querySingle();
        mineView.onGetUserInfo(model);
    }
}
