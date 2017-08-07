package com.lingxiao.mvp.huanxinmvp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingxiao on 17-7-14.
 */

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.MyViewHolder>{
    private List<AVUser> avUsers;
    private List<String> contacts = new ArrayList<>();

    public void setAvUsers(List<AVUser> avUsers){
        this.avUsers = avUsers;
    }
    public void setContacts(List<String> contacts){
        if (contacts!=null){
            this.contacts =contacts;
        }
    }

    public AddFriendAdapter(List<AVUser> avUsers,List<String> contacts){
        this.avUsers = avUsers;
        if (contacts != null){
            this.contacts = contacts;
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.list_addfriend,null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String username = avUsers.get(position).getUsername();
        holder.textView.setText(username);
        String date = StringUtils.getDate(avUsers.get(position).getCreatedAt());
        holder.textTime.setText(date);
        if (contacts.contains(username)){
            //已经是好友了
            holder.button.setText("已经是好友了");
            holder.button.setBackgroundResource(R.color.colorPrimaryDark);
            holder.button.setEnabled(false);
        }else {
            holder.button.setText("添加");
            holder.button.setEnabled(true);
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onAddClick(v,username);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return avUsers == null?0:avUsers.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView,textTime;
        Button button;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_search_head);
            textView = (TextView) itemView.findViewById(R.id.tv_search_name);
            textTime = (TextView) itemView.findViewById(R.id.tv_regist_time);
            button = (Button) itemView.findViewById(R.id.btn_add);
        }
    }

    private onAddFriendClickListener listener;
    public interface onAddFriendClickListener{
        void onAddClick(View v,String username);
    }
    public void setAddFriendClickListener(onAddFriendClickListener listener){
        this.listener = listener;
    }
}
