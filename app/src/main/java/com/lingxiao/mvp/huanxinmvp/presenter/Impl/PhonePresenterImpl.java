package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lingxiao.mvp.huanxinmvp.presenter.PhonePresenter;
import com.lingxiao.mvp.huanxinmvp.utils.DBUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ThreadUtils;
import com.lingxiao.mvp.huanxinmvp.view.PhoneView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lingxiao on 17-7-13.
 */

public class PhonePresenterImpl implements PhonePresenter {
    private PhoneView phoneView;

    public PhonePresenterImpl(PhoneView phoneView){
        this.phoneView = phoneView;
    }
    @Override
    public void initContact() {
        //初始化联系人
        //1先到数据库中获取
        ArrayList<String> list = DBUtils.initContact(EMClient.getInstance().getCurrentUser());
        phoneView.onInitContact(list);
        //2联网获取数据
        getContactListFromServer();
    }

    @Override
    public void updateContact() {
        getContactListFromServer();
    }

    @Override
    public void deleteContact(final String username) {
        ThreadUtils.runOnSonUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //此方法没有开启线程
                    EMClient.getInstance().contactManager().deleteContact(username);
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            //在主线程中进行view层的回调
                            phoneView.onDeleteContact(true,null);
                        }
                    });
                } catch (final HyphenateException e) {
                    //说明删除失败
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            phoneView.onDeleteContact(false,e.getMessage());
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
    }

    //联网获取数据
    public void getContactListFromServer(){
        ThreadUtils.runOnSonUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取所有好友
                    final ArrayList<String> contactList =
                            (ArrayList<String>) EMClient.getInstance()
                                    .contactManager()
                                    .getAllContactsFromServer();
                    Collections.sort(contactList, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    //获取数据后，保存到数据库
                    DBUtils.upDateContactFromServer(EMClient.getInstance().getCurrentUser(),contactList);
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            phoneView.onUpdateContact(contactList,true,null);
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            phoneView.onUpdateContact(null,false,null);
                        }
                    });
                }
            }
        });
    }
}
