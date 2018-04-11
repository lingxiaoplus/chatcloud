package com.lingxiao.mvp.huanxinmvp.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by lingxiao on 17-6-29.
 */

public abstract class BaseFragment extends Fragment{
    public Activity mActivity;
    Unbinder unbinder;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = initView();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    //初始化数据，由子类实现
    public abstract void initData();

    //由子类实现
    public abstract View initView();

    /**
     * @param windowToken View，一般为edittext  getWindowToken()
     * @param flag 软键盘隐藏时的控制参数  一般为0即可
     * @param isShow 显示还是隐藏
     */
    public void toggleSoftInput(View windowToken, int flag, boolean isShow){
        if (null == mActivity){
            return;
        }
        //隐藏软键盘
        InputMethodManager input = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
        if (null != input){
            if (isShow){
                windowToken.requestFocus();
                input.showSoftInput(windowToken,0);
            }else {
                input.hideSoftInputFromWindow(windowToken.getWindowToken(),flag);
            }
        }

    }


    public void StartActivity(Class clazz){
        Intent intent = new Intent(UIUtils.getContext(),clazz);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
