package com.lingxiao.mvp.huanxinmvp.presenter;

/**
 * 通讯录的presenter
 */

public interface PhonePresenter {
    void initContact();
    void updateContact();
    void deleteContact(String username);
}
