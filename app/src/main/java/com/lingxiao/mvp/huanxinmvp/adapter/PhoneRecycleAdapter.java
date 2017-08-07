package com.lingxiao.mvp.huanxinmvp.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.utils.StringUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by lingxiao on 17-7-12.
 */

public class PhoneRecycleAdapter extends RecyclerView.Adapter<PhoneRecycleAdapter.ViewHolder>{
    private ArrayList<String> contacts;
    public void setContacts(ArrayList<String> contacts){
        this.contacts = contacts;
    }
    public PhoneRecycleAdapter(ArrayList<String> contacts){
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String contactStr = contacts.get(position);
        holder.textSelect.setText(StringUtils.getFirstChar(contactStr));
        holder.textName.setText(contactStr);
        if (position == 0){
            holder.textSelect.setVisibility(View.GONE);
        }else {
            String current = StringUtils.getFirstChar(contactStr);
            String last = StringUtils.getFirstChar(contacts.get(position-1));
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
                    onItemClickListener.onClick(v,contactStr);
                }
            }
        });

        //设置长按事件
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onLongClick(v,contactStr);
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
        public ViewHolder(View itemView) {
            super(itemView);
            textSelect = (TextView) itemView.findViewById(R.id.tv_section);
            textName = (TextView) itemView.findViewById(R.id.tv_username);
        }
    }
    public ArrayList<String> getContacts(){
        return contacts;
    }
}
