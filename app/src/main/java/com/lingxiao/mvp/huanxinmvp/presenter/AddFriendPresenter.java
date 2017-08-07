package com.lingxiao.mvp.huanxinmvp.presenter;

import com.lingxiao.mvp.huanxinmvp.view.AddFriendView;

/**
 * Created by lingxiao on 17-7-14.
 */

public interface AddFriendPresenter {
    /**
     *添加好友
     */
    void addFriend(String username);
    /**
     *通过关键词在服务端搜索好友
     */
    void searchFriend(String keyword);
}
