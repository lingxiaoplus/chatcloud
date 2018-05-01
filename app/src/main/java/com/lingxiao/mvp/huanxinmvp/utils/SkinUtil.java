package com.lingxiao.mvp.huanxinmvp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.AppCompatImageView;
import android.widget.ImageView;

public class SkinUtil {
    /**
     * 改变SVG图片着色
     * @param imageView
     * @param iconResId svg资源id
     * @param color 期望的着色
     */
    public static void changeSVGColor(AppCompatImageView imageView, int iconResId, int color, Context ctx){
            @SuppressLint("RestrictedApi") Drawable drawable =  AppCompatDrawableManager.get().getDrawable(ctx, iconResId);
            imageView.setImageDrawable(drawable);
            Drawable.ConstantState state = drawable.getConstantState();
            Drawable drawable1 = DrawableCompat.wrap(state == null ? drawable : state.newDrawable()).mutate();
            drawable1.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            DrawableCompat.setTint(drawable1, ContextCompat.getColor(ctx, color));
            imageView.setImageDrawable(drawable1);
    }

}
