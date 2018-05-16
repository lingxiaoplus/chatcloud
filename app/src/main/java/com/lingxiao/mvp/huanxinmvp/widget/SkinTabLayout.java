package com.lingxiao.mvp.huanxinmvp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;

import com.lingxiao.mvp.huanxinmvp.R;

import skin.support.content.res.SkinCompatResources;
import skin.support.design.widget.SkinMaterialTabLayout;
import skin.support.widget.SkinCompatHelper;
import skin.support.widget.SkinCompatSupportable;

import static skin.support.widget.SkinCompatHelper.INVALID_ID;

public class SkinTabLayout extends TabLayout implements SkinCompatSupportable {
    private int mTabIndicatorColorResId = INVALID_ID;
    private int mTabTextColorsResId = INVALID_ID;
    private int mTabSelectedTextColorResId = INVALID_ID;
    private int mTabBackgroundColorResId = INVALID_ID;
    public SkinTabLayout(Context context) {
        this(context,null);
    }

    public SkinTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SkinTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, skin.support.design.R.styleable.TabLayout,
                defStyleAttr, 0);

        mTabIndicatorColorResId = a.getResourceId(skin.support.design.R.styleable.TabLayout_tabIndicatorColor, INVALID_ID);

        int tabTextAppearance = a.getResourceId(skin.support.design.R.styleable.TabLayout_tabTextAppearance, skin.support.design.R.style.TextAppearance_Design_Tab);

        // Text colors/sizes come from the text appearance first
        final TypedArray ta = context.obtainStyledAttributes(tabTextAppearance, skin.support.design.R.styleable.SkinTextAppearance);
        try {
            mTabTextColorsResId = ta.getResourceId(skin.support.design.R.styleable.SkinTextAppearance_android_textColor, INVALID_ID);
        } finally {
            ta.recycle();
        }

        if (a.hasValue(skin.support.design.R.styleable.TabLayout_tabTextColor)) {
            // If we have an explicit text color set, use it instead
            mTabTextColorsResId = a.getResourceId(skin.support.design.R.styleable.TabLayout_tabTextColor, INVALID_ID);
        }

        if (a.hasValue(skin.support.design.R.styleable.TabLayout_tabSelectedTextColor)) {
            // We have an explicit selected text color set, so we need to make merge it with the
            // current colors. This is exposed so that developers can use theme attributes to set
            // this (theme attrs in ColorStateLists are Lollipop+)
            mTabSelectedTextColorResId = a.getResourceId(skin.support.design.R.styleable.TabLayout_tabSelectedTextColor, INVALID_ID);
        }

        mTabBackgroundColorResId = a.getResourceId(skin.support.design.R.styleable.TabLayout_tabBackground, INVALID_ID);
        a.recycle();
        applySkin();
    }

    @Override
    public void applySkin() {
        mTabIndicatorColorResId = SkinCompatHelper.checkResourceId(mTabIndicatorColorResId);
        if (mTabIndicatorColorResId != INVALID_ID) {
            setBackgroundColor(SkinCompatResources.getColor(getContext(), mTabIndicatorColorResId));
            //setSelectedTabIndicatorColor(SkinCompatResources.getColor(getContext(), mTabIndicatorColorResId));
            setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
        }
        mTabTextColorsResId = SkinCompatHelper.checkResourceId(mTabTextColorsResId);
        if (mTabTextColorsResId != INVALID_ID) {
            setTabTextColors(SkinCompatResources.getColorStateList(getContext(), mTabTextColorsResId));
        }
        mTabSelectedTextColorResId = SkinCompatHelper.checkResourceId(mTabSelectedTextColorResId);
        if (mTabSelectedTextColorResId != INVALID_ID) {
            int selected = SkinCompatResources.getColor(getContext(), mTabSelectedTextColorResId);
            if (getTabTextColors() != null) {
                setTabTextColors(getTabTextColors().getDefaultColor(), selected);
            }
        }
    }
}
