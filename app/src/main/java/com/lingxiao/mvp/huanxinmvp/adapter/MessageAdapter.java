package com.lingxiao.mvp.huanxinmvp.adapter;

import android.hardware.usb.UsbInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;
import com.lingxiao.mvp.huanxinmvp.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lingxiao on 17-7-15.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder>{
    private List<EMConversation> conversationList = new ArrayList<>();

    public void setConversationList(List<EMConversation> conversationList){
        this.conversationList = conversationList;
    }
    public MessageAdapter(List<EMConversation> conversationList){
        this.conversationList = conversationList;
    }
    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.list_message,null);
        MessageHolder messageHolder = new MessageHolder(view);
        return messageHolder;
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        //获取一个会话
        EMConversation emConversation = conversationList.get(position);
        //获取最近一条消息
        EMMessage lastMessage = emConversation.getLastMessage();
        //聊天的对象
        final String userName = lastMessage.getUserName();
        EMTextMessageBody textMessageBody = (EMTextMessageBody) lastMessage.getBody();
        //获取消息内容
        String message = textMessageBody.getMessage();
        //获取未读消息数量
        int unreadMsgCount = emConversation.getUnreadMsgCount();
        //时间
        long date = lastMessage.getMsgTime();
        String dateStr = DateUtils.getTimestampString(new Date(date));
        holder.time.setText(dateStr);
        holder.name.setText(userName);
        holder.message.setText(message);
        if (unreadMsgCount == 0){
            holder.unread.setVisibility(View.GONE);
        }else if (unreadMsgCount>99){
            holder.unread.setVisibility(View.VISIBLE);
            holder.unread.setText(String.valueOf(99));
        }else {
            holder.unread.setVisibility(View.VISIBLE);
            holder.unread.setText(unreadMsgCount+"");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onMsgClick(v,userName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversationList == null?0:conversationList.size();
    }

    class MessageHolder extends RecyclerView.ViewHolder{
        TextView name,message,time,unread;
        public MessageHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_msg_name);
            message = (TextView) itemView.findViewById(R.id.tv_msg_message);
            time = (TextView) itemView.findViewById(R.id.tv_msg_time);
            unread = (TextView) itemView.findViewById(R.id.tv_msg_unread);
        }
    }
    private onMsgClickListener listener;
    public interface onMsgClickListener{
        void onMsgClick(View v,String username);
    }
    public void setOnMsgClickListener(onMsgClickListener listener){
        this.listener = listener;
    }
}
