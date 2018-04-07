package com.lingxiao.mvp.huanxinmvp.view;

public interface CallView {
    void getCallStatus(int status,String msg);
    void endCall(boolean result,String msg);
}
