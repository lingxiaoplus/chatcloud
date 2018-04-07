package com.lingxiao.mvp.huanxinmvp.view;

public interface AcceptCallView {
    void onAnswerCall(boolean result,String msg);
    void onRejectCall(boolean result,String msg);
    void onEndCall(boolean result,String msg);

    void onGetCallStatus(int status);
}
