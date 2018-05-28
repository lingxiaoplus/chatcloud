package com.lingxiao.mvp.huanxinmvp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.utils.GlideHelper;

public class BaseHolder extends RecyclerView.ViewHolder{
    private SparseArray<View> mViews;
    public BaseHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray();
    }

    /**
     * 通过viewid查找view
     * @param viewId
     * @param <T>
     * @return
     */
    public  <T extends View> T getView(int viewId){
        View view =mViews.get(viewId);
        if (view == null){
            view = itemView.findViewById(viewId);
        }
        return (T) view;
    }

    public BaseHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public BaseHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public BaseHolder setImageUrl(int viewId, String picPath,long time) {
        ImageView view = getView(viewId);
        GlideHelper.loadImageWithData(picPath,view,time);
        return this;
    }

    public BaseHolder setOnClickListener(int viewId,
                                         View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }
   /* public interface OnItemClickListener {
        void onTextClick(View view, int position,String msg);
        void onPictureClick(View view,int position,String picPath);
        void onVoiceClick(View view,int position,String voicePath);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }*/
}
