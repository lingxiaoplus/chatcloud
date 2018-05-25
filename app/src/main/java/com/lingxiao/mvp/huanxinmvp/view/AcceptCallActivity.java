package com.lingxiao.mvp.huanxinmvp.view;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel_Table;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.model.UserModel_Table;
import com.lingxiao.mvp.huanxinmvp.presenter.AcceptCallPresenter;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.AcceptCallPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.utils.GlideHelper;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

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
    FloatingActionButton btAcceptEnsure;
    @BindView(R.id.bt_accept_cancel)
    FloatingActionButton btAcceptCancel;
    @BindView(R.id.bt_accept_end)
    FloatingActionButton btAcceptEnd;
    @BindView(R.id.oppo_surface)
    EMOppositeSurfaceView oppoSurface;
    @BindView(R.id.local_surface)
    EMLocalSurfaceView localSurface;
    private Intent intent;
    private AcceptCallPresenter presenter;
    private ContactsModel mContactModel;

    private SoundPool mSoundPool;
    private int callSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_call);
        ButterKnife.bind(this);
        presenter = new AcceptCallPresenterImpl(this);
        initSoundPool();
        intent = getIntent();
        initData();
        presenter.callListener();
    }


    private void initSoundPool() {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_RING,0);
        callSound = mSoundPool.load(this,R.raw.phonering,1);
    }
    private void initData() {
        try {
            String from = intent.getStringExtra("from");
            String type = intent.getStringExtra("type");
            ToastUtils.showToast("类型：" + type);

            mContactModel = SQLite
                    .select()
                    .from(ContactsModel.class)
                    .where(ContactsModel_Table.contactUserName.eq(from))
                    .querySingle();
            tvAcceptName.setText(mContactModel.getNickName());
            if (type.equals("voice")) {
                GlideHelper.loadImageView(mContactModel.getProtrait(), imgAcceptHead);
                oppoSurface.setVisibility(View.GONE);
                localSurface.setVisibility(View.GONE);
            } else {
                imgAcceptHead.setVisibility(View.INVISIBLE);
                oppoSurface.setVisibility(View.VISIBLE);
                localSurface.setVisibility(View.VISIBLE);
                initSurfaceView();
            }

            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                    //等对方响应 loop为-1 循环播放
                    mSoundPool.play(callSound,1,1,0,-1,1);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    //栈顶复用
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
    }

    @OnClick(R.id.bt_accept_ensure)
    public void onAnswer() {
        mSoundPool.autoPause();
        presenter.answerCall();
    }

    @OnClick(R.id.bt_accept_cancel)
    public void onCancel() {
        mSoundPool.autoPause();
        presenter.cancelCall();
    }

    @OnClick(R.id.bt_accept_end)
    public void onEnd() {
        mSoundPool.autoPause();
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
            finish();
        } else {
            ToastUtils.showToast(msg);
        }
    }

    @Override
    public void onEndCall(boolean result, String msg) {
        if (result) {
            finish();
        } else {
            ToastUtils.showToast(msg);
        }
    }

    @Override
    public void onGetCallStatus(int status) {
        if (status == ContentValue.CALL_DISCONNECTED) {
            finish();
        }
    }


    private void initSurfaceView() {
        EMClient.getInstance().callManager().setSurfaceView(localSurface, oppoSurface);
    }
}
