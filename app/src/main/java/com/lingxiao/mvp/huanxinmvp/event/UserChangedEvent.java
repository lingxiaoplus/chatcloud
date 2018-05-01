package com.lingxiao.mvp.huanxinmvp.event;

public class UserChangedEvent {
    public String nickname;
    public String protrait;
    public UserChangedEvent(String name,String protrait){
        this.nickname = name;
        this.protrait = protrait;
    }
}
