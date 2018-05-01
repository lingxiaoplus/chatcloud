package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel_Table;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.model.UserModel_Table;
import com.lingxiao.mvp.huanxinmvp.presenter.PhonePresenter;
import com.lingxiao.mvp.huanxinmvp.utils.ChineseCharToEnUtil;
import com.lingxiao.mvp.huanxinmvp.utils.DBUtils;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.SpUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ThreadUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.lingxiao.mvp.huanxinmvp.view.PhoneView;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lingxiao on 17-7-13.
 */

public class PhonePresenterImpl implements PhonePresenter {
    private PhoneView phoneView;
    private List<ContactsModel> modelList = new ArrayList<>();
    private String objId;
    private List<String> mHuanList;

    public PhonePresenterImpl(PhoneView phoneView) {
        this.phoneView = phoneView;
    }

    @Override
    public void initContact() {
        //初始化联系人
        //1先到数据库中获取
        objId = SpUtils.getString(UIUtils.getContext(),
                ContentValue.OBJECTID, "");
        modelList = SQLite
                .select()
                .from(ContactsModel.class)
                .where(ContactsModel_Table.owerObjectId.eq(objId))
                .queryList();

        if (null != modelList && modelList.size() >0){
            //排序
            modelList = ChineseCharToEnUtil.listToSortByName(modelList);
        }
        phoneView.onInitContact(modelList);
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
                            phoneView.onDeleteContact(true, null);
                        }
                    });
                } catch (final HyphenateException e) {
                    //说明删除失败
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            phoneView.onDeleteContact(false, e.getMessage());
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
    }

    //联网获取数据
    public void getContactListFromServer() {
        //更新之前先清空之前保存下来的数据
        SQLite.delete()
                .from(ContactsModel.class)
                .where(ContactsModel_Table.owerObjectId.eq(objId)).execute();
        modelList.clear();
        ThreadUtils.runOnSonUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mHuanList = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    quaryFriend();
                } catch (Exception e) {
                    e.printStackTrace();
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            phoneView.onUpdateContact(null, false, null);
                        }
                    });
                }
            }
        });
    }

    /**
     * 通过username查询朋友列表
     */
    private void quaryFriend() {
        final AVQuery<AVUser> avQuery = new AVQuery<>("_User");
        //这里只需要查询第一个就行了，因为不可能存在重复的
        avQuery
                .whereNotEqualTo("objectId", objId)
                .findInBackground(new FindCallback<AVUser>() {
                    @Override
                    public void done(List<AVUser> list, AVException e) {
                        LogUtils.i("环信的："+mHuanList.size()+"leancloud的："+list.size());
                        for (int i = 0; i < list.size(); i++) {
                            AVUser user = list.get(i);
                            LogUtils.i("leancloud的用户名："+user.getUsername());
                            for (int j = 0; j < mHuanList.size(); j++) {
                                LogUtils.i("环信的用户名："+mHuanList.get(j));
                                if (user.getUsername().equals(mHuanList.get(j))) {
                                    ContactsModel model = new ContactsModel();
                                    model.setOwerObjectId(objId);
                                    model.setContactUserName(user.getUsername());
                                    model.setContactId(user.getObjectId());
                                    model.setDesc(user.getString(ContentValue.DESCRIPTION));
                                    model.setProtrait(user.getString(ContentValue.PROTRAIT));
                                    model.setNickName(user.getString(ContentValue.NICKNAME));
                                    model.setAge(user.getInt(ContentValue.AGE));
                                    model.setPhone(user.getMobilePhoneNumber());
                                    model.save();
                                    modelList.add(model);
                                }
                            }
                        }
                        if (modelList.size() > 0) {
                            //排序
                            modelList = ChineseCharToEnUtil.listToSortByName(modelList);
                        }
                        ThreadUtils.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                phoneView.onUpdateContact(modelList, true, null);
                            }
                        });
                    }
                });
    }
}
