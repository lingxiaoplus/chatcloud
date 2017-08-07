package com.lingxiao.mvp.huanxinmvp.presenter;

import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by lingxiao on 17-7-15.
 */

public interface ChatPresenter {
    /**
     *获取聊天记录
     */
    List<EMMessage> getChatHistoryMsg(String username);

    /**
     *发送消息
     */
    void sendMessage(String username,String message);
}
