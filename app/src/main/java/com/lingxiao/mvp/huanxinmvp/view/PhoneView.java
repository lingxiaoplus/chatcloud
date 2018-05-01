package com.lingxiao.mvp.huanxinmvp.view;

import com.lingxiao.mvp.huanxinmvp.model.ContactsModel;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingxiao on 17-7-13.
 */

public interface PhoneView {
    void onInitContact(List<ContactsModel> contactList);
    void onUpdateContact(List<ContactsModel> contactList,
                         boolean isUpadteSuccess,
                         String errorMsg);
    void onDeleteContact(boolean isDeleteSuccess,
                         String errorMsg);
}
