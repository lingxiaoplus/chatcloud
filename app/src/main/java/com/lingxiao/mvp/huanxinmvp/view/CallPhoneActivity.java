package com.lingxiao.mvp.huanxinmvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.czy1121.view.RoundButton;
import com.hyphenate.chat.EMClient;
import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.presenter.CallPresenter;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.CallPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class CallPhoneActivity extends BaseActivity implements CallView {

    @BindView(R.id.img_call_head)
    CircleImageView imgCallHead;
    @BindView(R.id.tv_call_name)
    TextView tvCallName;
    @BindView(R.id.bt_call_cancel)
    RoundButton btCallCancel;
    @BindView(R.id.opposite_surfaceview)
    RelativeLayout opSurfaceview;

    private CallPresenter presenter;
    private long callTime;
    private EMOppositeSurfaceView oppositeSurface;
    private EMLocalSurfaceView localSurface;
    private RelativeLayout.LayoutParams oppositeParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_phone);
        ButterKnife.bind(this);
        presenter = new CallPresenterImpl(this);
        callTime = System.currentTimeMillis();
        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);
        String name = intent.getStringExtra("name");
        if (type == 0) {
            presenter.callPhone(name);
        } else if (type == 1) {
            presenter.callVideo(name);
        }
        //initSurfaceView();
    }

    @OnClick(R.id.bt_call_cancel)
    public void cancelCall() {
        presenter.endCall();
    }

    @Override
    public void getCallStatus(int status, String msg) {
        if (null == msg) {
            if (status == ContentValue.CALL_CONNECTING) {
                //等对方响应
            } else if (status == ContentValue.CALL_ACCEPTED) {
                //接听了
                opSurfaceview.setVisibility(View.VISIBLE);
                //localSurface.release();
            } else if (status == ContentValue.CALL_DISCONNECTED) {
                long time = System.currentTimeMillis();
                if (time - callTime > 2000) {
                    //说明对方不在线
                    ToastUtils.showToast("对方可能不在线");
                    return;
                }
                finish();
            }
        } else {
            ToastUtils.showToast("拨号失败：" + msg);
            finish();
        }
    }

    @Override
    public void endCall(boolean result, String msg) {
        if (result) {

        } else {
            ToastUtils.showToast(msg);
        }
        finish();
    }

    private void initSurfaceView(){
        // 初始化显示远端画面控件
        oppositeSurface = new EMOppositeSurfaceView(this);
        oppositeParams = new RelativeLayout.LayoutParams(0, 0);
        oppositeParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        oppositeParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        oppositeSurface.setLayoutParams(oppositeParams);
        opSurfaceview.addView(oppositeSurface);
        //oppositeSurface.setVisibility(View.INVISIBLE);

        localSurface = new EMLocalSurfaceView(this);
        localSurface.setLayoutParams(oppositeParams);
        opSurfaceview.addView(localSurface);
        localSurface.setVisibility(View.INVISIBLE);
        EMClient.getInstance().callManager().setSurfaceView(localSurface,oppositeSurface);
    }
}
