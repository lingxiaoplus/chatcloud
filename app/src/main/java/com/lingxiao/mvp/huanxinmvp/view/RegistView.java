package com.lingxiao.mvp.huanxinmvp.view;

/**
 * Created by lingxiao on 17-6-28.
 */

public interface RegistView {
    /**
     *获取注册信息，如果注册成功，跳转到登录界面，如果注册失败，弹toast提示
     * isRegist 是否注册成功
     * username 用户名
     * pswd 密码
     * erroMsg 错误信息
     */
    void onGetRegistState(String username,String pswd,boolean isRegist,String erroMsg);
}
