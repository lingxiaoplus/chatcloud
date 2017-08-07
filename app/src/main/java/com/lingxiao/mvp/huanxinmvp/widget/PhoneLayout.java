package com.lingxiao.mvp.huanxinmvp.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lingxiao.mvp.huanxinmvp.R;

/**
 * 通讯录界面的自定义组合控件
 */

public class PhoneLayout extends RelativeLayout{

    private RecyclerView recycleView;
    private TextView tv_slidebar;
    private SlidebarView slidebar;
    private SwipeRefreshLayout swlayout;
    private FloatingActionButton fb_button;

    public PhoneLayout(Context context) {
        this(context,null);
    }

    public PhoneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PhoneLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PhoneLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView() {
        View.inflate(getContext(), R.layout.phone_layout,this);
        recycleView = (RecyclerView) findViewById(R.id.rv_phone);
        tv_slidebar = (TextView) findViewById(R.id.tv_slidebar);
        slidebar = (SlidebarView) findViewById(R.id.slidebar);
        swlayout = (SwipeRefreshLayout) findViewById(R.id.swlayout);
        fb_button = (FloatingActionButton) findViewById(R.id.fb_button);
    }

    public void setAdapter(RecyclerView.Adapter adapter){
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(adapter);
    }

    public void setRefreshListener(SwipeRefreshLayout.OnRefreshListener listener){
        swlayout.setOnRefreshListener(listener);
    }

    public void setRefreshing(boolean isRefreshing){
        swlayout.setRefreshing(isRefreshing);
    }
    public void setFbButtonListener(OnClickListener listener){
        fb_button.setOnClickListener(listener);
    }

}
