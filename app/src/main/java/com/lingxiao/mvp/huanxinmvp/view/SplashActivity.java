package com.lingxiao.mvp.huanxinmvp.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Property;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.lingxiao.mvp.huanxinmvp.MainActivity;
import com.lingxiao.mvp.huanxinmvp.NotifyService;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.SplashPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.presenter.SplashPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.SpUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity implements SplashView {

    @BindView(R.id.tv_splash)
    TextView tvAppName;
    @BindView(R.id.pb_splash)
    ProgressBar progressBar;
    @BindView(R.id.tv_version_splash)
    TextView tvVersion;
    @BindView(R.id.ll_splash)
    RelativeLayout llSplash;
    private SplashPresenter splashPresenter;

    private ColorDrawable mBgDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        //view层要持有presenter的引用
        splashPresenter = new SplashPresenterImpl(this);

        if (UIUtils.isServiceRunning(this,getPackageName()+".NotifyService")){
            ToastUtils.showToast("服务在运行");
            //服务正在运行，说明已经登录了，跳转到主界面
            startActivity(new Intent(getApplicationContext(),
                    MainActivity.class));
            finish();
        }
        // 获取颜色
        int color = getResources().getColor(R.color.black_alpha_192);
        // 创建一个Drawable
        ColorDrawable drawable = new ColorDrawable(color);
        // 设置给背景
        llSplash.setBackground(drawable);
        mBgDrawable = drawable;
        tvVersion.setText("版本号：V"+getVersionName());
        getUpdate();
        startAnim(1f, new Runnable() {
            @Override
            public void run() {
                //检查是否登录
                splashPresenter.checkLogin();
            }
        });
    }

    private void getUpdate() {
        try {
            AVQuery<AVObject> avQuery = new AVQuery<>("appinfo");
            avQuery.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObj, AVException e) {
                    if (e == null && avObj != null){
                        String versionName = avObj.getString("VersionName");
                        String desc = avObj.getString("description");
                        String url = avObj.getString("DownloadUrl");
                        int versionCode = avObj.getInt("VersionCode");
                        SpUtils.putString(UIUtils.getContext(), ContentValue.VERSION_NAME,versionName);
                        SpUtils.putString(UIUtils.getContext(), ContentValue.VERSION_DES,desc);
                        SpUtils.putString(UIUtils.getContext(), ContentValue.DOWNLOAD_URL,url);
                        SpUtils.putInt(UIUtils.getContext(), ContentValue.VERSION_CODE,versionCode);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 给背景设置一个动画
     *
     * @param endProgress 动画的结束进度
     * @param endCallback 动画结束时触发
     */
    private void startAnim(float endProgress, final Runnable endCallback) {
        // 获取一个最终的颜色
        int finalColor = getResources().getColor(R.color.white);
        // 运算当前进度的颜色
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int) evaluator.evaluate(endProgress, mBgDrawable.getColor(), finalColor);
        // 构建一个属性动画
        ValueAnimator valueAnimator = ObjectAnimator.ofObject(this, property, evaluator, endColor);
        valueAnimator.setDuration(1500); // 时间
        valueAnimator.setIntValues(mBgDrawable.getColor(), endColor); // 开始结束值
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 结束时触发
                endCallback.run();
            }
        });
        valueAnimator.start();
    }

    @Override
    public void onGetLoginState(boolean isLogin) {
        if (isLogin) {
            startService(new Intent(this, NotifyService.class));
            //如果登录过了，跳转到主界面
            startActivity(new Intent(getApplicationContext(),
                    MainActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(),
                    LoginActivity.class));
        }
        finish();
    }

    private final Property<SplashActivity, Object> property = new Property<SplashActivity, Object>(Object.class, "color") {
        @Override
        public void set(SplashActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }

        @Override
        public Object get(SplashActivity object) {
            return object.mBgDrawable.getColor();
        }
    };
}
