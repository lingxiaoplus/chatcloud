package com.lingxiao.mvp.huanxinmvp.model;

import com.lingxiao.mvp.huanxinmvp.db.database.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = AppDataBase.class)
public class ContactsModel extends BaseModel{
    @PrimaryKey(autoincrement = true)
    public int id;
    //自己的id
    @Column
    public String owerObjectId;
    //好友id
    @Column
    public String contactId;

    //自己的name
    @Column
    public String owerUserName;
    //好友name
    @Column
    public String contactUserName;

    @Column
    public String nickName;
    @Column
    public int sex;
    @Column
    public String protrait;
    @Column
    public String desc;
    @Column
    public String phone;
    @Column
    public int age;

    @Column
    public long createAt;
    @Column
    public long updateAt;

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

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

    public String getOwerUserName() {
        return owerUserName;
    }

    public void setOwerUserName(String owerUserName) {
        this.owerUserName = owerUserName;
    }

    public String getContactUserName() {
        return contactUserName;
    }

    public void setContactUserName(String contactUserName) {
        this.contactUserName = contactUserName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getProtrait() {
        return protrait;
    }

    public void setProtrait(String protrait) {
        this.protrait = protrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
