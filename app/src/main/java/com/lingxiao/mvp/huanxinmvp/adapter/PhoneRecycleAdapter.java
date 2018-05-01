package com.lingxiao.mvp.huanxinmvp.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.utils.ChineseCharToEnUtil;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.StringUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingxiao on 17-7-12.
 */

public class PhoneRecycleAdapter extends RecyclerView.Adapter<PhoneRecycleAdapter.ViewHolder>{
    private List<ContactsModel> contacts;
    public void setContacts(List<ContactsModel> contacts){
        this.contacts = contacts;
    }
    public PhoneRecycleAdapter(List<ContactsModel> contacts){
        this.contacts = contacts;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.recycle_item,null);
        ViewHolder viewHolder = new ViewHolder(view);
        Log.i("AmapError", "PhoneRecycleAdapter执行了: "+contacts.size());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ContactsModel model = contacts.get(position);
        holder.textSelect.setText(ChineseCharToEnUtil
                .getFirstChar(model.nickName));
        holder.textName.setText(model.nickName);
        Glide.with(UIUtils.getContext())
                .load(model.protrait)
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .into(holder.imgHead);

        if (position == 0){
            holder.textSelect.setVisibility(View.GONE);
        }else {
            String current = ChineseCharToEnUtil
                    .getFirstChar(model.nickName);
            String last = ChineseCharToEnUtil
                    .getFirstChar(contacts.get(position-1).nickName);
            if (current.equals(last)){
                holder.textSelect.setVisibility(View.GONE);
            }else {
                holder.textSelect.setVisibility(View.VISIBLE);
            }
        }
        //给条目设置点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onClick(v,contacts.get(position).contactUserName);
                }
            }
        });

        //设置长按事件
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onLongClick(v,contacts.get(position).contactUserName);
                    return true;
                }
                return false;
            }
        });
    }


    private onItemClickListener onItemClickListener;

    public interface onItemClickListener{
        void onClick(View v,String username);
        boolean onLongClick(View v,String username);
    }

    public void setOnItemClickListener(onItemClickListener onClickListener){
        this.onItemClickListener = onClickListener;
    }
    @Override
    public int getItemCount() {
        return contacts == null?0:contacts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textSelect;
        TextView textName;
        ImageView imgHead;
        public ViewHolder(View itemView) {
            super(itemView);
            textSelect = (TextView) itemView.findViewById(R.id.tv_section);
            textName = (TextView) itemView.findViewById(R.id.tv_username);
            imgHead = itemView.findViewById(R.id.iv_head);
        }
    }
    public List<ContactsModel> getContacts(){
        return contacts;
    }
}
