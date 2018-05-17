package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel_Table;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.model.UserModel_Table;
import com.lingxiao.mvp.huanxinmvp.presenter.AddFriendPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.DBUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ThreadUtils;
import com.lingxiao.mvp.huanxinmvp.view.AddFriendView;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingxiao on 17-7-14.
 */

public class AddFriendPresenterImpl implements AddFriendPresenter{
    private AddFriendView addFriendView;
    private List<ContactsModel> modelList;
    private List<String> userList = new ArrayList<>();
    public AddFriendPresenterImpl(AddFriendView addFriendView){
        this.addFriendView = addFriendView;
    }
    @Override
    public void addFriend(final String username) {
        ThreadUtils.runOnSonUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(username,"申请添加好友");
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            addFriendView.onGetAddFriendResult(true,null);
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            addFriendView.onGetAddFriendResult(true,e.getMessage());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void searchFriend(String keyword) {
        final String currentuser = EMClient.getInstance().getCurrentUser();
        //到服务端查询表  后面的参数是表名
        AVQuery<AVUser> avQuery = new AVQuery<>("_User");
        avQuery.whereStartsWith("nickname",keyword)
                .whereNotEqualTo("username",currentuser)
                .findInBackground(new FindCallback<AVUser>() {
                    @Override
                    public void done(List<AVUser> list, AVException e) {
                        if (e == null && list != null && list.size()>0){
                            //从数据库中拿到已经是好友的集合
                            //ArrayList<String> users = DBUtils.initContact(currentuser);
                            modelList = SQLite
                                    .select()
                                    .from(ContactsModel.class)
                                    .where(ContactsModel_Table.contactUserName.isNot(currentuser))
                                    .queryList();
                            for (int i = 0; i < modelList.size(); i++) {
                                userList.add(modelList.get(i).contactUserName);
                            }
                            addFriendView.onQuerySuccess(list,userList,true,null);
                        }else {
                            if (e == null){
                                //查询成功但是没有数据
                                addFriendView.onQuerySuccess(null,null,false,"没有满足的查询条件");
                            }else {
                                //查询失败
                                addFriendView.onQuerySuccess(null,null,false,e.getMessage());
                            }
                        }
                    }
                });
    }
}
