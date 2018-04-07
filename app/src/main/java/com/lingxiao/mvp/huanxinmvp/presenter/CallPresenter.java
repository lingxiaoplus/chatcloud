package com.lingxiao.mvp.huanxinmvp.presenter;

public interface CallPresenter {
    //语音聊天
    void callPhone(String username);
    //视频聊天
    void callVideo(String username);

    //取消
    void endCall();
}
