package com.lingxiao.mvp.huanxinmvp.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lingxiao on 2017/9/30.
 */

public abstract class BaseRecyAdapter<T> extends RecyclerView.Adapter<BaseRecyAdapter.BaseViewHolder>{
    private List<T> mList;
    //private int headCount = 0; //头布局个数
    //private int footCount = 1; //尾布局个数
    private static final int HEAD_TYPE=1;
    private static final int BODY_TYPE=2;
    private static final int FOOT_TYPE=3;
    private boolean isFinish;   //是否加载完成
    private LayoutInflater mLayoutInflater;
    private int mLastPosition = -1;
    private View mItemView;


    public BaseRecyAdapter(List<T> mList){
        this.mList = mList;
    }

    public BaseRecyAdapter(){

    }
    @Override
    public BaseRecyAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mLayoutInflater = LayoutInflater
                .from(parent.getContext());
        View view = mLayoutInflater.inflate(getLayoutId(),parent,false);
        final BaseViewHolder holder = new BaseViewHolder(view);

        if (onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int poisition = holder.getAdapterPosition();
                    onItemClickListener.onItemClick(holder.itemView,poisition);
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyAdapter.BaseViewHolder holder, int position) {

            bindData(holder,position,mList);
    }

    public abstract void bindData(BaseViewHolder holder, int position, List<T> mList);
    /**
     *获取布局文件
     */
    public abstract int getLayoutId();

    /**
     *获取头布局文件
     */
    public abstract int getHeadLayoutId();
    @Override
    public int getItemCount() {
        return mList == null?0:mList.size();
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder{
        private Map<Integer,View> mViewMap;
        public BaseViewHolder(View itemView) {
            super(itemView);
            mViewMap = new HashMap<>();
            mItemView = itemView;
        }
        public View getView(int id){
            View view = mViewMap.get(id);
            if (view == null){
                view = itemView.findViewById(id);
                mViewMap.put(id,view);
            }
            return view;
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View View, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //加载更多
    private onLoadmoreListener listener;
    public interface onLoadmoreListener{
        void onLoadMore();
    }
    public void setRefreshListener(onLoadmoreListener listener){
        this.listener = listener;
    }

    //数据加载完成
    public void isFinish(boolean isFinish){
        this.isFinish = isFinish;
    }
    /**
     *刷新数据
     */
    public void onRefresh(List<T> datas){
        this.mList.clear();
        this.mList = datas;
        notifyDataSetChanged();
    }
    public void addData(List<T> datas){
        this.mList.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     *  将动画对象加入集合中  根据左右滑动加入不同
     */
    private void addInAnimation(View view, boolean buttom) {
        List<Animator> list = new ArrayList<>();
        if (buttom) {
            list.add(ObjectAnimator.ofFloat(view,
                    "translationY", -view.getMeasuredHeight() * 2, 0));
        } else {
            list.add(ObjectAnimator.ofFloat(view,
                    "translationY", view.getMeasuredHeight() * 2, 0));
        }
        list.add(ObjectAnimator.ofFloat(view, "translationY",
                view.getMeasuredHeight() / 2, 0));
        list.add(ObjectAnimator.ofFloat(view, "alpha", 0, 1));
        list.add(ObjectAnimator.ofFloat(view, "scaleX", 0.25f, 1));
        list.add(ObjectAnimator.ofFloat(view, "scaleY", 0.25f, 1));
        startAnimation(list);
    }
    /**
     *  开启动画
     */
    private void startAnimation(List<Animator> list) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(list);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(800);
        animatorSet.start();
    }
}
