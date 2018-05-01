package com.lingxiao.mvp.huanxinmvp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.lingxiao.mvp.huanxinmvp.event.SkinChangeEvent;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.google.activity.CaptureActivity;
import com.lingxiao.mvp.huanxinmvp.model.VersionModel;
import com.lingxiao.mvp.huanxinmvp.receiver.NetworkReceiver;
import com.lingxiao.mvp.huanxinmvp.utils.HttpUtils;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.SpUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.lingxiao.mvp.huanxinmvp.view.AddFriendActivity;
import com.lingxiao.mvp.huanxinmvp.view.BaseActivity;
import com.lingxiao.mvp.huanxinmvp.view.WebViewActivity;
import com.lingxiao.mvp.huanxinmvp.view.fragment.BaseFragment;
import com.lingxiao.mvp.huanxinmvp.view.fragment.FragmentFactory;
import com.liuguangqiang.cookie.CookieBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private TextView tv_toolbar;
    private FrameLayout fl_main;
    private BottomNavigationBar bm_bar;
    private BadgeItem badgeItem;
    private String[] itemStr = {"消息","通讯录","发现","我"};
    /*private static final String TAG_MESSAGE = "TAG_MESSAGE";
    private static final String TAG_PHONE = "TAG_PHONE";
    private static final String TAG_FIND = "TAG_FIND";
    private static final String TAG_MINE = "TAG_MINE";
    private static final String[] TAG = {TAG_MESSAGE,TAG_PHONE,TAG_FIND,TAG_MINE};*/
    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;
    private Toolbar tb_main;

    private static final int REQUEST_TAKE_PHOTO_PERMISSION = 200;
    private String scanResult;
    private Menu mMenu;
    public boolean isSnackBar; //根据条件判断是否弹出提示

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 2:
                    Log.i("main", "isSnackBar"+isSnackBar);
                    if (isSnackBar){
                        //当前WiFi连接可用
                    }
                    break;
                case 3:
                    if (isSnackBar){
                        //当前移动网络连接可用
                        new CookieBar.Builder(MainActivity.this)
                                .setTitle("提示")
                                .setMessage("当前使用的是移动网络")
                                .setIcon(R.mipmap.ic_net_prompting)
                                .setBackgroundColor(R.color.net_pre)
                                .show();
                    }
                    break;
                case 4:
                    //当前没有网络连接，请确保你已经打开网络
                    new CookieBar.Builder(MainActivity.this)
                            .setTitle("提示")
                            .setMessage("当前没有网络连接，请确保你已经打开网络")
                            .setBackgroundColor(R.color.net_pre)
                            .setIcon(R.mipmap.ic_net_prompting)
                            .show();
                    break;
            }
        }
    };
    private NetworkReceiver mNetworkChangeListener;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initBottomNavigationBar();
        initFirstFragment();

        //注册监听网络变化的广播接受者
        mNetworkChangeListener = new NetworkReceiver(mHandler);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(mNetworkChangeListener,filter);
        isSnackBar = true;

        //检测版本
        HttpUtils.doGet(ContentValue.UPDATEURL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                VersionModel modle = gson.fromJson(result, VersionModel.class);
                SpUtils.putInt(UIUtils.getContext(), ContentValue.VERSION_CODE, modle.getVersionCode());
                SpUtils.putString(UIUtils.getContext(), ContentValue.VERSION_DES, modle.getVersionDes());
                SpUtils.putString(UIUtils.getContext(), ContentValue.DOWNLOAD_URL, modle.getDownloadUrl());
            }
        });
    }

    private void initBottomNavigationBar() {
        BottomNavigationItem item = new BottomNavigationItem(R.drawable.ic_msage,itemStr[0]);

        badgeItem = new BadgeItem();
        upDateBadgeItem();
        //右上角的圆圈文字 显示出来
        item.setBadgeItem(badgeItem);
        bm_bar.addItem(item);
        item = new BottomNavigationItem(R.drawable.ic_phone,itemStr[1]);
        bm_bar.addItem(item);
        item = new BottomNavigationItem(R.drawable.ic_find,itemStr[2]);
        bm_bar.addItem(item);
        item = new BottomNavigationItem(R.drawable.ic_mine,itemStr[3]);
        bm_bar.addItem(item);

        //bottom总体状态的颜色
        bm_bar.setBarBackgroundColor(R.color.white);
        bm_bar.setActiveColor(R.color.colorPrimary);
        //未选中颜色
        bm_bar.setInActiveColor(R.color.colorBottomNav);
        //初始化
        bm_bar.initialise();

        bm_bar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                tv_toolbar.setText(itemStr[position]);  //给toolbar设置字体
                BaseFragment fragment;
                mFragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                fragment = FragmentFactory.createFragment(position);

                if (fragment.isAdded()){
                    //如果fragment已经添加进来了，就show一下
                    transaction.show(fragment).commit();
                    //mFragmentManager.beginTransaction().show(fragment).commit();
                }else {
                    //如果没有添加进来，就添加
                    //initFragment(fragment,TAG[position]);
                    transaction.add(R.id.fl_main,fragment,position+"").commit();
                }
                Log.i("main", "position="+position+"\n"+"menuitem="+mMenu);
                if (position != 1 && mMenu != null){
                    for (int i = 0; i < mMenu.size(); i++) {
                        mMenu.getItem(i).setVisible(false);
                    }
                    Log.i("main", "菜单隐藏执行了 ");
                }else {
                    for (int i = 0; i < mMenu.size(); i++) {
                        mMenu.getItem(i).setVisible(true);
                    }
                }
            }

            @Override
            public void onTabUnselected(int position) {
                //未选中的要隐藏
                mFragmentManager
                        .beginTransaction()
                        .hide(FragmentFactory.createFragment(position))
                        .commit();
            }

            @Override
            public void onTabReselected(int position) {
            }
        });

    }

    private void initView() {
        tv_toolbar = (TextView) findViewById(R.id.tv_toolbar);
        fl_main = (FrameLayout) findViewById(R.id.fl_main);
        bm_bar = (BottomNavigationBar) findViewById(R.id.bm_bar);
        tb_main = (Toolbar) findViewById(R.id.tb_main);
        tb_main.setTitle("");
        setSupportActionBar(tb_main);           //设置了才能添加menu
        /*badgeItem = new BadgeItem()
                .setBorderWidth(2)      //border的边界
                .setBorderColor("#FF0000")      //badge的border的颜色
                .setBackgroundColor("#9ACD32")  //badge背景颜色
                .setGravity(Gravity.RIGHT | Gravity.TOP)       //位置，默认右上角
                .setText("首页")
                .setTextColor("#F0F8FF")
                .setAnimationDuration(2000)
                .setHideOnSelect(true);     //当选中状态时消失，非选中状态显示*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        mMenu = menu;
        //创建之后先将菜单隐藏
        for (int i = 0; i < mMenu.size(); i++) {
            mMenu.getItem(i).setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add:
                StartActivity(AddFriendActivity.class,false);
                break;
            case R.id.menu_photo:
                getPermission();
                break;
            case R.id.menu_suggest:
                FeedbackAgent agent = new FeedbackAgent(getApplicationContext());
                agent.startDefaultThreadActivity();
                break;
        }
        return true;
    }


    public void upDateBadgeItem(){
        //获取所有的未读条目数量
        int unreadMessageCount = EMClient.getInstance().chatManager().getUnreadMessageCount();
        if(unreadMessageCount==0){
            badgeItem.hide(true);
        }else if(unreadMessageCount>99){
            badgeItem.show(true);
            badgeItem.setText(String.valueOf(99));
        }else{
            badgeItem.show(true);
            badgeItem.setText(String.valueOf(unreadMessageCount));
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(List<EMMessage> emMessages){
        upDateBadgeItem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upDateBadgeItem();
    }

    /**
     * 初始化第一个fragment
     */
    private void initFirstFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(fragments!=null && fragments.size()>0){
            //说明有之前缓存的fragment 处理fragment重影的问题
            for(int i = 0;i<fragments.size();i++){
                transaction.remove(fragments.get(i));
            }
            transaction.commit();
        }

        BaseFragment fragment = FragmentFactory.createFragment(0);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_main,fragment,"0").commit();
    }
    //获取摄像头权限
    private void getPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
            showDialog();
        } else {
            //有权限，直接拍照
            takePhoto();
        }
    }
    private void takePhoto() {
        //扫描二维码
        Intent capture = new Intent(this, CaptureActivity.class);
        startActivityForResult(capture,10);
    }
    private void showDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("授权使用相机");
        dialog.setMessage("没有此权限，无法开启这个功能，为了更好的为您服务,请同意开启权限");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_TAKE_PHOTO_PERMISSION);
            }
        });
        dialog.show();
    }
    //显示是否打开链接对话框
    private void showisOpenDialog(final String url){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setMessage("是否打开链接:"+url);
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this,WebViewActivity.class);

                if (url != null){
                    intent.putExtra("url", url);
                }
                startActivity(intent);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_TAKE_PHOTO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                takePhoto();
            } else {
                getPermission();
            }
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) { //RESULT_OK = -1
            Bundle bundle = data.getExtras();
            scanResult = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
            //说明返回数据了，弹窗提示是否打开链接
            showisOpenDialog(scanResult);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        isSnackBar = false;
        Log.i("main", "onPause: isSnackBar = false");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNetworkChangeListener != null){
            unregisterReceiver(mNetworkChangeListener);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeSkin(SkinChangeEvent event) {
        ToastUtils.showToast("换肤了:"+event.color);
        bm_bar.setActiveColor(event.color);
        LogUtils.i("换肤了act： event:"+event.color +
                "bm:"+bm_bar.getActiveColor());
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    /*//监听返回键按两次退出，这里不需要
    private long PressedTime = 0;
    @Override
    public void onBackPressed() {
        long nowTime = System.currentTimeMillis();
        if ((nowTime - PressedTime) > 3000){
            //当前移动网络连接可用
            new CookieBar.Builder(MainActivity.this)
                    .setTitle("提示")
                    .setMessage("再按一次退出应用")
                    .setIcon(R.mipmap.ic_net_prompting)
                    .setLayoutGravity(Gravity.BOTTOM)
                    .setBackgroundColor(R.color.colorPrimary)
                    .show();
            PressedTime = nowTime;
        }else {
            ActivityManager.getAppManager().AppExit(this);
        }
    }*/
}
