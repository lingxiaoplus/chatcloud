package com.lingxiao.mvp.huanxinmvp.view;

import com.lingxiao.mvp.huanxinmvp.model.UserModel;

/**
 * Created by lingxiao on 17-7-16.
 */

public interface UserCardView {
    void onLogOut(boolean isSuccess,String errormsg);
    void onGetUserInfo(UserModel model);
    void onChangeUserInfo(int type,boolean result);
}
