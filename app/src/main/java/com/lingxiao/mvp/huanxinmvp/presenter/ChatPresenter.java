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
     *发送文本消息
     */
    void sendMessage(String toChatUsername,String message);

    /**
     *发送语音消息
     * @param filePath 语音文件路径
     * @param length 时长
     */
    void sendVoiceMessage(String toChatUsername,String filePath,int length);

    /**
     *发送图片消息
     * @param filePath 图片文件路径
     * @param originImg 是否发原图
     */
    void sendPicMessage(String toChatUsername,boolean originImg,String filePath);
}
