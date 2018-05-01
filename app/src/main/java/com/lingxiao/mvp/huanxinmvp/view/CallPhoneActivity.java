package com.lingxiao.mvp.huanxinmvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.presenter.CallPresenter;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.CallPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.utils.GlideHelper;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ThreadUtils;
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
    @BindView(R.id.bt_call_end)
    FloatingActionButton btCallend;
    @BindView(R.id.opposite_surfaceview)
    RelativeLayout opSurfaceview;
    @BindView(R.id.local_surfaceview)
    RelativeLayout localSurfaceview;

    private CallPresenter presenter;
    private long callTime;
    private EMOppositeSurfaceView oppositeSurface;
    private EMLocalSurfaceView localSurface;
    private RelativeLayout.LayoutParams mParams;
    private String mProtrait;

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
        mProtrait = intent.getStringExtra("protrait");
        if (type == 0) {
            presenter.callPhone(name);
        } else if (type == 1) {
            presenter.callVideo(name);
            initSurfaceView();
        }
        if (null != mProtrait){
            GlideHelper.loadImageView(mProtrait,imgCallHead);
        }
    }

    @OnClick(R.id.bt_call_end)
    public void cancelCall() {
        presenter.endCall();
    }

    @Override
    public void getCallStatus(final int status, final String msg) {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (null == msg) {
                    if (status == ContentValue.CALL_CONNECTING) {
                        //等对方响应
                    } else if (status == ContentValue.CALL_ACCEPTED) {
                        //接听了
                        //opSurfaceview.setVisibility(View.VISIBLE);
                        //localSurface.release();
                        ToastUtils.showToast("接通电话了");
                    } else if (status == ContentValue.CALL_DISCONNECTED) {
                        long time = System.currentTimeMillis();
                        if (time - callTime < 1000) {
                            //说明对方不在线
                            ToastUtils.showToast("对方可能不在线");
                        }else {
                            finish();
                        }
                    }
                } else {
                    ToastUtils.showToast("对方可能不在线" );
                }
            }
        });

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
        imgCallHead.setVisibility(View.INVISIBLE);
        mParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        // 初始化显示远端画面控件
        oppositeSurface = new EMOppositeSurfaceView(this);
        oppositeSurface.setLayoutParams(mParams);
        opSurfaceview.addView(oppositeSurface);

        //初始化本地视频画面
        localSurface = new EMLocalSurfaceView(this);
        //localSurface.setLayoutParams(mParams);
        localSurfaceview.addView(localSurface);

        EMClient.getInstance().callManager().setSurfaceView(localSurface,oppositeSurface);
    }
}
