package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.lingxiao.mvp.huanxinmvp.presenter.MessagePresenter;
import com.lingxiao.mvp.huanxinmvp.view.MessageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by lingxiao on 17-7-15.
 */

public class MessagePresenterImpl implements MessagePresenter{
    private MessageView messageView;
    public MessagePresenterImpl(MessageView messageView){
        this.messageView = messageView;
    }
    @Override
    public void getMessages() {
        Map<String,EMConversation> allMessages =
                EMClient.getInstance()
                        .chatManager()
                        .getAllConversations();
        Collection<EMConversation> values = allMessages.values();
        //获取会话的集合
        ArrayList<EMConversation> messageList = new ArrayList<>(values);
        //根据最近收到的消息时间的顺序对会话进行排序
        Collections.sort(messageList, new Comparator<EMConversation>() {
            @Override
            public int compare(EMConversation o1, EMConversation o2) {
                return (int) (o2.getLastMessage().getMsgTime()-o1.getLastMessage().getMsgTime());
            }
        });
        messageView.onGetAllMessages(messageList);
    }

    @Override
    public void clearAllUnreadMark() {
        EMClient.getInstance().chatManager().markAllConversationsAsRead();
        messageView.onClearAllUnreadMark();
    }
}
