package com.lingxiao.mvp.huanxinmvp.model;

import com.lingxiao.mvp.huanxinmvp.db.database.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

@Table(database = AppDataBase.class)
public class MessageModel {
    @PrimaryKey(autoincrement = true)
    public int id;
    //自己的id
    @Column
    public String owerObjectId;
    //好友id
    @Column
    public String contactId;

    //会话创建时间
    @Column
    public long createTime;
    //会话更新时间
    @Column
    public long updateTime;
    //消息类型
    @Column
    public int type;
    //消息内容
    @Column
    public String content;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwerObjectId() {
        return owerObjectId;
    }

    public void setOwerObjectId(String owerObjectId) {
        this.owerObjectId = owerObjectId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
