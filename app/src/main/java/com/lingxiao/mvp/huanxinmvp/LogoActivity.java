package com.lingxiao.mvp.huanxinmvp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lingxiao.mvp.huanxinmvp.view.BaseActivity;
import com.lingxiao.mvp.huanxinmvp.view.SplashActivity;

public class LogoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartActivity(SplashActivity.class,true);
    }
}
