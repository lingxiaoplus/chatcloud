package com.lingxiao.mvp.huanxinmvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.czy1121.view.RoundButton;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.presenter.AcceptCallPresenter;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.AcceptCallPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class AcceptCallActivity extends BaseActivity implements AcceptCallView {

    @BindView(R.id.img_accept_head)
    CircleImageView imgAcceptHead;
    @BindView(R.id.tv_accept_name)
    TextView tvAcceptName;
    @BindView(R.id.bt_accept_ensure)
    RoundButton btAcceptEnsure;
    @BindView(R.id.bt_accept_cancel)
    RoundButton btAcceptCancel;
    @BindView(R.id.bt_accept_end)
    RoundButton btAcceptEnd;
    private Intent intent;
    private AcceptCallPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_call);
        ButterKnife.bind(this);
        presenter = new AcceptCallPresenterImpl(this);
        intent = getIntent();
        initData();
    }

    private void initData() {
        String from = intent.getStringExtra("from");
        String type = intent.getStringExtra("type");
        ToastUtils.showToast("类型："+type);
        tvAcceptName.setText(from);
    }

    @OnClick(R.id.bt_accept_ensure)
    public void onAnswer() {
        presenter.answerCall();
    }

    @OnClick(R.id.bt_accept_cancel)
    public void onCancel() {
        presenter.cancelCall();
    }
    @OnClick(R.id.bt_accept_end)
    public void onEnd(){
        presenter.endCall();
    }
    @Override
    public void onAnswerCall(boolean result, String msg) {
        if (result) {
            btAcceptEnsure.setVisibility(View.GONE);
            btAcceptCancel.setVisibility(View.GONE);
            btAcceptEnd.setVisibility(View.VISIBLE);
        } else {
            ToastUtils.showToast(msg);
        }
    }

    @Override
    public void onRejectCall(boolean result, String msg) {
        if (result) {
            presenter.callListener();
        } else {
            ToastUtils.showToast(msg);
        }
    }

    @Override
    public void onEndCall(boolean result, String msg) {
        if (result) {
            presenter.callListener();
        } else {
            ToastUtils.showToast(msg);
        }
    }

    @Override
    public void onGetCallStatus(int status) {
        if (status == ContentValue.CALL_DISCONNECTED){
            finish();
        }
    }
}
