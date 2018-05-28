package com.lingxiao.mvp.huanxinmvp.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.event.SkinChangeEvent;
import com.lingxiao.mvp.huanxinmvp.event.UserChangedEvent;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.UserCardPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.presenter.UserCardPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.GlideHelper;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.SkinUtil;
import com.lingxiao.mvp.huanxinmvp.utils.SpUtils;
import com.lingxiao.mvp.huanxinmvp.utils.StringUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.lingxiao.mvp.huanxinmvp.view.BaseActivity;
import com.lingxiao.mvp.huanxinmvp.view.LoginActivity;
import com.lingxiao.mvp.huanxinmvp.view.SettingActivity;
import com.lingxiao.mvp.huanxinmvp.view.SkinActivity;
import com.lingxiao.mvp.huanxinmvp.view.UserCardView;
import com.lingxiao.mvp.huanxinmvp.view.UserInfoActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    @BindView(R.id.iv_mine_theme)
    AppCompatImageView ivTheme;
    @BindView(R.id.iv_mine_setting)
    AppCompatImageView ivSetting;

    private UserCardPresenter presenter;
    private ProgressDialog dialog;

    @Override
    public void initData() {
        presenter.getUserInfo();
        int color = SpUtils.getInt(getActivity(),ContentValue.SKIN_SVG,-1);
        if (color != -1){
            UIUtils.changeSVGColor(ivTheme,R.drawable.ic_img_theme,color);
            UIUtils.changeSVGColor(ivSetting,R.drawable.ic_img_setting,color);
        }
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
            tvMineUsername.setText(model.getNickname());
            GlideHelper.loadImageWithData(
                    model.getProtrait(),
                    headImgView,
                    model.getUpdateTime());
            String data = StringUtils.transferLongToDate(model.getUpdateTime());
            LogUtils.i("获取本地更新用户信息的时间"+data);
        } catch (Exception e) {
            ToastUtils.showToast("获取信息失败，请重新登录");
        }
    }

    @Override
    public void onChangeUserInfo(int type, boolean result) {

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
        StartActivity(UserInfoActivity.class);
    }

    @OnClick(R.id.cv_mine_theme)
    public void changeTheme() {
        StartActivity(SkinActivity.class);
    }

    @OnClick(R.id.cv_mine_setting)
    public void startSetting(){
        StartActivity(SettingActivity.class);
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeSkin(SkinChangeEvent event) {
        //换肤
        SkinUtil.changeSVGColor(ivTheme,R.drawable.ic_img_theme,
                event.color, UIUtils.getContext());
        SkinUtil.changeSVGColor(ivSetting,R.drawable.ic_img_setting,
                event.color, UIUtils.getContext());
    }*/

    /**
     * 在settingact中修改了用户信息
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeInfo(UserChangedEvent event) {
        tvMineUsername.setText(event.nickname);
        Glide.with(getActivity())
                .load(event.protrait)
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .into(headImgView);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSkinChange(SkinChangeEvent event){
        SpUtils.putInt(UIUtils.getContext(), ContentValue.SKIN_SVG,event.color);
        UIUtils.changeSVGColor(ivTheme,R.drawable.ic_img_theme,event.color);
        UIUtils.changeSVGColor(ivSetting,R.drawable.ic_img_setting,event.color);
    }
    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }
}
