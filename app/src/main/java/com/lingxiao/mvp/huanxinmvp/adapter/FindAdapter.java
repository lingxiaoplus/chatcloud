package com.lingxiao.mvp.huanxinmvp.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.model.FindBean;
import com.lingxiao.mvp.huanxinmvp.utils.GlideHelper;
import com.lingxiao.mvp.huanxinmvp.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by lingxiao on 17-7-16.
 */

public class FindAdapter extends RecyclerView.Adapter<FindAdapter.FindHolder>{
    private ArrayList<FindBean.DetailMsg> msgArrayList;
    public void setMsgArrayList(ArrayList<FindBean.DetailMsg> msgArrayList){
        this.msgArrayList = msgArrayList;
    }
    public FindAdapter(ArrayList<FindBean.DetailMsg> msgArrayList){
        this.msgArrayList = msgArrayList;
    }
    @Override
    public FindAdapter.FindHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.list_find,null);
        FindHolder holder = new FindHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FindAdapter.FindHolder holder, final int position) {
        holder.title.setText(msgArrayList.get(position).title);
        holder.message.setText(msgArrayList.get(position).brief);

        GlideHelper.loadImageView(msgArrayList.get(position).headpic,holder.headPic);
        GlideHelper.loadImageView(msgArrayList.get(position).site_info.pic,holder.fromPic);

        holder.from.setText(msgArrayList.get(position).site_info.name);
        String date = StringUtils.strToDate(msgArrayList.get(position).pub_date);
        holder.time.setText(date);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onFindClick(v,msgArrayList.get(position).origin_url,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return msgArrayList == null?0:msgArrayList.size();
    }
    class FindHolder extends RecyclerView.ViewHolder{
        TextView title,message,from,time;
        ImageView headPic,fromPic;
        public FindHolder(View itemView) {
            super(itemView);
            headPic = itemView.findViewById(R.id.iv_find_pic);
            fromPic = itemView.findViewById(R.id.iv_find_who);
            title = (TextView) itemView.findViewById(R.id.tv_find_title);
            message = (TextView) itemView.findViewById(R.id.tv_find_message);
            from = (TextView) itemView.findViewById(R.id.tv_find_who);
            time = (TextView) itemView.findViewById(R.id.tv_find_time);
        }
    }
    private onFindClickListener listener;
    public interface onFindClickListener{
        void onFindClick(View v,String url,int position);
    };
    public void setOnFindClickListener(onFindClickListener listener){
        this.listener = listener;
    }
}
