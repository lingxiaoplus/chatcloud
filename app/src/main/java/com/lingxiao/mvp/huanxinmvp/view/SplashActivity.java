package com.lingxiao.mvp.huanxinmvp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.lingxiao.mvp.huanxinmvp.MainActivity;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.SplashPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.presenter.SplashPresenter;

public class SplashActivity extends Activity implements SplashView{

    private ImageView imageview;
    private SplashPresenter splashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageview = (ImageView) findViewById(R.id.iv_splash);

        //view层要持有presenter的引用
        splashPresenter = new SplashPresenterImpl(this);
        //检查是否登录
        splashPresenter.checkLogin();

    }
    /**
     * 给闪屏页添加动画效果
     **/
    private void initAnimation() {
        AnimationSet set = new AnimationSet(false);
        //旋转动画
        RotateAnimation rotateAnimation = new RotateAnimation(
                180,180,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(3000);
        rotateAnimation.setFillAfter(true);
        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0,1,0,1,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(3000);
        scaleAnimation.setFillAfter(true);
        //渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(3000);
        alphaAnimation.setFillAfter(true);

        set.addAnimation(rotateAnimation);
        set.addAnimation(scaleAnimation);
        set.addAnimation(alphaAnimation);

        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(getApplicationContext(),
                        LoginActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageview.startAnimation(set);
    }

    @Override
    public void onGetLoginState(boolean isLogin) {
        if (isLogin){
            //如果登录过了，跳转到主界面
            startActivity(new Intent(getApplicationContext(),
                    MainActivity.class));
            finish();
        }else {
            initAnimation();
        }
    }
}
