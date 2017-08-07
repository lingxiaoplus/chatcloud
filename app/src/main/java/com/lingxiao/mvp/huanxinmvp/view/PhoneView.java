package com.lingxiao.mvp.huanxinmvp.view;

import java.util.ArrayList;

/**
 * Created by lingxiao on 17-7-13.
 */

public interface PhoneView {
    void onInitContact(ArrayList<String> contactList);
    void onUpdateContact(ArrayList<String> contactList,
                         boolean isUpadteSuccess,
                         String errorMsg);
    void onDeleteContact(boolean isDeleteSuccess,
                         String errorMsg);
}
