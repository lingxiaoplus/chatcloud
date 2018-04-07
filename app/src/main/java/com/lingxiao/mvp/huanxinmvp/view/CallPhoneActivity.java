package com.lingxiao.mvp.huanxinmvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.github.czy1121.view.RoundButton;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.presenter.CallPresenter;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.CallPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class CallPhoneActivity extends BaseActivity implements CallView{

    @BindView(R.id.img_call_head)
    CircleImageView imgCallHead;
    @BindView(R.id.tv_call_name)
    TextView tvCallName;
    @BindView(R.id.bt_call_cancel)
    RoundButton btCallCancel;

    private CallPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_phone);
        ButterKnife.bind(this);
        presenter = new CallPresenterImpl(this);
        Intent intent = getIntent();
        int type = intent.getIntExtra("type",0);
        String name = intent.getStringExtra("name");
        if (type == 0){
            presenter.callPhone(name);
        }else if (type == 1){
            presenter.callVideo(name);
        }
    }

    @OnClick(R.id.bt_call_cancel)
    public void cancelCall(){
        presenter.endCall();
    }

    @Override
    public void getCallStatus(int status, String msg) {
        if (null == msg){
            if (status == ContentValue.CALL_CONNECTING){
                //等对方响应
            }else if (status == ContentValue.CALL_ACCEPTED){
                //接听了
            }else if (status == ContentValue.CALL_DISCONNECTED){
                finish();
            }
        }else {
            ToastUtils.showToast("拨号失败："+msg);
            finish();
        }
    }

    @Override
    public void endCall(boolean result, String msg) {
        if (result){
            finish();
        }else {
            ToastUtils.showToast(msg);
        }
    }
}
