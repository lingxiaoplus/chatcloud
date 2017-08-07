package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lingxiao.mvp.huanxinmvp.presenter.RegistPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.ThreadUtils;
import com.lingxiao.mvp.huanxinmvp.view.RegistView;

/**
 * 先去learncloud注册用户，如果注册成功，再到环信注册用户，如果环信注册失败，
 * 需要移除learncloud中的注册信息
 */

public class RegistPresenterImpl implements RegistPresenter{
    //view的引用
    private RegistView mRegistView;
    private static final String TAG = "RegistPresenterImpl";

    public RegistPresenterImpl(RegistView registView){
        this.mRegistView = registView;
    }
    @Override
    public void registUser(final String username, final String psd) {
        final AVUser user = new AVUser();// 新建 AVUser 对象实例
        user.setUsername(username);// 设置用户名
        user.setPassword(psd);// 设置密码
        //在子线程中进行
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {

                if (e == null) {
                    // 注册成功，把用户对象赋值给当前用户 AVUser.getCurrentUser()
                    //在环信注册
                    //注册失败会抛出HyphenateException
                    ThreadUtils.runOnSonUIThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().createAccount(username, psd);//同步方法
                                //说明注册成功
                                //在主线程中通知界面跳转
                                ThreadUtils.runOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRegistView.onGetRegistState(username,psd,true,null);
                                    }
                                });
                            } catch (HyphenateException e1) {
                                e1.printStackTrace();
                                //注册失败，删除用户
                                try {
                                    user.delete();
                                } catch (AVException e2) {
                                    e2.printStackTrace();
                                }
                                //环信注册失败    通知界面显示注册失败
                                mRegistView.onGetRegistState(username,psd,false,e1.getDescription());
                                Log.i(TAG, "环信注册失败");
                            }
                        }
                    });

                } else {
                    // 失败的原因可能有多种，常见的是用户名已经存在。
                    //learncloud注册失败    通知界面显示注册失败
                    try {
                        user.delete();
                    } catch (AVException e2) {
                        e2.printStackTrace();
                    }
                    mRegistView.onGetRegistState(username,psd,false,e.getMessage());
                    Log.i(TAG, "腾讯云注册失败");
                }
            }
        });
    }
}
