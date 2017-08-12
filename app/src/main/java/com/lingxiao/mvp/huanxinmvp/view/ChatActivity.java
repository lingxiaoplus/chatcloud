package com.lingxiao.mvp.huanxinmvp.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.lingxiao.mvp.huanxinmvp.MainActivity;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.adapter.ChatAdapter;
import com.lingxiao.mvp.huanxinmvp.presenter.ChatPresenter;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.ChatPresenterImpl;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class ChatActivity extends BaseActivity implements ChatView,View.OnClickListener{

    private Toolbar toolbar;
    private TextView chatName;
    private String username;
    private EditText editChat;
    private Button btnFace,btnSend;
    private ChatPresenter chatPresenter;
    private RecyclerView recyChat;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatPresenter = new ChatPresenterImpl(this);
        username = getIntent().getStringExtra("name");
        getPermission();
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.tb_chat);
        chatName = (TextView) findViewById(R.id.tv_char_name);
        editChat = (EditText) findViewById(R.id.et_chat);
        btnFace = (Button) findViewById(R.id.bt_face);
        btnSend = (Button) findViewById(R.id.bt_send);
        recyChat = (RecyclerView) findViewById(R.id.rv_chat);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        chatName.setText(username);
        btnSend.setEnabled(false);
        //设置文本框监听
        editChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    btnSend.setEnabled(false);
                }else {
                    btnSend.setEnabled(true);
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
        btnSend.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        String msg = editChat.getText().toString();
        switch (v.getId()){
            case R.id.bt_send:
                //通过chatpresenter处理发送消息的逻辑
                chatPresenter.sendMessage(username,msg);
                editChat.setText("");
                Log.i("username", "onClick: ");
                break;
        }
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
}
