package com.lingxiao.mvp.huanxinmvp.view.regist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.utils.GlideHelper;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.QiNiuHelper.Auth;
import com.lingxiao.mvp.huanxinmvp.utils.QiNiuHelper.StringMap;
import com.lingxiao.mvp.huanxinmvp.utils.QiNiuSdkHelper;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.lingxiao.mvp.huanxinmvp.view.BaseActivity;
import com.lingxiao.mvp.huanxinmvp.view.LoginActivity;

import net.qiujuer.genius.ui.widget.Loading;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegistInfoActivity extends BaseActivity implements QiNiuSdkHelper.uploadListener {

    @BindView(R.id.img_upload_header)
    CircleImageView imgUploadHeader;
    @BindView(R.id.tv_upload)
    TextView tvUpload;
    @BindView(R.id.male)
    RadioButton male;
    @BindView(R.id.femle)
    RadioButton femle;
    @BindView(R.id.rg_sex)
    RadioGroup rgSex;
    @BindView(R.id.et_upload_nickname)
    EditText etUploadNickname;
    @BindView(R.id.et_upload_desc)
    EditText etUploadDesc;
    @BindView(R.id.btn_info_submit)
    net.qiujuer.genius.ui.widget.Button mSubmit;
    @BindView(R.id.loading_info)
    Loading mLoading;

    private String mObjcectId;
    private int REQUEST_CODE = 1;
    private int mSex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_info);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mObjcectId = intent.getStringExtra("objectid");

        initData();
    }

    private void initData() {
        QiNiuSdkHelper.getInstance().setUploadListener(this);
        rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mSex = i == R.id.male ? 0 : 1;
            }
        });
    }

    @OnClick(R.id.img_upload_header)
    public void uploadProtrait() {
        //单选并剪裁
        ImageSelectorUtils.openPhotoAndClip(RegistInfoActivity.this, REQUEST_CODE);
    }

    @OnClick(R.id.btn_info_submit)
    public void uploadInfo() {
        // 开始Loading
        mLoading.start();
        // 让控件不可以输入
        etUploadNickname.setEnabled(false);
        etUploadDesc.setEnabled(false);
        mSubmit.setEnabled(false);
        rgSex.setEnabled(false);
        imgUploadHeader.setEnabled(false);
        saveInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            //获取选择器返回的数据
            ArrayList<String> images = data.getStringArrayListExtra(
                    ImageSelectorUtils.SELECT_RESULT);
            //presenter.changeUserInfo(ContentValue.CHANGE_PROTRAIT,images.get(0),objId,0);
            QiNiuSdkHelper.getInstance().upload(images.get(0), mObjcectId, getToken(mObjcectId));
            showProgressDialog("上传中...");
            GlideHelper.loadImageView(images.get(0), imgUploadHeader);
        }
    }

    private void saveProtrait(final String url) {
        AVUser.getCurrentUser().put(ContentValue.PROTRAIT, url);
        AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                cancleProgressDialog();
                if (e == null) {
                } else {
                    ToastUtils.showToast(e.getMessage());
                }
            }
        });
    }

    private void saveInfo() {
        String nickName = etUploadNickname.getText().toString().trim();
        String desc = etUploadDesc.getText().toString().trim();
        if (TextUtils.isEmpty(nickName)) {
            etUploadNickname.setError("昵称不能为空");
            return;
        }
        if (!TextUtils.isEmpty(desc)) {
            AVUser.getCurrentUser().put(ContentValue.DESCRIPTION, desc);
        }
        AVUser.getCurrentUser().put(ContentValue.NICKNAME, nickName);
        AVUser.getCurrentUser().put(ContentValue.SEX, mSex);
        AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (null == e) {
                    StartActivity(LoginActivity.class, true);
                } else {
                    mLoading.stop();
                    // 让控件不可以输入
                    etUploadNickname.setEnabled(true);
                    etUploadDesc.setEnabled(true);
                    mSubmit.setEnabled(true);
                    rgSex.setEnabled(true);
                    imgUploadHeader.setEnabled(true);
                    ToastUtils.showToast(e.getMessage());
                }
            }
        });
    }

    @Override
    public void onSuccess(String url) {
        saveProtrait(url);
    }

    @Override
    public void onFaild(String msg) {
        ToastUtils.showToast(msg);
        cancleProgressDialog();
    }

    private String getToken(String key) {
        //这句就是生成token  bucket:key 允许覆盖同名文件
        //insertOnly 如果希望只能上传指定key的文件，
        //并且不允许修改，那么可以将下面的 insertOnly 属性值设为 1
        LogUtils.i("七牛云的bucket和key：" + ContentValue.BUCKET + "   " + key);
        String token = Auth.create(UIUtils
                .getContext()
                .getResources()
                .getString(R.string.AccessKey), UIUtils
                .getContext()
                .getResources()
                .getString(R.string.SecretKey))
                .uploadToken(ContentValue.BUCKET,
                        key, 3600, new StringMap().put("insertOnly", 0));
        return token;
    }
}
