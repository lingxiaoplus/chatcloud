package com.lingxiao.mvp.huanxinmvp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.adapter.PhoneRecycleAdapter;
import com.lingxiao.mvp.huanxinmvp.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by lingxiao on 17-7-12.
 */

public class SlidebarView extends View{
    private String[] sections = {"搜","A","B","C","D","E","F","G","H","I","J","K","L","M","N",
            "O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    private Paint paint;
    private int x;
    private int viewHeight;
    private RecyclerView rv_phone;
    private TextView tv_slidebar;
    private PhoneRecycleAdapter adapter;

    public SlidebarView(Context context) {
        this(context,null);
    }

    public SlidebarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlidebarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //初始化画笔
    private void init(){
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.GRAY);
        paint.setTextSize(getResources().getDimension(R.dimen.slide_text_size));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        x = w/2;            //拿到总宽度的一半
        viewHeight = h;     //现在的高度
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < sections.length; i++) {
            //从左下角开始画
            canvas.drawText(sections[i],x,viewHeight/sections.length*(i+1),paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ViewGroup parent = (ViewGroup) getParent();
        rv_phone = (RecyclerView) parent.findViewById(R.id.rv_phone);
        tv_slidebar = (TextView) parent.findViewById(R.id.tv_slidebar);
        adapter = (PhoneRecycleAdapter) rv_phone.getAdapter();
        String str;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //手指按下时，要设置背景为灰色，展示中间的text
                str = sections[getIndex(event.getY())];
                tv_slidebar.setText(str);
                setBackgroundColor(getResources().getColor(R.color.slidebar_pressed));
                tv_slidebar.setVisibility(VISIBLE);
                break;
            case MotionEvent.ACTION_MOVE:
                //手指移动时，要更改text，滑动recycle
                str = sections[getIndex(event.getY())];
                tv_slidebar.setText(str);
                scrollRecycleView(str);
                break;
            case MotionEvent.ACTION_UP:
                tv_slidebar.setVisibility(GONE);
                setBackgroundColor(getResources().getColor(R.color.slidebar_normal));
                break;
        }
        return true;
    }

    /**
     *recycle滑动
     */
    private void scrollRecycleView(String str) {
        ArrayList<String> contacts = adapter.getContacts();
        if (contacts != null && contacts.size()>0){
            for (int i = 0; i < contacts.size(); i++) {
                if (StringUtils.getFirstChar(contacts.get(i)).equals(str)){
                    rv_phone.smoothScrollToPosition(i);
                    break;
                }
            }
        }
    }

    /**
     *通过y坐标判断当前的位置
     */
    private int getIndex(float y) {
        //每个文字的高度
        int textHeight = viewHeight/sections.length;
        //当前位置/文字高度
        int result = (int) (y/textHeight);
        return result<0?0:result>sections.length-1?sections.length-1:result;
    }
}
