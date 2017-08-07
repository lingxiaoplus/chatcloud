package com.lingxiao.mvp.huanxinmvp.callback;

import com.hyphenate.EMCallBack;
import com.lingxiao.mvp.huanxinmvp.utils.ThreadUtils;

/**
 * 环信提供的回调没有在主线程中进行，需要自己封装一下
 */

public abstract class MyEmCallBack implements EMCallBack{
    @Override
    public void onSuccess() {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                success();
            }
        });
    }

    @Override
    public void onError(final int i, final String s) {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                error(i,s);
            }
        });
    }

    @Override
    public void onProgress(int i, String s) {

    }

    //在主线程中处理环信操作成功的操作
    public abstract void success();

    public abstract void error(int i, String s);
}
