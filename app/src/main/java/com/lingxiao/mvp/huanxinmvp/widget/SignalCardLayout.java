package com.lingxiao.mvp.huanxinmvp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lingxiao.mvp.huanxinmvp.R;

public class SignalCardLayout extends CardView{

    private String leftText,RightText;
    private TextView leftView;
    private TextView rightView;

    public SignalCardLayout(Context context) {
        this(context,null);
    }

    public SignalCardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);

    }

    public SignalCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void initView(Context context,AttributeSet attrs) {
        View.inflate(context, R.layout.signal_layout,this);
        leftView = findViewById(R.id.tv_left);
        rightView = findViewById(R.id.tv_right);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SignalCardLayout);
        leftText = a.getString(R.styleable.SignalCardLayout_left_text);
        RightText = a.getString(R.styleable.SignalCardLayout_right_text);
        leftView.setText(leftText);
        rightView.setText(RightText);
        a.recycle();
    }

    public void setRightText(String right){
        //leftView.setText(left);
        rightView.setText(right);
    }

    public String getRightText(){
        return rightView.getText().toString();
    }
}
