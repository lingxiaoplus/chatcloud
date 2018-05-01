package com.lingxiao.mvp.huanxinmvp.event;

public class AudioEvent {
    public float time;
    public String audioPath;
    public AudioEvent(float time,String audioPath){
        this.time = time;
        this.audioPath =audioPath;
    }
}
