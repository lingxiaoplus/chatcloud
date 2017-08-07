package com.lingxiao.mvp.huanxinmvp.view;

import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.RegistPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.presenter.RegistPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.PreUtils;
import com.lingxiao.mvp.huanxinmvp.utils.StringUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;

/*
* 注册界面
* */
public class RegistActivity extends BaseActivity implements RegistView{

    private EditText mUsername,mPsd,mRepsd;
    private Button mBtnFinish;

    private RegistPresenter mRegistPresenter;
    private TextInputLayout tl_username,tl_re_psd,tl_psd;
    private TextView tv_have;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        initView();
        mRegistPresenter = new RegistPresenterImpl(this);

        mBtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString().trim();
                String psd = mPsd.getText().toString().trim();
                String rePsd = mRepsd.getText().toString().trim();
                //检验用户名是否合法
                if (!StringUtils.CheckUsername(username)){
                    tl_username.setErrorEnabled(true);
                    tl_username.setError("用户名不合法");
                    return;
                }else {
                    tl_username.setErrorEnabled(false);
                }
                //检验密码是否合法
                if (!StringUtils.CheckPsd(psd)){
                    tl_psd.setErrorEnabled(true);
                    tl_psd.setError("密码不合法");
                    return;
                }else {
                    tl_psd.setErrorEnabled(false);
                }
                //检验两次密码是否输入一致
                if (!TextUtils.equals(psd,rePsd)){
                    tl_re_psd.setErrorEnabled(true);
                    tl_re_psd.setError("两次密码不一致！");
                    return;
                }else {
                    tl_re_psd.setErrorEnabled(false);
                }
                //显示对话框
                showProgressDialog("正在注册");
                mRegistPresenter.registUser(username,psd);
            }
        });
        tv_have.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivity(LoginActivity.class,true);
            }
        });
    }

    private void initView() {
        mUsername = (EditText) findViewById(R.id.et_username);
        mPsd = (EditText) findViewById(R.id.et_psd);
        mRepsd = (EditText) findViewById(R.id.et_re_psd);
        mBtnFinish = (Button) findViewById(R.id.bt_finish_regist);
        tl_username = (TextInputLayout) findViewById(R.id.tl_username);
        tl_psd = (TextInputLayout) findViewById(R.id.tl_psd);
        tl_re_psd = (TextInputLayout) findViewById(R.id.tl_re_psd);
        tv_have = (TextView) findViewById(R.id.tv_have);
    }

    @Override
    public void onGetRegistState(String username, String pswd, boolean isRegist, String erroMsg) {
        //走到这个方法中，说明已经获取到注册信息,隐藏对话框
        cancleProgressDialog();
        if (isRegist){
            PreUtils.setUserNamePsd(UIUtils.getContext(),username,pswd);
            ToastUtils.showToast("注册成功");
            StartActivity(LoginActivity.class,true);
        }else {
            //注册失败，提示错误信息
            ToastUtils.showToast("注册失败"+erroMsg);
        }
    }
}
