package com.lingxiao.mvp.huanxinmvp.view;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.global.ActivityManager;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.model.UserModel_Table;
import com.lingxiao.mvp.huanxinmvp.utils.SpUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.lingxiao.mvp.huanxinmvp.widget.SettingCardView;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import skin.support.SkinCompatManager;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.toolbar_setting)
    Toolbar toolbarSetting;
    @BindView(R.id.set_upadte)
    SettingCardView setUpadte;
    @BindView(R.id.set_friend)
    SettingCardView setFriend;
    @BindView(R.id.set_clearHistory)
    SettingCardView setClearHistory;
    @BindView(R.id.set_about)
    SettingCardView setAbout;
    @BindView(R.id.set_message_remind)
    SettingCardView setMessageRemind;
    @BindView(R.id.set_voice)
    SettingCardView setVoice;
    @BindView(R.id.set_shake)
    SettingCardView setShake;
    @BindView(R.id.set_logout)
    net.qiujuer.genius.ui.widget.Button setLogout;
    private List<UserModel> modelList = new ArrayList<>();
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initToolbar();

        String versionName = getVersionName();
        setAbout.setMessage("版本号：V"+versionName);
        initSwitch();
    }

    private void initSwitch() {
        boolean update = SpUtils
                .getBoolean(UIUtils.getContext(),ContentValue.UPDATE,true);
        boolean friend = SpUtils
                .getBoolean(UIUtils.getContext(),ContentValue.FRIEND,true);
        boolean remind = SpUtils
                .getBoolean(UIUtils.getContext(),ContentValue.MESSAGE_REMIND,true);
        boolean voice = SpUtils
                .getBoolean(UIUtils.getContext(),ContentValue.MESSAGE_VOICE,true);
        boolean shake = SpUtils
                .getBoolean(UIUtils.getContext(),ContentValue.MESSAGE_SNAKE,true);
        setUpadte.setChecked(update);
        setFriend.setChecked(friend);
        setMessageRemind.setChecked(remind);
        setVoice.setChecked(voice);
        setShake.setChecked(shake);

        setVoice.setVisable(remind);
        setShake.setVisable(remind);
    }

    private void initToolbar() {
        toolbarSetting.setTitle("设置");
        setSupportActionBar(toolbarSetting);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.set_upadte)
    public void setUpdate(View v){
        SkinCompatManager.getInstance().loadSkin("skin_night.skin", SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
        boolean ischeked = setUpadte.getChecked();
        setUpadte.setChecked(!ischeked);
        SpUtils.putBoolean(UIUtils.getContext(),ContentValue.UPDATE,!ischeked);
    }

    @OnClick(R.id.set_friend)
    public void setFriend(){
        boolean ischeked = setFriend.getChecked();
        setFriend.setChecked(!ischeked);
        SpUtils.putBoolean(UIUtils.getContext(),ContentValue.FRIEND,!ischeked);
    }
    @OnClick(R.id.set_message_remind)
    public void setRemind(){
        boolean ischeked = setMessageRemind.getChecked();
        setMessageRemind.setChecked(!ischeked);
        SpUtils.putBoolean(UIUtils.getContext(),ContentValue.MESSAGE_REMIND,!ischeked);
        setVoice.setVisable(!ischeked);
        setShake.setVisable(!ischeked);
    }

    @OnClick(R.id.set_voice)
    public void setVoice(){
        boolean ischeked = setVoice.getChecked();
        setVoice.setChecked(!ischeked);
        SpUtils.putBoolean(UIUtils.getContext(),ContentValue.MESSAGE_VOICE,!ischeked);
    }

    @OnClick(R.id.set_shake)
    public void setShake(){
        boolean ischeked = setShake.getChecked();
        setShake.setChecked(!ischeked);
        SpUtils.putBoolean(UIUtils.getContext(),ContentValue.MESSAGE_SNAKE,!ischeked);
    }

    @OnClick(R.id.set_about)
    public void getVersion(){
        if (!checkUpdate()){
            ToastUtils.showToast("已经是最新版本");
        }
    }

    @OnClick(R.id.set_logout)
    public void logout(){
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                //退出登录，删除表  同步方法
                SQLite.delete()
                        .from(UserModel.class).execute();
                ActivityManager.getAppManager().AppExit(getApplicationContext());
            }

            @Override
            public void onError(int code, String error) {
                ToastUtils.showToast(error);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    @OnClick(R.id.set_clearHistory)
    public void removeMessage(){
        showDia();
    }

    private void showDia() {
        if (null != builder){
            return;
        }
        builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定要删除所有聊天记录吗？此操作不可逆");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteMsg();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void deleteMsg(){
        try {
            //删除和某个user会话，如果需要保留聊天记录，传false
            String objId = SpUtils.getString(UIUtils.getContext(),
                    ContentValue.OBJECTID,"");
            modelList = SQLite
                    .select()
                    .from(UserModel.class)
                    .where(UserModel_Table.objId.isNot(objId))
                    .queryList();
            for (int j = 0; j < modelList.size(); j++) {
                EMClient.getInstance()
                        .chatManager()
                        .deleteConversation(modelList.get(j).username, true);
            }
            ToastUtils.showToast("清除成功");
        }catch (Exception e){
            ToastUtils.showToast("清除失败");
        }
    }
}
