package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.hyphenate.chat.EMClient;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.callback.MyEmCallBack;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.presenter.UserCardPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.QiNiuHelper.Auth;
import com.lingxiao.mvp.huanxinmvp.utils.QiNiuSdkHelper;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.lingxiao.mvp.huanxinmvp.view.UserCardView;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * Created by lingxiao on 17-7-16.
 */

public class UserCardPresenterImpl implements UserCardPresenter,QiNiuSdkHelper.uploadListener {
    private UserCardView mineView;
    public UserCardPresenterImpl(UserCardView mineView){
        this.mineView = mineView;
        QiNiuSdkHelper.getInstance().setUploadListener(this);
    }
    @Override
    public void logOut() {
        EMClient.getInstance().logout(true, new MyEmCallBack() {
            @Override
            public void success() {
                mineView.onLogOut(true,null);
            }

            @Override
            public void error(int i, String s) {
                mineView.onLogOut(false,s);
            }
        });
    }

    @Override
    public void getUserInfo() {
        UserModel model = SQLite
                .select()
                .from(UserModel.class)
                .querySingle();
        mineView.onGetUserInfo(model);
    }

    @Override
    public void changeUserInfo(final int type, final String path, String name, final int integers) {
        final UserModel model = SQLite
                .select()
                .from(UserModel.class)
                .querySingle();
        if (type == ContentValue.CHANGE_PROTRAIT){
            QiNiuSdkHelper.getInstance().upload(path,name,getToken(name));
        }else if (type == ContentValue.CHANGE_AGE){
            AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null){
                        AVUser.getCurrentUser().put(ContentValue.AGE, integers);
                        AVUser.getCurrentUser().saveInBackground();
                        model.setAge(integers);
                        model.save();
                        mineView.onChangeUserInfo(type,true);
                    }else {
                        mineView.onChangeUserInfo(type,false);
                    }
                }
            });
        }else if (type == ContentValue.CHANGE_SEX){
            AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null){
                        AVUser.getCurrentUser().put(ContentValue.SEX, integers);
                        AVUser.getCurrentUser().saveInBackground();
                        model.setSex(integers);
                        model.save();
                        mineView.onChangeUserInfo(type,true);
                    }else {
                        mineView.onChangeUserInfo(type,false);
                    }
                }
            });
        }else if (type == ContentValue.CHANGE_PHONE){
            AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null){
                        AVUser.getCurrentUser().setMobilePhoneNumber(path);
                        AVUser.getCurrentUser().saveInBackground();
                        model.setPhone(path);
                        model.save();
                        mineView.onChangeUserInfo(type,true);
                    }else {
                        mineView.onChangeUserInfo(type,false);
                    }
                }
            });
        }else if (type == ContentValue.CHANGE_NAME){
            AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null){
                        AVUser.getCurrentUser().setMobilePhoneNumber(path);
                        AVUser.getCurrentUser().saveInBackground();
                        model.setNickname(path);
                        model.save();
                        mineView.onChangeUserInfo(type,true);
                    }else {
                        mineView.onChangeUserInfo(type,false);
                    }
                }
            });
        }else if (type == ContentValue.CHANGE_DESC){
            AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null){
                        AVUser.getCurrentUser().put(ContentValue.NICKNAME,path);
                        AVUser.getCurrentUser().saveInBackground();
                        model.setDesc(path);
                        model.save();
                        mineView.onChangeUserInfo(type,true);
                    }else {
                        mineView.onChangeUserInfo(type,false);
                    }
                }
            });
        }
    }

    private String getToken(String key){
        //这句就是生成token
        String token = Auth.create(UIUtils
                .getContext()
                .getResources()
                .getString(R.string.AccessKey), UIUtils
                .getContext()
                .getResources()
                .getString(R.string.SecretKey))
                .uploadToken(ContentValue.BUCKET,key);
        return token;
    }

    @Override
    public void onSuccess(final String url) {
        LogUtils.i("成功的回调"+url);
        final UserModel model = SQLite
                .select()
                .from(UserModel.class)
                .querySingle();
        AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null){
                    AVUser.getCurrentUser().put(ContentValue.PROTRAIT, url);
                    AVUser.getCurrentUser().saveInBackground();
                    model.setProtrait(url);
                    boolean re = model.save();
                    LogUtils.i("保存到数据库状态："+re);
                    mineView.onChangeUserInfo(ContentValue.CHANGE_PROTRAIT,true);
                }else {
                    mineView.onChangeUserInfo(ContentValue.CHANGE_PROTRAIT,false);
                }
            }
        });
    }

    @Override
    public void onFaild(String msg) {
        mineView.onChangeUserInfo(ContentValue.CHANGE_PROTRAIT,false);
    }
}
