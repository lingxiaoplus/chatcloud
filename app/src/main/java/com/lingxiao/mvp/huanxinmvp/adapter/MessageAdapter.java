package com.lingxiao.mvp.huanxinmvp.adapter;

import android.hardware.usb.UsbInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel_Table;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.model.UserModel_Table;
import com.lingxiao.mvp.huanxinmvp.utils.GlideHelper;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sqk.emojirelease.EmojiUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lingxiao on 17-7-15.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder>{
    private List<EMConversation> conversationList;

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
    public void onBindViewHolder (final MessageHolder holder, final int position){
        //获取一个会话
        EMConversation emConversation = conversationList.get(position);
        //获取最近一条消息
        EMMessage lastMessage = emConversation.getLastMessage();
        //聊天的对象
        final String userName = lastMessage.getUserName();
        //数据库中通过username查询对应字段
        ContactsModel model = SQLite
                .select()
                .from(ContactsModel.class)
                .where(ContactsModel_Table.contactUserName.eq(userName))
                .querySingle();

        EMMessage.Type type = lastMessage.getType();
        if (type == EMMessage.Type.TXT){
            EMTextMessageBody textMessageBody = (EMTextMessageBody) lastMessage.getBody();
            //获取消息内容
            String message = textMessageBody.getMessage();

            try {
                EmojiUtil.handlerEmojiText(holder.message,message, UIUtils.getContext());
            } catch (IOException e) {
                e.printStackTrace();
                holder.message.setText(message);
            }
        }else if (type == EMMessage.Type.IMAGE){
            holder.message.setText("图片");
        }else if (type == EMMessage.Type.VOICE){
            holder.message.setText("语音");
        }

        //获取未读消息数量
        int unreadMsgCount = emConversation.getUnreadMsgCount();
        //时间
        long date = lastMessage.getMsgTime();
        String dateStr = DateUtils.getTimestampString(new Date(date));
        holder.time.setText(dateStr);
        try {
            //有可能为空
            GlideHelper.realTimeLoading(
                    model.getProtrait(),
                    holder.headImage);
            holder.name.setText(model.getNickName());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        if (unreadMsgCount == 0){
            holder.unread.setVisibility(View.GONE);
        }else if (unreadMsgCount>99){
            holder.unread.setVisibility(View.VISIBLE);
            holder.unread.setText(String.valueOf(99));
        }else {
            holder.unread.setVisibility(View.VISIBLE);
            holder.unread.setText(unreadMsgCount+"");
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    holder.unread.setVisibility(View.GONE);
                    listener.onMsgClick(v,userName);
                }
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除
                if (listener != null){
                    listener.onMsgDelete(view,position,userName);
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
        CircleImageView headImage;
        Button deleteBtn;
        CardView cardView;
        public MessageHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_msg_name);
            message = (TextView) itemView.findViewById(R.id.tv_msg_message);
            time = (TextView) itemView.findViewById(R.id.tv_msg_time);
            unread = (TextView) itemView.findViewById(R.id.tv_msg_unread);
            headImage = itemView.findViewById(R.id.iv_msg_head);
            deleteBtn = itemView.findViewById(R.id.delete);
            cardView = itemView.findViewById(R.id.message_card);
        }
    }
    private onMsgClickListener listener;
    public interface onMsgClickListener{
        void onMsgClick(View v,String username);
        void onMsgDelete(View view,int pos,String username);
    }
    public void setOnMsgClickListener(onMsgClickListener listener){
        this.listener = listener;
    }
}
