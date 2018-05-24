package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.lingxiao.mvp.huanxinmvp.callback.MyEmCallBack;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.presenter.LoginPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.SpUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
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
        AVUser.logInInBackground(username, pwd, new LogInCallback<AVUser>() {
            @Override
            public void done(final AVUser avUser, AVException e) {
                if (e == null){
                    UserModel model = new UserModel();
                    model.setUsername(username);
                    model.setObjId(avUser.getObjectId());
                    model.setDesc(avUser.getString(ContentValue.DESCRIPTION));
                    model.setProtrait(avUser.getString(ContentValue.PROTRAIT));
                    model.setNickname(avUser.getString(ContentValue.NICKNAME));
                    model.setAge(avUser.getInt(ContentValue.AGE));
                    model.setObjId(avUser.getObjectId());
                    model.setPhone(avUser.getMobilePhoneNumber());
                    model.setToken(avUser.getSessionToken());
                    model.setCreateTime(avUser.getCreatedAt().getTime());
                    model.setUpdateTime(avUser.getUpdatedAt().getTime());
                    model.save();
                    SpUtils.putString(UIUtils.getContext(),
                            ContentValue.OBJECTID,avUser.getObjectId());
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
                }else {
                    loginView.onGetLoginState(username,false,e.getMessage());
                }
            }
        });

    }
}
