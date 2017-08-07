package com.lingxiao.mvp.huanxinmvp.event;

/**
 * Created by lingxiao on 17-7-14.
 */

public class PhoneChangedEvent {
    //联系人发生改变的名称
    public String username;
    //联系人是增加还是删除
    public boolean isDelete;

    public PhoneChangedEvent(String username,boolean isDelete){
        this.username = username;
        this.isDelete = isDelete;
    }
}
