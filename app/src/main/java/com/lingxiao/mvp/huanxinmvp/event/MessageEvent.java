package com.lingxiao.mvp.huanxinmvp.event;

public class MessageEvent {
    public String text;
    public String username;

    public MessageEvent(String username, String text){
        this.username = username;
        this.text = text;
    }
}
