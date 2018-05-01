package com.lingxiao.mvp.huanxinmvp.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;

import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.event.AudioEvent;
import com.lingxiao.mvp.huanxinmvp.widget.AudioButton;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AudioActivity extends BaseActivity {

    @BindView(R.id.button2)
    AudioButton audioBtn;
    @BindView(R.id.audio_root)
    ConstraintLayout audioRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        ButterKnife.bind(this);

        audioBtn.setAudioFinishRecorderListener(new AudioButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                EventBus.getDefault().post(new AudioEvent(seconds,filePath));
            }
        });
    }

    @OnClick(R.id.audio_root)
    public void exitAudio(){
        finish();
    }
}
