package com.lingxiao.mvp.huanxinmvp.presenter;

/**
 * 注册的presenter
 */

public interface RegistPresenter {
    void registUser(String username,String psd,String phone);
    void getCheckCode(String phone);
    void submitCode(String phone,String code);
}
