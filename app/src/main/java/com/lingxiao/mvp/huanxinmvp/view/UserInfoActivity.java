package com.lingxiao.mvp.huanxinmvp.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.event.UserChangedEvent;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.google.encoding.EncodingHandler;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel_Table;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.UserCardPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.presenter.UserCardPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.DBUtils;
import com.lingxiao.mvp.huanxinmvp.utils.GlideHelper;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.MD5Util;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.lingxiao.mvp.huanxinmvp.widget.SignalCardLayout;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserInfoActivity extends BaseActivity implements UserCardView {

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
    @BindView(R.id.card_info_phone)
    SignalCardLayout infoPhone;
    @BindView(R.id.iv_info_qrcode)
    ImageView imgQrcode;
    @BindView(R.id.card_info_username)
    SignalCardLayout cardUsername;

    private int REQUEST_CODE = 1;
    private AlertDialog mDialog;
    private View dialogView;
    private String objId;
    private UserCardPresenter presenter;

    private int sexPos; //记录性别
    private String mUsername;

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
    public void changeName() {
        showChangeDia(0);
    }

    @OnClick(R.id.card_info_sex)
    public void changeSex() {
        selectSex();
    }

    @OnClick(R.id.card_info_desc)
    public void changeDesc() {
        showChangeDia(1);
    }

    @OnClick(R.id.card_change_head)
    public void changeHead() {
        //单选并剪裁
        ImageSelectorUtils.openPhotoAndClip(UserInfoActivity.this, REQUEST_CODE);
    }

    @OnClick(R.id.card_info_phone)
    public void changePhone() {
        ToastUtils.showToast("无法修改绑定电话");
    }

    @OnClick(R.id.card_change_qrcode)
    public void getQrCode() {
        Intent intent = new Intent(getApplicationContext(), LocalPicActivity.class);
        intent.putExtra("qrcode", true);
        intent.putExtra("username", mUsername);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            //获取选择器返回的数据
            ArrayList<String> images = data.getStringArrayListExtra(
                    ImageSelectorUtils.SELECT_RESULT);
            presenter.changeUserInfo(ContentValue.CHANGE_PROTRAIT, images.get(0), objId, 0);
            showProgressDialog("上传中...");
        }
    }

    private void showChangeDia(final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        /*dialogView = View.inflate(UIUtils.getContext(),
                R.layout.dialog_user_info,null);*/
        //final EditText editText = dialogView.findViewById(R.id.et_dia_input);
        //Button btConfirm = dialogView.findViewById(R.id.bt_dia_confirm);
        //Button btCancel = dialogView.findViewById(R.id.bt_dia_cancel);
        //mDialog.setView(dialogView);
        builder.setTitle("修改个人信息");
        final EditText editText = new EditText(this);
        editText.setHint("改成什么呢?");
        editText.setHintTextColor(Color.GRAY);
        builder.setView(editText);
        String name;
        if (type == 0) {
            name = infoName.getRightText();
        } else {
            name = infoDesc.getRightText();
        }
        if (!name.isEmpty()) {
            editText.setText(name);
        }
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String text = editText.getText().toString().trim();
                if (text.isEmpty()) {
                    ToastUtils.showToast("不能为空");
                    return;
                }
                if (type == 0) {
                    if (text.length() > 9) {
                        ToastUtils.showToast("昵称太长了吧！");
                        return;
                    }
                    presenter.changeUserInfo(ContentValue.CHANGE_NAME,
                            text, "", 0);
                } else if (type == 1) {
                    presenter.changeUserInfo(ContentValue.CHANGE_DESC,
                            text, "", 0);
                }
                mDialog.dismiss();
                showProgressDialog("修改中...");
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDialog.dismiss();
            }
        });
        mDialog = builder.create();
        mDialog.show();
    }


    /**
     * 性别选择
     */
    private void selectSex() {
        String[] str = {"男", "女"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(str, sexPos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    presenter.changeUserInfo(3, "", "", 0);
                } else {
                    presenter.changeUserInfo(3, "", "", 1);
                }
                dialogInterface.dismiss();
                showProgressDialog("修改中");
            }
        });
        builder.show();
    }

    @Override
    public void onLogOut(boolean isSuccess, String errormsg) {

    }

    @Override
    public void onGetUserInfo(UserModel model) {
        try {

            GlideHelper.loadImageWithData(
                    model.getProtrait(),
                    ivInfoHead,
                    model.getUpdateTime());
            int sex = model.getSex();
            if (sex == 0) {
                infoSex.setRightText("男");
                sexPos = 0;
            } else {
                infoSex.setRightText("女");
                sexPos = 1;
            }
            if (null == model.getDesc()) {
                infoDesc.setRightText("还没有填写哟");
            } else {
                infoDesc.setRightText(model.getDesc());
            }
            if (null != model.getNickname()) {
                infoName.setRightText(model.getNickname());
            }
            infoPhone.setRightText(model.getPhone());
            cardUsername.setRightText(model.getUsername());
            objId = model.getObjId();
            //生成二维码
            //300表示宽高
            mUsername = model.getUsername();
            Bitmap bitmap = EncodingHandler
                    .createQRCode(mUsername, 20);
            imgQrcode.setImageBitmap(bitmap);
        } catch (Exception e) {
            ToastUtils.showToast("获取信息失败，请尝试重新登录" + e.getMessage());
            LogUtils.i(e.getMessage());
        }
    }

    @Override
    public void onChangeUserInfo(final int type, boolean result) {
        final UserModel model = SQLite
                .select()
                .from(UserModel.class)
                .querySingle();
        if (result) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (type == ContentValue.CHANGE_PROTRAIT) {
                                Glide.with(UserInfoActivity.this)
                                        .load(model.getProtrait())
                                        .skipMemoryCache(true) // 不使用内存缓存
                                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                                        .into(ivInfoHead);
                            } else if (type == ContentValue.CHANGE_NAME) {
                                infoName.setRightText(model.getNickname());
                            } else if (type == ContentValue.CHANGE_DESC) {
                                infoDesc.setRightText(model.getDesc());
                            } else if (type == ContentValue.CHANGE_SEX) {
                                if (model.getSex() == 0) {
                                    infoSex.setRightText("男");
                                    sexPos = 0;
                                } else {
                                    infoSex.setRightText("女");
                                    sexPos = 1;
                                }
                            } else if (type == ContentValue.CHANGE_AGE) {

                            }
                            cancleProgressDialog();
                            EventBus.getDefault().post(new UserChangedEvent(model.getNickname()
                                    , model.getProtrait()));
                        }
                    });
                }
            }).start();

        } else {
            ToastUtils.showToast("修改失败，请重试");
            cancleProgressDialog();
        }
    }
}
