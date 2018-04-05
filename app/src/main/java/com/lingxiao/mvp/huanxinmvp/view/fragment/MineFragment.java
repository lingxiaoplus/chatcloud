package com.lingxiao.mvp.huanxinmvp.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.LogoutPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.presenter.LogoutPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.view.BaseActivity;
import com.lingxiao.mvp.huanxinmvp.view.LoginActivity;
import com.lingxiao.mvp.huanxinmvp.view.MineView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by lingxiao on 17-6-29.
 */

public class MineFragment extends BaseFragment implements MineView {

    @BindView(R.id.cv_mine_info)
    CardView cvInfo;
    @BindView(R.id.cv_mine_theme)
    CardView cvTheme;
    @BindView(R.id.cv_mine_setting)
    CardView cvSetting;

    private LogoutPresenter presenter;
    private ProgressDialog dialog;

    @Override
    public void initData() {

    }

    @Override
    public View initView() {
        presenter = new LogoutPresenterImpl(this);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        //View view = UIUtils.inflateView(R.layout.fragment_mine);
        View view = View.inflate(getContext(), R.layout.fragment_mine, null);

        return view;
    }

    @Override
    public void onLogOut(boolean isSuccess, String errormsg) {
        if (isSuccess) {
            BaseActivity activity = (BaseActivity) getActivity();
            activity.StartActivity(LoginActivity.class, true);
        } else {
            ToastUtils.showToast("注销失败" + errormsg);
        }
    }

    @Override
    public void onGetUserInfo(UserModel model) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @OnClick(R.id.cv_mine_info)
    public void changeInfo(View v){
        ToastUtils.showToast("点击了信息");
    }
}
