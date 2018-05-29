package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.presenter.RegistPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.MD5Util;
import com.lingxiao.mvp.huanxinmvp.utils.SpUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ThreadUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.lingxiao.mvp.huanxinmvp.view.RegistView;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 先去learncloud注册用户，如果注册成功，再到环信注册用户，如果环信注册失败，
 * 需要移除learncloud中的注册信息
 */

public class RegistPresenterImpl implements RegistPresenter{
    //view的引用
    private RegistView mRegistView;
    private static final String TAG = "RegistPresenterImpl";
    private String md5Psd;

    public RegistPresenterImpl(RegistView registView){
        this.mRegistView = registView;
    }
    @Override
    public void registUser(final String username, final String psd,final String phone) {
        //注册时加密
        md5Psd = psd;
        try {
            md5Psd = MD5Util.getEncryptedPwd(psd);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final AVUser user = new AVUser();// 新建 AVUser 对象实例
        user.setUsername(username);// 设置用户名
        user.setPassword(md5Psd);// 设置密码
        user.setMobilePhoneNumber(phone); //设置手机号
        //在子线程中进行
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {

                if (e == null) {
                    // 注册成功，把用户对象赋值给当前用户 AVUser.getCurrentUser()
                    //在环信注册
                    //注册失败会抛出HyphenateException
                    final String objId = AVUser.getCurrentUser().getObjectId();
                    ThreadUtils.runOnSonUIThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().createAccount(username, md5Psd);//同步方法
                                //说明注册成功
                                //在主线程中通知界面跳转
                                ThreadUtils.runOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SpUtils.putString(UIUtils.getContext(),
                                                ContentValue.KEY_USERNAME, username);
                                        SpUtils.putString(UIUtils.getContext(),
                                                ContentValue.KEY_PSD, psd);
                                        mRegistView.onGetRegistState(objId,username,md5Psd,true,null);
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
                                mRegistView.onGetRegistState(objId,username,md5Psd,false,e1.getDescription());
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

                    if (e.getCode() == 202){
                        mRegistView.onGetRegistState(null,username,psd,false,"用户名已经存在");
                    }else if (e.getCode() == 214){
                        mRegistView.onGetRegistState(null,username,psd,false,"手机号已经被注册");
                    }else {
                        mRegistView.onGetRegistState(null,username,psd,false,e.getMessage());
                    }

                }
            }
        });
    }

    @Override
    public void getCheckCode(String phone) {
        sendCode("86",phone);
    }

    @Override
    public void submitCode(String phone, String code) {
        submitCode("86",phone,code);
    }

    // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
    public void sendCode(String country, String phone) {
        // 注册一个事件回调，用于处理发送验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理成功得到验证码的结果
                    // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                    mRegistView.onGetCode(true,null);
                } else{
                    // TODO 处理错误的结果
                    Throwable error = (Throwable) data;

                    mRegistView.onGetCode(false,error.getMessage());
                }

            }
        });
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
    }

    // 提交验证码，其中的code表示验证码，如“1357”
    public void submitCode(String country, String phone, String code) {
        // 注册一个事件回调，用于处理提交验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理验证成功的结果
                    mRegistView.onSubmitCode(true,result);
                } else{
                    // TODO 处理错误的结果
                    mRegistView.onSubmitCode(false,result);
                }

            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);
    }

}
