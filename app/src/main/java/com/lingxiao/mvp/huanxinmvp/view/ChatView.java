package com.lingxiao.mvp.huanxinmvp.view;

import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by lingxiao on 17-7-15.
 */

public interface ChatView {
    /**
     *获取历史消息记录
     */
    void onGetHistoryMsg(List<EMMessage> messages);
    /**
     *更新消息列表
     */
    void onUpdateList();
}
