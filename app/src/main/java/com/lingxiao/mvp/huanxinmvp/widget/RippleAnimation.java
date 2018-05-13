package com.lingxiao.mvp.huanxinmvp.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class RippleAnimation extends View {

    private ViewGroup mRootView;
    private float mStartX,mStartY;
    private int mStartRadius;
    private Paint mPaint;
    private int mMaxRadius;
    private boolean isStarted = false;
    private Bitmap mBackground;
    private int mCurrentRadius;
    private AnimatorListenerAdapter mAnimatorListener;
    private ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private long mDuration = 1000;
    public RippleAnimation(Context context, float startX, float startY, int radius) {
        super(context);
        //获取activity的根视图，用来添加本view
        mRootView = (ViewGroup) ((Activity)getContext()).getWindow().getDecorView();
        mStartX = startX;
        mStartY = startY;
        mStartRadius = radius;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        //设置成擦除模式
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        updateMaxRadius();
        initListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layer = canvas.saveLayer(0,0,getWidth(),getHeight(),null);
        canvas.drawBitmap(mBackground,0,0,null);
        canvas.drawCircle(mStartX,mStartY,mCurrentRadius,mPaint);
        canvas.restoreToCount(layer);
    }

    public static RippleAnimation create(View onClickView){
        Context context = onClickView.getContext();
        int newWidth = onClickView.getWidth() / 2;
        int newHeight = onClickView.getHeight() / 2;
        //计算起点坐标
        float startX = getAbsoluteX(onClickView) +newWidth;
        float startY = getAbsoluteY(onClickView) +newHeight;

        //起始半径 （避免遮挡按钮）
        int radius = Math.max(newWidth,newHeight);
        return new RippleAnimation(context,startX,startY,radius);
    }

    /**
     * 递归 获取绝对坐标
     * @param view
     * @return
     */
    private static float getAbsoluteX(View view) {
        float x = view.getX();
        ViewParent parent = view.getParent();
        if (parent != null && parent instanceof View){
            x += getAbsoluteX((View) parent);
        }
        return x;
    }
    private static float getAbsoluteY(View view) {
        float y = view.getY();
        ViewParent parent = view.getParent();
        if (parent != null && parent instanceof View){
            y += getAbsoluteY((View) parent);
        }
        return y;
    }

    private void updateMaxRadius() {
        //将屏幕分为四个小矩形
        RectF leftTop = new RectF(0,0,mStartX+mStartRadius,mStartY+mStartRadius);
        RectF rightTop = new RectF(leftTop.right,0,mRootView.getRight(),leftTop.bottom);
        RectF leftBottom = new RectF(0,leftTop.bottom,leftTop.right,mRootView.getBottom());
        RectF rightBottom = new RectF(leftBottom.right,leftTop.bottom,mRootView.getRight(),leftBottom.bottom);
        //获取对角线长度
        double leftTopHypoten = Math.sqrt(Math.pow(leftTop.width(),2)+Math.pow(leftTop.height(),2));
        double rightTopYopHypoten = Math.sqrt(Math.pow(rightTop.width(),2)+Math.pow(rightTop.height(),2));
        double leftBottomHypoten = Math.sqrt(Math.pow(leftBottom.width(),2)+Math.pow(leftBottom.height(),2));
        double rightBottomHypoten = Math.sqrt(Math.pow(rightBottom.width(),2)+Math.pow(rightBottom.height(),2));

        //取最大值
        mMaxRadius = (int) Math.max(
                Math.max(leftTopHypoten,rightTopYopHypoten),
                Math.max(leftBottomHypoten,rightBottomHypoten)
        );
    }
    private void initListener(){
        mAnimatorListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //动画播放完毕，移除view
                detachFromRootView();
                if (endListener != null){
                    endListener.onEnd();
                }
                isStarted = false;
            }
        };
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentRadius = (int) (float)animation.getAnimatedValue() + mStartRadius;
                postInvalidate();
            }
        };
    }

    private void detachFromRootView() {
        mRootView.removeView(this);
    }

    public void start(){
        if (!isStarted){
            isStarted = true;
            updateBackground();
            attachToRootView();
            getAnimator().start();
        }
    }

    private ValueAnimator getAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0,mMaxRadius).setDuration(mDuration);
        animator.addUpdateListener(mUpdateListener);
        animator.addListener(mAnimatorListener);
        return animator;
    }

    /**
     * 添加到根视图
     */
    private void attachToRootView() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mRootView.addView(this);
    }

    /**
     * 更新屏幕截图
     */
    private void updateBackground() {
        if (mBackground != null && !mBackground.isRecycled()){
            mBackground.recycle();
        }
        mRootView.setDrawingCacheEnabled(true);
        mBackground = mRootView.getDrawingCache();
        mBackground = Bitmap.createBitmap(mBackground);
        mRootView.setDrawingCacheEnabled(false);
    }

    public RippleAnimation setDuration(long duration){
        mDuration = duration;
        return this;
    }

    public RippleEndListener endListener;
    public void setRippleListener(RippleEndListener listener){
        this.endListener = listener;
    }
    public interface RippleEndListener{
        void onEnd();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
