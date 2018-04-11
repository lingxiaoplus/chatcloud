package com.lingxiao.mvp.huanxinmvp.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMMessage;
import com.lingxiao.mvp.huanxinmvp.MainActivity;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.adapter.BaseRecyAdapter;
import com.lingxiao.mvp.huanxinmvp.adapter.ChatAdapter;
import com.lingxiao.mvp.huanxinmvp.adapter.PopWindowAdapter;
import com.lingxiao.mvp.huanxinmvp.presenter.ChatPresenter;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.ChatPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.sqk.emojirelease.Emoji;
import com.sqk.emojirelease.EmojiUtil;
import com.sqk.emojirelease.FaceFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity implements ChatView,FaceFragment.OnEmojiClickListener{

    @BindView(R.id.tb_chat)
    Toolbar toolbar;
    @BindView(R.id.tv_char_name)
    TextView chatName;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        chatPresenter = new ChatPresenterImpl(this);
        username = getIntent().getStringExtra("name");
        getPermission();
        initView();

        faceFragment = FaceFragment.Instance();
    }

    private void initView() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        chatName.setText(username);
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

        //初始化recycle
        recyChat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(null);
        recyChat.setAdapter(adapter);
        chatPresenter.getChatHistoryMsg(username);
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
    }

    //订阅消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    void onGetMessageEvent(List<EMMessage> emMessages){
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
            public void onItemClick(View View, int position) {
                Intent intent = null;
                if (position == 0){
                    //打电话
                    intent = new Intent(UIUtils.getContext(),CallPhoneActivity.class);
                    intent.putExtra("type",0);
                    intent.putExtra("name",username);
                    startActivity(intent);
                }else if (position == 1){
                    //视频
                    intent = new Intent(UIUtils.getContext(),CallPhoneActivity.class);
                    intent.putExtra("type",1);
                    intent.putExtra("name",username);
                    startActivity(intent);
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
        //mPopWindow.showAsDropDown(rootview);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }
}
