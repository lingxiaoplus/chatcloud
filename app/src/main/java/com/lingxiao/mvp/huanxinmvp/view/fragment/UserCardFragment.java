package com.lingxiao.mvp.huanxinmvp.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.UserCardPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.presenter.UserCardPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.view.BaseActivity;
import com.lingxiao.mvp.huanxinmvp.view.LoginActivity;
import com.lingxiao.mvp.huanxinmvp.view.UserCardView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lingxiao on 17-6-29.
 */

public class UserCardFragment extends BaseFragment implements UserCardView {

    @BindView(R.id.cv_mine_info)
    CardView cvInfo;
    @BindView(R.id.cv_mine_theme)
    CardView cvTheme;
    @BindView(R.id.cv_mine_setting)
    CardView cvSetting;
    @BindView(R.id.tv_mine_username)
    TextView tvMineUsername;
    @BindView(R.id.img_mine_protrait)
    CircleImageView headImgView;

    private UserCardPresenter presenter;
    private ProgressDialog dialog;

    @Override
    public void initData() {
        presenter.getUserInfo();
    }

    @Override
    public View initView() {
        presenter = new UserCardPresenterImpl(this);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        //View view = UIUtils.inflateView(R.layout.fragment_user_card);
        View view = View.inflate(getContext(), R.layout.fragment_user_card, null);
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
        try {
            tvMineUsername.setText(model.getUsername());
            Glide.with(getActivity())
                    .load(model.getProtrait())
                    .into(headImgView);
        } catch (Exception e){
            ToastUtils.showToast("获取信息失败，请重新登录");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @OnClick(R.id.cv_mine_info)
    public void changeInfo(View v) {

    }
}
