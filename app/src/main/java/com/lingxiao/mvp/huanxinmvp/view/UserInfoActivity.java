package com.lingxiao.mvp.huanxinmvp.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.UserCardPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.presenter.UserCardPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.lingxiao.mvp.huanxinmvp.widget.SignalCardLayout;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserInfoActivity extends BaseActivity implements UserCardView{

    @BindView(R.id.tb_user_info)
    Toolbar tbUserInfo;
    @BindView(R.id.iv_info_head)
    ImageView ivInfoHead;
    @BindView(R.id.card_change_head)
    CardView cardChangeHead;
    @BindView(R.id.card_info_name)
    SignalCardLayout infoName;
    @BindView(R.id.card_info_sex)
    SignalCardLayout infoSex;
    @BindView(R.id.card_info_desc)
    SignalCardLayout infoDesc;

    private int REQUEST_CODE = 1;
    private AlertDialog mDialog;
    private View dialogView;
    private String objId;
    private UserCardPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        setSupportActionBar(tbUserInfo);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        presenter = new UserCardPresenterImpl(this);
        presenter.getUserInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.card_info_name)
    public void changeName(){
        showChangeDia();
    }
    @OnClick(R.id.card_info_sex)
    public void changeSex(){
        showChangeDia();
    }
    @OnClick(R.id.card_info_desc)
    public void changeDesc(){
        showChangeDia();
    }
    @OnClick(R.id.card_change_head)
    public void changeHead(){
        //单选并剪裁
        ImageSelectorUtils.openPhotoAndClip(UserInfoActivity.this, REQUEST_CODE);
    }

    @OnClick(R.id.card_info_phone)
    public void changePhone(){

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            //获取选择器返回的数据
            ArrayList<String> images = data.getStringArrayListExtra(
                    ImageSelectorUtils.SELECT_RESULT);
            presenter.changeUserInfo(ContentValue.CHANGE_PROTRAIT,images.get(0),objId,0);
            showProgressDialog("上传中...");
        }
    }

    private void showChangeDia(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        mDialog = builder.create();
        dialogView = View.inflate(UIUtils.getContext(),
                R.layout.dialog_user_info,null);
        final EditText editText = dialogView.findViewById(R.id.et_dia_input);
        Button btConfirm = dialogView.findViewById(R.id.bt_dia_confirm);
        Button btCancel = dialogView.findViewById(R.id.bt_dia_cancel);
        mDialog.setView(dialogView);
        mDialog.show();

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String text = editText.getText().toString().trim();
                if (text.isEmpty()){
                    editText.setError("不能为空");
                    return;
                }
                presenter.changeUserInfo(1,"",null,0);
                mDialog.dismiss();
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
    }

    @Override
    public void onLogOut(boolean isSuccess, String errormsg) {

    }

    @Override
    public void onGetUserInfo(UserModel model) {
        try {
            Glide.with(UserInfoActivity.this)
                    .load(model.getProtrait())
                    .into(ivInfoHead);
            int sex = model.getSex();
            if (sex == 0){
                infoSex.setRightText("男");
            }else {
                infoSex.setRightText("女");
            }
            if (null == model.getDesc()){
                infoDesc.setRightText("还没有填写哟");
            }else {
                infoDesc.setRightText(model.getDesc());
            }
            if (null != model.getNickname()){
                infoName.setRightText(model.getNickname());
            }

            objId = model.getObjId();
        }catch (Exception e){
            ToastUtils.showToast("获取信息失败，请尝试重新登录"+e.getMessage());
            LogUtils.i(e.getMessage());
        }
    }

    @Override
    public void onChangeUserInfo(int type, boolean result) {
        UserModel model = SQLite
                .select()
                .from(UserModel.class)
                .querySingle();
        if (result){
            Glide.with(UserInfoActivity.this)
                    .load(model.getProtrait())
                    .into(ivInfoHead);
            cancleProgressDialog();
        }
    }
}
