package com.lingxiao.mvp.huanxinmvp.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.presenter.CallPresenter;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.CallPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.utils.GlideHelper;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ThreadUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;

import java.text.Collator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class CallPhoneActivity extends BaseActivity implements CallView {

    @BindView(R.id.img_call_head)
    CircleImageView imgCallHead;
    @BindView(R.id.tv_call_name)
    TextView tvCallName;
    @BindView(R.id.bt_call_end)
    FloatingActionButton btCallend;
    @BindView(R.id.opposite_surfaceview)
    RelativeLayout opSurfaceview;
    @BindView(R.id.local_surfaceview)
    RelativeLayout localSurfaceview;

    private CallPresenter presenter;
    private long callTime;
    private EMOppositeSurfaceView oppositeSurface;
    private EMLocalSurfaceView localSurface;
    private RelativeLayout.LayoutParams mParams;
    private String mProtrait;
    private SoundPool mSoundPool;
    private int callSound;


    /**
     * 传感器
     */
    private SensorManager sm;
    //设备的电源控制器
    //private PowerManager mPowerManager;
    //唤醒锁
    //private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_phone);
        ButterKnife.bind(this);
        presenter = new CallPresenterImpl(this);
        callTime = System.currentTimeMillis();

        initSoundPool();
        Intent intent = getIntent();
        initData(intent);
        //initPowerManager();
    }

    private void initPowerManager() {
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //获取系统服务POWER_SERVICE，返回一个PowerManager对象
        //mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象
        /**
         * 参数一是PowerManager提供的的四种唤醒锁
         * PARTIAL_WAKE_LOCK
         * SCREEN_DIM_WAKE_LOCK  （level 17  deprecated）
         * SCREEN_BRIGHT_WAKE_LOCK  （level 17  deprecated）
         * FULL_WAKE_LOCK  （level 17  deprecated）
         */
        //mWakeLock = this.mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWake");
    }

    /**
     * 初始化声音
     */
    private void initSoundPool() {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_RING,0);
        callSound = mSoundPool.load(this,R.raw.ipcall_phonering,1);
    }

    private void initData(Intent intent) {
        int type = intent.getIntExtra("type", 0);
        String name = intent.getStringExtra("name");
        mProtrait = intent.getStringExtra("protrait");
        String nickName = intent.getStringExtra("nickname");
        if (type == 0) {
            presenter.callPhone(name);
        } else if (type == 1) {
            presenter.callVideo(name);
            initSurfaceView();
            tvCallName.setTextColor(Color.WHITE);
        }
        if (null != mProtrait){
            GlideHelper.loadImageView(mProtrait,imgCallHead);
        }
        if (null != nickName){
            tvCallName.setText(nickName);
        }
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                //等对方响应 loop为-1 循环播放
                //mSoundPool.play(callSound,1,1,0,-1,1);
            }
        });

    }

    @OnClick(R.id.bt_call_end)
    public void cancelCall() {
        presenter.endCall();
    }

    @Override
    public void getCallStatus(final int status, final String msg) {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (null == msg) {
                    if (status == ContentValue.CALL_CONNECTED) {

                    } else if (status == ContentValue.CALL_ACCEPTED) {
                        //接听了
                        //opSurfaceview.setVisibility(View.VISIBLE);
                        //localSurface.release();
                        ToastUtils.showToast("接通电话了");
                    } else if (status == ContentValue.CALL_DISCONNECTED) {
                        long time = System.currentTimeMillis();
                        if (time - callTime < 1000) {
                            //说明对方不在线
                            ToastUtils.showToast("对方可能不在线");
                        }else {
                            finish();
                        }
                    }
                } else {
                    ToastUtils.showToast("对方可能不在线" );
                }
            }
        });

    }

    @Override
    public void endCall(boolean result, String msg) {
        if (result) {

        } else {
            ToastUtils.showToast(msg);
        }
        finish();
    }

    private void initSurfaceView(){
        imgCallHead.setVisibility(View.INVISIBLE);
        mParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        // 初始化显示远端画面控件
        oppositeSurface = new EMOppositeSurfaceView(this);
        oppositeSurface.setLayoutParams(mParams);
        opSurfaceview.addView(oppositeSurface);

        //初始化本地视频画面
        localSurface = new EMLocalSurfaceView(this);
        //localSurface.setLayoutParams(mParams);
        localSurfaceview.addView(localSurface);

        EMClient.getInstance().callManager().setSurfaceView(localSurface,oppositeSurface);
    }

    /**
     * 感应器监听
     */
    /*private SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float[] its = event.values;
            //ToastUtils.showToast("距离："+its[0]+"cm");
            if (its != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                LogUtils.i("锁屏");
                if (its[0] <= 3.0) {
                    if (mWakeLock.isHeld()) {
                        LogUtils.i("锁屏1");
                        return;
                    } else {
                        mWakeLock.acquire();
                        LogUtils.i("锁屏2");
                    }

                } else {
                    if (mWakeLock.isHeld()) {
                        return;
                    } else {
                        mWakeLock.setReferenceCounted(false);
                        mWakeLock.release();
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };*/
    //注册黑屏亮屏监听
    @Override
    protected void onResume() {
        super.onResume();
        /*Sensor s = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (s != null) {
            sm.registerListener(mSensorListener, s, SensorManager.SENSOR_DELAY_NORMAL);
        }*/
    }

    /**
     * 取消注册
     */
    /*public synchronized void stopSensor() {
        if (sm != null) {
            mWakeLock.release();//释放电源锁
            sm.unregisterListener(mSensorListener);//注销传感器监听
        }
    }*/
    @Override
    protected void onDestroy() {
        //stopSensor();
        super.onDestroy();
        if (mSoundPool !=null){
            mSoundPool.autoPause();
            mSoundPool.release();
            mSoundPool = null;
        }
    }
}
