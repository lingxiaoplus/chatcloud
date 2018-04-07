package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.lingxiao.mvp.huanxinmvp.presenter.AcceptCallPresenter;
import com.lingxiao.mvp.huanxinmvp.view.AcceptCallView;

public class AcceptCallPresenterImpl implements AcceptCallPresenter{
    private AcceptCallView callView;
    public AcceptCallPresenterImpl(AcceptCallView view){
        this.callView = view;
    }
    @Override
    public void answerCall() {
        try {
            EMClient.getInstance().callManager().answerCall();
            callView.onAnswerCall(true,null);
        } catch (EMNoActiveCallException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            callView.onAnswerCall(false,e.getMessage());
        }
    }

    @Override
    public void cancelCall() {
        try {
            EMClient.getInstance().callManager().rejectCall();
            callView.onRejectCall(true,null);
        } catch (EMNoActiveCallException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            callView.onRejectCall(false,e.getMessage());
        }
    }

    @Override
    public void endCall() {
        try {
            EMClient.getInstance().callManager().endCall();
            callView.onEndCall(true,null);
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            callView.onEndCall(false,e.getMessage());
        }
    }

    @Override
    public void callListener() {
        EMClient.getInstance().callManager().addCallStateChangeListener(new EMCallStateChangeListener() {
            @Override
            public void onCallStateChanged(CallState callState, CallError error) {
                switch (callState) {
                    case CONNECTING: // 正在连接对方

                        break;
                    case CONNECTED: // 双方已经建立连接

                        break;

                    case ACCEPTED: // 电话接通成功

                        break;
                    case DISCONNECTED: // 电话断了
                        callView.onGetCallStatus(4);
                        break;
                    case NETWORK_UNSTABLE: //网络不稳定
                        if(error == CallError.ERROR_NO_DATA){
                            //无通话数据
                        }else{
                        }
                        break;
                    case NETWORK_NORMAL: //网络恢复正常

                        break;
                    default:
                        break;
                }

            }
        });
    }
}
