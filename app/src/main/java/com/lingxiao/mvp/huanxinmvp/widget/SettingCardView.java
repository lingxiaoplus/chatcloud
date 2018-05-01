package com.lingxiao.mvp.huanxinmvp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.lingxiao.mvp.huanxinmvp.R;

public class SettingCardView extends CardView{

    private TextView titleView;
    private TextView messageView;
    private SwitchCompat switchCompat;

    private String title,message;
    private boolean visable,switchs;
    public SettingCardView(Context context) {
        this(context,null);
    }

    public SettingCardView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        View.inflate(context, R.layout.setting_card_layout,this);
        titleView = findViewById(R.id.tv_card_title);
        messageView = findViewById(R.id.tv_card_message);
        switchCompat = findViewById(R.id.switch_card);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingCardView);
        title = a.getString(R.styleable.SettingCardView_title);
        message = a.getString(R.styleable.SettingCardView_message);
        switchs = a.getBoolean(R.styleable.SettingCardView_switchcompat,false);
        visable = a.getBoolean(R.styleable.SettingCardView_visable,true);

        titleView.setText(title);
        messageView.setText(message);
        switchCompat.setChecked(switchs);
        if (visable){
            switchCompat.setVisibility(VISIBLE);
        }else {
            switchCompat.setVisibility(GONE);
        }
        a.recycle();
    }

    public void setTitle(String title){
        titleView.setText(title);
    }

    public void setMessage(String message){
        messageView.setText(message);
    }

    public void setChecked(boolean checked){
        switchCompat.setChecked(checked);
    }


    public String getTitle(){
        return titleView.getText().toString();
    }

    public String getMessage(){
        return messageView.getText().toString();
    }

    public boolean getChecked(){
        return switchCompat.isChecked();
    }

    public void setVisable(boolean vis){
        if (vis){
            switchCompat.setVisibility(VISIBLE);
        }else {
            switchCompat.setVisibility(GONE);
        }
    }
}
