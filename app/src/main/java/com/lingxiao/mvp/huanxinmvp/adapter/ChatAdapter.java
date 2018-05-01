package com.lingxiao.mvp.huanxinmvp.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.chat.adapter.EMACallSession;
import com.hyphenate.util.DateUtils;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.adapter.chatholder.ChatPicHolder;
import com.lingxiao.mvp.huanxinmvp.adapter.chatholder.ChatTextHolder;
import com.lingxiao.mvp.huanxinmvp.adapter.chatholder.ChatVoiceHolder;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel_Table;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.model.UserModel_Table;
import com.lingxiao.mvp.huanxinmvp.utils.GlideHelper;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lingxiao on 17-7-15.
 */

public class ChatAdapter extends RecyclerView.Adapter<BaseHolder>{
    private List<EMMessage> messages;
    private int TYPE_TEXT_SEND = 0;//文字发送方
    private int TYPE_TEXT_RECEIVER = 1;//文字接收方

    private int TYPE_VOICE_SEND = 2; //语音
    private int TYPE_VOICE_RECEIVER = 3;

    private int TYPE_PIC_SEND = 4;//图片
    private int TYPE_PIC_RECEIVER = 5;

    private String msg;
    private String picPath;
    private int mVoiceLen;

    public void setMessages(List<EMMessage> messages){
        this.messages = messages;
    }
    public ChatAdapter(List<EMMessage> messages){
        if (messages != null){
            this.messages = messages;
        }
    }
    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        BaseHolder holder = null;
        if (viewType == TYPE_VOICE_RECEIVER){
            //接收方 文本
            view = inflateView(parent,R.layout.cell_chat_text_left);
        }else if (viewType == TYPE_TEXT_SEND){
            //发送方 文本
            view = inflateView(parent,R.layout.cell_chat_text_right);
        }else if (viewType == TYPE_PIC_RECEIVER){
            view = inflateView(parent,R.layout.cell_chat_pic_left);
        }else if (viewType == TYPE_PIC_SEND){
            view = inflateView(parent,R.layout.cell_chat_pic_right);
        }else if (viewType == TYPE_VOICE_RECEIVER){
            view = inflateView(parent,R.layout.cell_chat_audio_left);
        }
        else if (viewType == TYPE_VOICE_SEND){
            view = inflateView(parent,R.layout.cell_chat_audio_right);
        }else {
            view = inflateView(parent,R.layout.cell_chat_text_left);
        }
        holder = new BaseHolder(view);
        return holder;
    }

    private View inflateView(ViewGroup parent,int layId){
        return LayoutInflater
                .from(parent.getContext())
                .inflate(layId,parent,false);
    }
    @Override
    public void onBindViewHolder(final BaseHolder holder, final int position) {
        //获取当前条目对应的消息
        EMMessage emMessage = messages.get(position);
        long date = emMessage.getMsgTime();
        emMessage.getMsgId();
        //判断是否要显示时间
        /*if (position == 0){
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
        }*/
        //获取消息的类型
        final EMMessage.Type type = emMessage.getType();
        if (type == EMMessage.Type.TXT){
            EMTextMessageBody emMessageBody = (EMTextMessageBody) emMessage.getBody();
            msg = emMessageBody.getMessage();
            holder.setText(R.id.txt_content,msg);
        }else if (type == EMMessage.Type.IMAGE){
            EMImageMessageBody messageBody = (EMImageMessageBody) emMessage.getBody();
            picPath = messageBody.thumbnailLocalPath();
            holder.setImageUrl(R.id.im_image,picPath);
        }else if (type == EMMessage.Type.VOICE){
            EMVoiceMessageBody voiceMessageBody = (EMVoiceMessageBody) emMessage.getBody();
            mVoiceLen = voiceMessageBody.getLength();
            holder.setText(R.id.txt_content,""+ mVoiceLen);
        }

        int itemViewType = holder.getItemViewType();
        if (itemViewType == TYPE_PIC_RECEIVER || itemViewType == TYPE_PIC_SEND){
            holder.setOnClickListener(R.id.im_image, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String path = ((EMImageMessageBody)messages
                            .get(position).getBody()).getThumbnailUrl();
                    if (onItemClickListener != null){
                        onItemClickListener.onPictureClick(view,position,path);
                    }
                }
            });
        }else if (itemViewType == TYPE_TEXT_RECEIVER || itemViewType == TYPE_TEXT_SEND){
            holder.setOnClickListener(R.id.txt_content, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = ((EMTextMessageBody)messages
                            .get(position).getBody()).getMessage();
                    if (onItemClickListener != null){
                        onItemClickListener
                                .onTextClick(view,position,message);
                    }
                }
            });
        }else if (itemViewType == TYPE_VOICE_RECEIVER || itemViewType == TYPE_VOICE_SEND){
            holder.setOnClickListener(R.id.txt_content, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int length = ((EMVoiceMessageBody)messages
                            .get(position).getBody()).getLength();
                    String voicepath = ((EMVoiceMessageBody)messages
                            .get(position).getBody()).getLocalUrl();
                    if (onItemClickListener != null){
                        onItemClickListener
                                .onVoiceClick(view,position,voicepath,length);
                    }
                }
            });
        }

        //获取消息内容
        //EMMessageBody messageBody = emMessage.getBody().;

            EMMessage.Direct direct = emMessage.direct();
            if (direct == EMMessage.Direct.RECEIVE){
                //头像
                String name = emMessage.getUserName();
                //数据库中通过username查询对应字段
                ContactsModel model = SQLite
                        .select()
                        .from(ContactsModel.class)
                        .where(ContactsModel_Table.contactUserName.eq(name))
                        .querySingle();
                holder.setImageUrl(R.id.im_portrait,model.protrait);
            }else {
                UserModel userModel = SQLite
                        .select()
                        .from(UserModel.class).querySingle();
                holder.setImageUrl(R.id.im_portrait,userModel.protrait);
            }
        //如果是发送的消息，需要处理发送的状态
        EMMessage.Status status = emMessage.status();
        if (emMessage.direct() == EMMessage.Direct.SEND){
            switch (status){
                case SUCCESS:
                    holder.getView(R.id.loading).setVisibility(View.GONE);
                    break;
                case FAIL:
                    //holder.progressBar.setImageResource(R.drawable.error);
                    break;
                case INPROGRESS:
                    holder.getView(R.id.loading).setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages == null?0:messages.size();
    }
    /*class ChatViewHolder extends RecyclerView.ViewHolder{
        //TextView tv_time;
        TextView tv_message,tv_voice_len;
        CircleImageView iv_protrait;
        ProgressBar progressBar;
        ImageView img_message,img_voice;
        public ChatViewHolder(View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.txt_content);
            //tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            iv_protrait = itemView.findViewById(R.id.im_portrait);
            progressBar = itemView.findViewById(R.id.loading);
            img_message = itemView.findViewById(R.id.im_image);
            tv_voice_len = itemView.findViewById(R.id.txt_content);
            img_voice = itemView.findViewById(R.id.im_audio_track);
        }
    }*/

    @Override
    public int getItemViewType(int position) {
        //Direct 消息的发送方向  send说明是自己发的  receive收到的消息
        EMMessage message = messages.get(position);
        //获取消息的类型
        EMMessage.Type type = message.getType();
        //根据direct的值来决定 究竟是发送的消息 还是收到的消息
        EMMessage.Direct direct = message.direct();

        if (direct == EMMessage.Direct.RECEIVE){
            //接收方
            if (type == EMMessage.Type.TXT){
                return TYPE_TEXT_RECEIVER;
            }else if (type == EMMessage.Type.IMAGE){
                return TYPE_PIC_RECEIVER;
            }else if (type == EMMessage.Type.VOICE){
                return TYPE_VOICE_RECEIVER;
            }
        }else {
            //发送方
            if (type == EMMessage.Type.TXT){
                return TYPE_TEXT_SEND;
            }else if (type == EMMessage.Type.IMAGE){
                return TYPE_PIC_SEND;
            }else if (type == EMMessage.Type.VOICE){
                return TYPE_VOICE_SEND;
            }
        }
        return 1;
    }

    public interface OnItemClickListener {
        void onTextClick(View view, int position,String msg);
        void onPictureClick(View view,int position,String picPath);
        void onVoiceClick(View view,int position,String voicePath,int lenth);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
