package com.lingxiao.mvp.huanxinmvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.RegistPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.presenter.RegistPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.SpUtils;
import com.lingxiao.mvp.huanxinmvp.utils.StringUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ThreadUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.lingxiao.mvp.huanxinmvp.view.regist.RegistInfoActivity;

import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.SMSSDK;

/*
 * 注册界面
 * */
public class RegistActivity extends BaseActivity implements RegistView {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_psd)
    EditText etPsd;
    @BindView(R.id.et_re_psd)
    EditText etRePsd;
    @BindView(R.id.tv_have)
    TextView tvHave;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.bt_send_code)
    Button btSendCode;
    @BindView(R.id.et_checkcode)
    EditText etCheckcode;
    @BindView(R.id.btn_submit)
    net.qiujuer.genius.ui.widget.Button mSubmit;
    @BindView(R.id.loading)
    Loading mLoading;

    private RegistPresenter mRegistPresenter;
    private CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long l) {
            btSendCode.setEnabled(false);
            btSendCode.setText(l / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            btSendCode.setEnabled(true);
            btSendCode.setText("发送验证码");
        }
    };
    private String username;
    private String phone;
    private String psd;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);
        mRegistPresenter = new RegistPresenterImpl(this);
    }

    @Override
    public void onGetRegistState(String objcectId, String username, String pswd, boolean isRegist, String erroMsg) {
        //走到这个方法中，说明已经获取到注册信息,隐藏对话框
        // 开始Loading
        mLoading.stop();
        etUsername.setEnabled(true);
        etPhone.setEnabled(true);
        etCheckcode.setEnabled(true);
        etPsd.setEnabled(true);
        etRePsd.setEnabled(true);
        mSubmit.setEnabled(true);
        if (null != timer) {
            timer.cancel();btSendCode.setEnabled(true);
            btSendCode.setText("发送验证码");
        }
        if (isRegist) {

            SpUtils.putString(UIUtils.getContext(),
                    ContentValue.KEY_USERNAME, username);
            SpUtils.putString(UIUtils.getContext(),
                    ContentValue.KEY_PSD, pswd);
            Intent intent = new Intent(UIUtils.getContext(),
                    RegistInfoActivity.class);
            intent.putExtra("objectid", objcectId);
            startActivity(intent);
            /*ToastUtils.showToast("注册成功");
            StartActivity(LoginActivity.class,true);*/
        } else {
            //注册失败，提示错误信息
            ToastUtils.showToast("注册失败" + erroMsg);
        }
    }

    @Override
    public void onGetCode(final boolean result, final String msg) {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (result) {
                    //说明发送成功
                    timer.start();
                } else {

                    ToastUtils.showToast("验证码发送失败" + msg);
                }
            }
        });

    }

    @Override
    public void onSubmitCode(final boolean result, int msg) {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (result) {
                    //说明验证成功 跳转到填写用户信息界面
                    mRegistPresenter.registUser(username, psd, phone);

                } else {
                    mLoading.stop();
                    etUsername.setEnabled(true);
                    etPhone.setEnabled(true);
                    etCheckcode.setEnabled(true);
                    etPsd.setEnabled(true);
                    etRePsd.setEnabled(true);
                    mSubmit.setEnabled(true);
                    ToastUtils.showToast("验证码错误");
                }
            }
        });
    }

    @OnClick(R.id.btn_submit)
    public void onNext() {
        username = etUsername.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        code = etCheckcode.getText().toString().trim();
        psd = etPsd.getText().toString().trim();
        String rePsd = etRePsd.getText().toString().trim();
        //检验用户名是否合法
        if (!StringUtils.CheckUsername(username)) {
            etUsername.setError("用户名不合法");
            return;
        }
        //检验密码是否合法
        if (!StringUtils.CheckPsd(psd)) {
            etPsd.setError("密码不合法");
            return;
        }
        //检验两次密码是否输入一致
        if (!TextUtils.equals(psd, rePsd)) {
            etRePsd.setError("两次密码不一致！");
            return;
        }
        mRegistPresenter.submitCode(phone, code);
        // 开始Loading
        mLoading.start();
        // 让控件不可以输入
        etUsername.setEnabled(false);
        etPhone.setEnabled(false);
        etCheckcode.setEnabled(false);
        etPsd.setEnabled(false);
        etRePsd.setEnabled(false);
        // 提交按钮不可以继续点击
        mSubmit.setEnabled(false);

    }

    @OnClick(R.id.tv_have)
    public void onLogin() {
        StartActivity(LoginActivity.class, true);
    }

    @OnClick(R.id.bt_send_code)
    public void sendCode() {
        String phone = etPhone.getText().toString().trim();
        if (phone.length() != 11) {
            ToastUtils.showToast("请输入十一位手机号");
            return;
        }
        mRegistPresenter.getCheckCode(phone);
    }

    protected void onDestroy() {
        super.onDestroy();
        //用完回调要注销掉，否则可能会出现内存泄露
        SMSSDK.unregisterAllEventHandler();
        timer.cancel();
    }
}
