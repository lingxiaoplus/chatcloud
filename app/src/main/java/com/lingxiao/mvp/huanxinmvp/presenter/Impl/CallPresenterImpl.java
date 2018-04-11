package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.hyphenate.media.EMLocalSurfaceView;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.presenter.CallPresenter;
import com.lingxiao.mvp.huanxinmvp.view.CallView;

public class CallPresenterImpl implements CallPresenter{
    private CallView callView;
    public CallPresenterImpl(CallView callView){
        this.callView = callView;
    }
    @Override
    public void callPhone(String username) {
        /**
         * 拨打语音通话
         * @param to
         * @throws EMServiceNotReadyException
         */
        try {//单参数
            EMClient.getInstance().callManager().makeVoiceCall(username);
        } catch (EMServiceNotReadyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            callView.getCallStatus(ContentValue.CALL_ERROR,e.getMessage());
        }
    }

    @Override
    public void callVideo(String username) {
        try {//单参数
            EMClient.getInstance().callManager().makeVideoCall(username);
        } catch (EMServiceNotReadyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            callView.getCallStatus(ContentValue.CALL_ERROR,e.getMessage());
        }
    }

    @Override
    public void endCall() {
        try {
            EMClient.getInstance().callManager().endCall();
            callView.endCall(true,null);
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            callView.endCall(false,e.getMessage());
        }
    }


    private void getCallStatus(){
        EMClient.getInstance().callManager().addCallStateChangeListener(new EMCallStateChangeListener() {
            @Override
            public void onCallStateChanged(CallState callState, CallError error) {
                switch (callState) {
                    case CONNECTING: // 正在连接对方
                        callView.getCallStatus(ContentValue.CALL_CONNECTED,null);
                        break;
                    case CONNECTED: // 双方已经建立连接
                        break;
                    case ACCEPTED: // 电话接通成功
                        callView.getCallStatus(ContentValue.CALL_ACCEPTED,null);
                        break;
                    case DISCONNECTED: // 电话断了
                        callView.getCallStatus(ContentValue.CALL_DISCONNECTED,null);
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
