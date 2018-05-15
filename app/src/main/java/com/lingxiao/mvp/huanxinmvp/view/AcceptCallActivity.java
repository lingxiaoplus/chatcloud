package com.lingxiao.mvp.huanxinmvp.view;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_call);
        ButterKnife.bind(this);
        presenter = new AcceptCallPresenterImpl(this);
        intent = getIntent();
        initData();
        presenter.callListener();
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
        presenter.answerCall();
    }

    @OnClick(R.id.bt_accept_cancel)
    public void onCancel() {
        presenter.cancelCall();
    }

    @OnClick(R.id.bt_accept_end)
    public void onEnd() {
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
