package com.lingxiao.mvp.huanxinmvp.view;

import com.lingxiao.mvp.huanxinmvp.model.UserModel;

/**
 * Created by lingxiao on 17-7-16.
 */

public interface MineView {
    void onLogOut(boolean isSuccess,String errormsg);
    void onGetUserInfo(UserModel model);
}
