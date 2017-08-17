package com.lingxiao.mvp.huanxinmvp.view.fragment;

import android.app.ProgressDialog;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.lingxiao.mvp.huanxinmvp.MainActivity;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.LogoutPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.presenter.LogoutPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.DataCleanUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.lingxiao.mvp.huanxinmvp.view.BaseActivity;
import com.lingxiao.mvp.huanxinmvp.view.LoginActivity;
import com.lingxiao.mvp.huanxinmvp.view.MineView;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by lingxiao on 17-6-29.
 */

public class MineFragment extends BaseFragment implements MineView{

    private LogoutPresenter presenter;
    private ProgressDialog dialog;
    private TextView tv_mine_name,tv_mine_cache;
    private Button bt_logout;

    @Override
    public void initData() {

    }

    @Override
    public View initView() {
        presenter = new LogoutPresenterImpl(this);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        //View view = UIUtils.inflateView(R.layout.fragment_mine);
        View view = View.inflate(getContext(),R.layout.fragment_mine,null);
        tv_mine_name = (TextView) view.findViewById(R.id.tv_mine_name);
        bt_logout = (Button) view.findViewById(R.id.bt_logout);
        tv_mine_cache = view.findViewById(R.id.tv_mine_cache);
        tv_mine_name.setText(EMClient.getInstance().getCurrentUser());
        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("正在注销...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                presenter.logOut();
            }
        });

        tv_mine_cache.setText("3.6MB");
        return view;
    }

    @Override
    public void onLogOut(boolean isSuccess, String errormsg) {
        if (isSuccess){
            BaseActivity activity = (BaseActivity) getActivity();
            activity.StartActivity(LoginActivity.class,true);
        }else {
            ToastUtils.showToast("注销失败"+errormsg);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null){
            dialog.dismiss();
        }
    }
}
