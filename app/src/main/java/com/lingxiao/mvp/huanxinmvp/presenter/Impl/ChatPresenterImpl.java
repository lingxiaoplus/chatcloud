package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.lingxiao.mvp.huanxinmvp.model.MessageModel;
import com.lingxiao.mvp.huanxinmvp.presenter.ChatPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.ThreadUtils;
import com.lingxiao.mvp.huanxinmvp.view.ChatView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingxiao on 17-7-15.
 */

public class ChatPresenterImpl implements ChatPresenter{
    private ChatView chatView;
    public ChatPresenterImpl(ChatView chatView){
        this.chatView = chatView;
    }
    private List<EMMessage> msgList = new ArrayList<>();
    @Override
    public List<EMMessage> getChatHistoryMsg(String username) {
        /*EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
            //获取此会话的所有消息
        List<EMMessage> messages = conversation.getAllMessages();
            //SDK初始化加载的聊天记录为20条，到顶时需要去DB里获取更多
            //获取startMsgId之前的pagesize条消息，此方法获取的messages SDK会自动存入到此会话中，APP中无需再次把获取到的messages添加到会话中
        List<EMMessage> messages = conversation.loadMoreMsgFromDB(startMsgId, pagesize);*/
        EMConversation conversation =
                EMClient.getInstance()
                        .chatManager()
                        .getConversation(username);

        if (conversation != null){
            //设置所有消息为已读
            conversation.markAllMessagesAsRead();
            //先获取最近的一条消息
            EMMessage lastMsg = conversation.getLastMessage();
            String msgId = lastMsg.getMsgId();
            int allMsg = conversation.getAllMsgCount();
            List<EMMessage> emMessages = conversation.loadMoreMsgFromDB(msgId,allMsg);
            //先清空一下集合(数据库)
            msgList.clear();
            msgList.addAll(emMessages);
            //把最后一条数据添加进来
            msgList.add(lastMsg);
            chatView.onGetHistoryMsg(msgList);

            /*for (EMMessage msg:emMessages) {
                //获取消息的类型
                MessageModel model = new MessageModel();
                final EMMessage.Type type = msg.getType();
                if (type == EMMessage.Type.TXT){
                    EMTextMessageBody emMessageBody = (EMTextMessageBody) msg.getBody();
                    model.setType(0);
                    model.setContent(emMessageBody.getMessage());
                    model.setCreateTime(msg.getMsgTime());
                }
            }*/
        }
        return msgList;
    }

    @Override
    public void sendMessage(final String username, String message) {
        final EMMessage emMessage = EMMessage.createTxtSendMessage(message,username);
        messageStatusResolve(emMessage);
    }

    @Override
    public void sendVoiceMessage(String toChatUsername, String filePath, int length) {
        //filePath为语音文件路径，length为录音时间(秒)
        EMMessage message = EMMessage.
                createVoiceSendMessage(filePath, length, toChatUsername);
        messageStatusResolve(message);
    }

    @Override
    public void sendPicMessage(String toChatUsername, boolean originImg, String filePath) {
        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），
        // 需要发送原图传true
        EMMessage emMessage =
                EMMessage.createImageSendMessage(
                        filePath,
                        originImg,
                        toChatUsername);
        messageStatusResolve(emMessage);
    }

    private void messageStatusResolve(final EMMessage emMessage){
        msgList.add(emMessage);
        //添加消息发送状态的监听
        emMessage.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                chatView.onUpdateList();
            }

            @Override
            public void onError(int i, String s) {
                chatView.onUpdateList();
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
        chatView.onGetHistoryMsg(msgList);
        ThreadUtils.runOnSonUIThread(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().chatManager().sendMessage(emMessage);
            }
        });
    }
}
