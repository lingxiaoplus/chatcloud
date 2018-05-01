package com.lingxiao.mvp.huanxinmvp.view;

import com.hyphenate.chat.EMConversation;

import java.util.List;

/**
 * Created by lingxiao on 17-7-15.
 */

public interface MessageView {
    void onGetAllMessages(List<EMConversation> conversationList);
    void onClearAllUnreadMark();
    void onDelete(boolean result,String msg);
}
