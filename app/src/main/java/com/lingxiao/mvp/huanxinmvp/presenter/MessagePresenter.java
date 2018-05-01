package com.lingxiao.mvp.huanxinmvp.presenter;

/**
 * Created by lingxiao on 17-7-15.
 */

public interface MessagePresenter {
    void getMessages();
    void clearAllUnreadMark();
    void deleteMessages(String username,int pos);
}
