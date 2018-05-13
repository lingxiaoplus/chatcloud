package com.lingxiao.mvp.huanxinmvp.adapter;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.lingxiao.mvp.huanxinmvp.R;

import java.util.ArrayList;
import java.util.List;

public class PopWindowAdapter extends BaseRecyAdapter{

    private AppCompatImageView imageView;
    private TextView textView;
    private String[] titles = {"电话","视频","语音","相册"};
    private int[] imgs = {
            R.drawable.ic_img_call,
            R.drawable.ic_img_videocall,
            R.drawable.ic_img_voice,
            R.drawable.ic_img_photo};
    public PopWindowAdapter(List mList) {
        super(mList);
    }
    public PopWindowAdapter() {

    }
    @Override
    public void bindData(BaseHolder holder, int position, List mList) {
        try {
            imageView = (AppCompatImageView) holder.getView(R.id.iv_pop);
            textView = (TextView) holder.getView(R.id.tv_pop);
            imageView.setImageResource(imgs[position]);
            textView.setText(titles[position]);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_pop_window;
    }

    @Override
    public int getHeadLayoutId() {
        return 0;
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}
