package com.lingxiao.mvp.huanxinmvp.utils;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

public class GlideHelper {
    /**
     * 默认加载样式
     * @param url 图片地址
     * @param view imageview
     */
    public static void loadImageView(String url, ImageView view) {
        Glide.with(UIUtils.getContext())
                .load(url)
                .into(view);
    }

    /**
     * 添加一个标识用来更新图片
     * @param url 图片地址
     * @param view imageview
     * @param updateTime 更新时间
     */
    public static void loadImageWithData(String url, ImageView view,long updateTime) {
        Glide.with(UIUtils.getContext())
                .load(url)
                .signature(new StringSignature(String.valueOf(updateTime)))
                .into(view);
    }
    /**
     * 设置加载大小
     */
    public static void loadImageViewSize(String path, int width, int height, ImageView mImageView) {
        Glide.with(UIUtils.getContext())
                .load(path)
                .override(width, height)
                .into(mImageView);
    }

    /**
     * 加载失败图
     * @param path
     * @param mImageView
     * @param lodingImage
     * @param errorImageView
     */
    public static void loadImageViewLoding(String path, ImageView mImageView, int lodingImage, int errorImageView) {
        Glide.with(UIUtils.getContext())
                .load(path)
                .placeholder(lodingImage)
                .error(errorImageView)
                .into(mImageView);
    }

    /**
     * 不使用内存缓存和不使用磁盘缓存的实时加载
     * @param url
     * @param view
     */
    public static void realTimeLoading(String url,ImageView view){
        Glide.with(UIUtils.getContext())
                .load(url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }
}
