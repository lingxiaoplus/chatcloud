package com.lingxiao.mvp.huanxinmvp.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.adapter.EMACallSession;
import com.hyphenate.util.DateUtils;
import com.lingxiao.mvp.huanxinmvp.R;

import java.util.Date;
import java.util.List;

/**
 * Created by lingxiao on 17-7-15.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    private List<EMMessage> messages;
    public void setMessages(List<EMMessage> messages){
        this.messages = messages;
    }
    public ChatAdapter(List<EMMessage> messages){
        if (messages != null){
            this.messages = messages;
        }
    }
    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 0){
            //收到消息
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.list_chat_receive,parent,false);
            //view = View.inflate(parent.getContext(), R.layout.list_chat_receive,null);
        }else {
            //发送消息
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.list_chat_send,parent,false);
            //view = View.inflate(parent.getContext(),R.layout.list_chat_send,null);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        //获取当前条目对应的消息
        EMMessage emMessage = messages.get(position);
        long date = emMessage.getMsgTime();
        //判断是否要显示时间
        if (position == 0){
            //两个时间 间隔是否很近 <30s
            if (DateUtils.isCloseEnough(date,System.currentTimeMillis())){
                holder.tv_time.setVisibility(View.GONE);
            }else {
                //使用环信的工具类 来格式化时间
                holder.tv_time.setText(DateUtils.getTimestampString(new Date(date)));
                holder.tv_time.setVisibility(View.VISIBLE);
            }
        }else {
            if (DateUtils.isCloseEnough(date,messages.get(position-1).getMsgTime())){
                //跟上一条消息的时间对比，获取两条消息的时间间隔
                holder.tv_time.setVisibility(View.GONE);
            }else {
                holder.tv_time.setText(DateUtils.getTimestampString(new Date(date)));
                holder.tv_time.setVisibility(View.VISIBLE);
            }
        }
        //显示消息的内容
        EMTextMessageBody emMessageBody = (EMTextMessageBody) emMessage.getBody();
        //获取消息内容
        String msg = emMessageBody.getMessage();
        holder.tv_message.setText(msg);
        //如果是发送的消息，需要处理发送的状态
        EMMessage.Status status = emMessage.status();
        if (emMessage.direct() == EMMessage.Direct.SEND){
            switch (status){
                case SUCCESS:
                    holder.iv_state.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.iv_state.setImageResource(R.drawable.error);
                    break;
                case INPROGRESS:
                    holder.iv_state.setImageResource(R.drawable.send_animation);
                    AnimationDrawable drawable = (AnimationDrawable) holder.iv_state.getDrawable();
                    drawable.start();
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages == null?0:messages.size();
    }
    class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView tv_time;
        TextView tv_message;
        ImageView iv_state;
        public ChatViewHolder(View itemView) {
            super(itemView);
            tv_message = (TextView) itemView.findViewById(R.id.tv_message);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            iv_state = (ImageView) itemView.findViewById(R.id.iv_state);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //Direct 消息的发送方向  send说明是自己发的  receive收到的消息
        EMMessage message = messages.get(position);
        EMMessage.Direct direct = message.direct();
        //根据direct的值来决定 究竟是发送的消息 还是收到的消息
        return direct == EMMessage.Direct.RECEIVE?0:1;
    }
}
