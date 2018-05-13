package com.lingxiao.mvp.huanxinmvp.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;

import skin.support.content.res.SkinCompatResources;
import skin.support.design.widget.SkinMaterialBottomNavigationView;
import skin.support.design.widget.SkinMaterialFloatingActionButton;
import skin.support.design.widget.SkinMaterialTabLayout;
import skin.support.widget.SkinCompatBackgroundHelper;
import skin.support.widget.SkinCompatHelper;
import skin.support.widget.SkinCompatImageHelper;
import skin.support.widget.SkinCompatSupportable;

import static skin.support.widget.SkinCompatHelper.INVALID_ID;

public class BottomSkinNavView extends BottomNavigationBar implements SkinCompatSupportable{
    private int mActiviteColorResId = INVALID_ID;
    private SkinMaterialTabLayout tabLayout;
    public BottomSkinNavView(Context context) {
        this(context,null);
    }

    public BottomSkinNavView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BottomSkinNavView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BottomNavigationBar,
                defStyleAttr, 0);
        mActiviteColorResId = a.getResourceId(R.styleable.BottomNavigationBar_bnbActiveColor,
                INVALID_ID);
        a.recycle();
        applySkin();
    }

    private void applyActiviteColorResource() {
        mActiviteColorResId = SkinCompatHelper.checkResourceId(mActiviteColorResId);
        if (mActiviteColorResId != INVALID_ID) {
            int color = SkinCompatResources.getColor(getContext(), mActiviteColorResId);
            LogUtils.i("bottom的color："+color);
            //setActiveColor(color);
        }
    }

    @Override
    public void applySkin() {
        applyActiviteColorResource();
    }
}
