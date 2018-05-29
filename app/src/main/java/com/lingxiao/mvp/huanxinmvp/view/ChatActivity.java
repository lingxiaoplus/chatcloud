package com.lingxiao.mvp.huanxinmvp.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.hyphenate.chat.EMMessage;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.adapter.BaseHolder;
import com.lingxiao.mvp.huanxinmvp.adapter.BaseRecyAdapter;
import com.lingxiao.mvp.huanxinmvp.adapter.ChatAdapter;
import com.lingxiao.mvp.huanxinmvp.adapter.PopWindowAdapter;
import com.lingxiao.mvp.huanxinmvp.event.AudioEvent;
import com.lingxiao.mvp.huanxinmvp.event.MessageEvent;
import com.lingxiao.mvp.huanxinmvp.event.PhoneChangedEvent;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel_Table;
import com.lingxiao.mvp.huanxinmvp.model.UserModel;
import com.lingxiao.mvp.huanxinmvp.model.UserModel_Table;
import com.lingxiao.mvp.huanxinmvp.presenter.ChatPresenter;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.ChatPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.MD5Util;
import com.lingxiao.mvp.huanxinmvp.utils.PermissionHelper;
import com.lingxiao.mvp.huanxinmvp.utils.SoundUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sqk.emojirelease.Emoji;
import com.sqk.emojirelease.EmojiUtil;
import com.sqk.emojirelease.FaceFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity implements ChatView,FaceFragment.OnEmojiClickListener{

    @BindView(R.id.tb_chat)
    Toolbar toolbar;
    @BindView(R.id.et_chat)
    EditText editChat;
    @BindView(R.id.bt_face)
    Button btnFace;
    @BindView(R.id.bt_send)
    Button btnSend;
    @BindView(R.id.rv_chat)
    RecyclerView recyChat;
    @BindView(R.id.iv_chat_add)
    ImageView chatAdd;
    @BindView(R.id.fr_emoji)
    FrameLayout fr_emoji;
    private ChatAdapter adapter;
    private ChatPresenter chatPresenter;
    private String username;
    private FaceFragment faceFragment;
    private int faceNum = 0;
    private PopupWindow mPopWindow;
    private ContactsModel mContactModel;
    private int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        chatPresenter = new ChatPresenterImpl(this);
        username = getIntent().getStringExtra("name");
        mContactModel = SQLite
                .select()
                .from(ContactsModel.class)
                .where(ContactsModel_Table.contactUserName.eq(username))
                .querySingle();
        getPermission();
        initView();

        faceFragment = FaceFragment.Instance();

    }

    private void initView() {
        toolbar.setTitle(mContactModel.nickName);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        btnSend.setVisibility(View.INVISIBLE);
        //设置文本框监听
        editChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    //btnSend.setEnabled(false);
                    btnSend.setVisibility(View.INVISIBLE);
                    chatAdd.setVisibility(View.VISIBLE);
                }else {
                    //btnSend.setEnabled(true);
                    btnSend.setVisibility(View.VISIBLE);
                    chatAdd.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0){
                    btnSend.setEnabled(false);
                }else {
                    btnSend.setEnabled(true);
                }
            }
        });

        editChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (faceFragment.isVisible()){
                    FragmentTransaction transation = getSupportFragmentManager().beginTransaction();
                    transation
                            .hide(faceFragment)
                            .commit();
                    btnFace.setBackground(getResources().getDrawable(R.drawable.ic_face_normal));
                    faceNum = 0;
                }
                return false;
            }
        });
        //初始化recycle
        recyChat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(null);
        recyChat.setAdapter(adapter);
        chatPresenter.getChatHistoryMsg(username);

        adapter.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {
            @Override
            public void onTextClick(View view, int position, String msg) {
                ToastUtils.showToast("文字信息："+msg);
            }

            @Override
            public void onPictureClick(View view, int position, String picPath) {
                ToastUtils.showToast("图片");
                Intent intent = new Intent(getApplicationContext(),LocalPicActivity.class);
                intent.putExtra("path",picPath);
                startActivity(intent);
            }

            @Override
            public void onVoiceClick(final View view, final View voiceIcon,int position, String voicePath, int len) {
                ToastUtils.showToast("语音消息");
                view.setEnabled(false);
                voiceIcon.setVisibility(View.VISIBLE);
                SoundUtils.getInstance()
                        .playSoundByMedia(voicePath)
                        .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                mediaPlayer.seekTo(0);
                                LogUtils.i("播放结束");
                                view.setEnabled(true);
                                voiceIcon.setVisibility(View.INVISIBLE);
                            }
                        });

            }
        });
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

    @Override
    public void onGetHistoryMsg(List<EMMessage> messages) {
        adapter.setMessages(messages);
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount()>0){
            recyChat.smoothScrollToPosition(adapter.getItemCount()-1);
        }
    }

    @Override
    public void onUpdateList() {
        if (adapter.getItemCount()>0){
            recyChat.smoothScrollToPosition(adapter.getItemCount()-1);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
        //发送消息通知更新
        EventBus.getDefault().post(new MessageEvent(username,null));
    }

    //订阅消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessageEvent(List<EMMessage> emMessages){
        chatPresenter.getChatHistoryMsg(username);
    }

    //获取摄像头权限
    private void getPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_LOGS)
                != PackageManager.PERMISSION_GRANTED) {
            //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_LOGS},
                    200);
        } else {
            //有权限，直接拍照
        }
    }

    @Override
    public void onEmojiDelete() {
        int index = editChat.getSelectionStart();
        Editable editable = editChat.getEditableText();
        if (index < 0) {
            editable.clear();
        } else {
            editable.delete(index,editChat.getSelectionEnd());
        }
    }

    @Override
    public void onEmojiClick(Emoji emoji) {

            // 得到SpannableString对象,主要用于拆分字符串
            try {
                SpannableStringBuilder builder = EmojiUtil
                        .handlerTextToEmojiSpannable(emoji.getContent()
                                ,getApplicationContext());
                if (emoji != null) {
                    int index = editChat.getSelectionStart();
                    Editable editable = editChat.getEditableText();
                    if (index < 0) {
                        editable.append(builder);
                        //editable.append(emoji.getContent());
                    } else {
                        editable.insert(index, builder);
                    }}
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @OnClick(R.id.bt_send)
    public void sendMessage(){

        String msg = editChat.getText().toString();
        //通过chatpresenter处理发送消息的逻辑
        chatPresenter.sendMessage(username,msg);
        editChat.setText("");
        //Log.i("username", "onClick: ");
    }

    @OnClick(R.id.bt_face)
    public void onClickEmoji(){
        FragmentTransaction transation = getSupportFragmentManager().beginTransaction();
        faceNum++;
        if (faceNum == 1){
            btnFace
                    .setBackground(getResources()
                            .getDrawable(R.drawable.ic_face_pressed));
            if (faceFragment.isAdded()){
                transation
                        .show(faceFragment)
                        .commit();
                //btnFace.setPressed(false);

            }else {
                transation
                        .add(R.id.fr_emoji,faceFragment)
                        .commit();
                //btnFace.setPressed(true);
            }
            toggleSoftInput(editChat,0,false);
        }else {
            btnFace.setBackground(getResources().getDrawable(R.drawable.ic_face_normal));
            transation
                    .hide(faceFragment)
                    .commit();
            faceNum = 0;
        }
    }

    @OnClick(R.id.iv_chat_add)
    public void togglePopupWindow(){
        showPopupWindow();
    }

    private void showPopupWindow(){
        backgroundAlpha(0.5f);
        //设置contentView
        View contentView = LayoutInflater
                .from(ChatActivity.this)
                .inflate(R.layout.chat_pop_window, null);
        //设置各个控件的点击响应
        RecyclerView recyclerView =
                contentView.findViewById(R.id.rv_chat_popwindow);
        GridLayoutManager manager =
                new GridLayoutManager(this,4);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        PopWindowAdapter adapter = new PopWindowAdapter();
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseHolder holder, int position) {
                Intent intent = new Intent(UIUtils.getContext(),CallPhoneActivity.class);;
                if (position == 0 || position == 1){
                    //打电话  视频
                    intent.putExtra("type",position);
                    intent.putExtra("name",username);
                    intent.putExtra("nickname",mContactModel.nickName);
                    intent.putExtra("protrait",mContactModel.protrait);
                    startActivity(intent);
                }else if (position == 2){
                    //发送语音消息
                    boolean result = PermissionHelper.getInstance(UIUtils.getContext()
                    ,ChatActivity.this,"授权使用录音权限",
                            "没有此权限，无法开启这个功能，为了更好的为您服务,请同意开启权限"
                            ,Manifest.permission.RECORD_AUDIO).getPermission();
                    if (result){
                        StartActivity(AudioActivity.class,false);
                    }
                }else if (position == 3){
                    //发送图片消息
                    //单选并剪裁
                    ImageSelectorUtils.openPhoto(ChatActivity.this,
                            REQUEST_CODE);
                }
                mPopWindow.dismiss();
            }
        });
        mPopWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true);
        mPopWindow.setAnimationStyle(R.style.contextMenuAnim);
        mPopWindow.setContentView(contentView);
        //显示PopupWindow
        View rootview = LayoutInflater
                .from(ChatActivity.this)
                .inflate(R.layout.activity_chat, null);
        mPopWindow.setFocusable(true);
        //点击外部消失
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        //mPopWindow.showAsDropDown(rootview);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1);
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }
    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAudioEvent(AudioEvent event){
        chatPresenter.sendVoiceMessage(username,event.audioPath, (int) event.time);
        adapter.notifyDataSetChanged();
        ToastUtils.showToast("语音发送成功！");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            //获取选择器返回的数据
            ArrayList<String> images = data.getStringArrayListExtra(
                    ImageSelectorUtils.SELECT_RESULT);
            chatPresenter.sendPicMessage(username,false,images.get(0));
        }
    }

    /**
     * 监听返回按钮，隐藏表情和popwindow
     * 先监听popwindow是否关闭，然后再考虑表情
     */
    @Override
    public void onBackPressed() {
        //可能pop还没有初始化
        if (null != mPopWindow && mPopWindow.isShowing()){
            mPopWindow.dismiss();
        }else if (faceFragment.isVisible()){
            FragmentTransaction transation = getSupportFragmentManager().beginTransaction();
            transation
                    .hide(faceFragment)
                    .commit();
            btnFace.setBackground(getResources().getDrawable(R.drawable.ic_face_normal));
            faceNum = 0;
        }else {
            super.onBackPressed();
        }
    }
}
