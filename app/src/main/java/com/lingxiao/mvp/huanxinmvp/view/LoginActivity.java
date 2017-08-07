package com.lingxiao.mvp.huanxinmvp.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lingxiao.mvp.huanxinmvp.MainActivity;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.LoginPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.presenter.LoginPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.PreUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;

/**
 * Created by lingxiao on 17-6-28.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener,LoginView{

    private TextInputLayout tl_login_username,tl_login_psd;
    private EditText et_login_username,et_login_psd;
    private TextView tv_regist_now;
    private Button bt_login;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        tv_regist_now.setOnClickListener(this);
        bt_login.setOnClickListener(this);

        //获取缓存用户名密码
        if (PreUtils.getUserName(UIUtils.getContext())!=null&&PreUtils.getUserName(UIUtils.getContext()).trim().length()>0)
            et_login_username.setText(PreUtils.getUserName(UIUtils.getContext()));
        if (PreUtils.getPsd(UIUtils.getContext())!=null&&PreUtils.getPsd(UIUtils.getContext()).trim().length()>0)
            et_login_psd.setText(PreUtils.getPsd(UIUtils.getContext()));

        loginPresenter = new LoginPresenterImpl(this);
    }

    private void initView() {
        tl_login_username = (TextInputLayout) findViewById(R.id.tl_login_username);
        tl_login_psd = (TextInputLayout) findViewById(R.id.tl_login_psd);
        et_login_username = (EditText) findViewById(R.id.et_login_username);
        et_login_psd = (EditText) findViewById(R.id.et_login_psd);
        tv_regist_now = (TextView) findViewById(R.id.tv_regist_now);
        bt_login = (Button) findViewById(R.id.bt_login);
    }

    //在该activity的实例中已经存在于task或backtask中，当使用intent来再次启动该activity时，
    // 如果此次启动不再创建该activity的实例，则会调用原有的obnewintent
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //获取缓存用户名密码
        if (PreUtils.getUserName(this)!=null&&PreUtils.getUserName(this).trim().length()>0)
            et_login_username.setText(PreUtils.getUserName(this));
        if (PreUtils.getPsd(this)!=null&&PreUtils.getPsd(this).trim().length()>0)
            et_login_psd.setText(PreUtils.getPsd(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_regist_now:
                StartActivity(RegistActivity.class,true);
                break;
            case R.id.bt_login:
                login();
                break;
        }
    }

    private void login() {
        showProgressDialog("正在登录...");
        String username = et_login_username.getText().toString().trim();
        String pwd = et_login_psd.getText().toString().trim();
        if (username.isEmpty()){
            tl_login_username.setErrorEnabled(true);
            tl_login_username.setError("用户名不能为空!");
            cancleProgressDialog();
            return;
        }else if (pwd.isEmpty()){
            tl_login_psd.setErrorEnabled(true);
            tl_login_psd.setError("密码不能为空!");
            cancleProgressDialog();
            return;
        }else {
            tl_login_psd.setErrorEnabled(false);
            tl_login_username.setErrorEnabled(false);
        }
        // 如果用到隐私的权限 6.0之后 需要手动检测是否有权限
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                PermissionChecker.PERMISSION_GRANTED){
            //如果有权限的话就执行登录
            loginPresenter.login(username,pwd);
        }else{
            //如果没有权限 就动态申请
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }
    }

    @Override
    public void onGetLoginState(String usernme, boolean isLogin, String erroMsg) {
        cancleProgressDialog();
        if (isLogin){
            StartActivity(MainActivity.class,true);
        }else {
            ToastUtils.showToast("登录失败"+erroMsg);
        }
    }
    //申请权限之后的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //requestCode 区分不同的权限申请操作 注意requestCode不能<0
        //permissions 动态申请的权限数组
        //grantResults int数组 用来封装每一个权限授权的结果  PermissionChecker.PERMISSION_GRANTED 授权了
        //PermissionChecker.PERMISSION_DENIED; 拒绝了
        if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED){
            //说明授权了
            login();
        }else {
            ProgressDialog prog = new ProgressDialog(UIUtils.getContext());
            prog.setTitle("没授权sd卡权限 数据库保存会失败 影响使用");
            prog.setIcon(R.mipmap.ic_launcher);
        }
    }
}
