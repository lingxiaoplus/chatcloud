package com.lingxiao.mvp.huanxinmvp.view;

import com.avos.avoscloud.AVUser;

import java.util.List;

/**
 * Created by lingxiao on 17-7-14.
 */

public interface AddFriendView {
    void onQuerySuccess(List<AVUser> avUsers,List<String> users,
                        boolean isSuccess,String errormsg);
    void onGetAddFriendResult(boolean result,String messagre);
}
