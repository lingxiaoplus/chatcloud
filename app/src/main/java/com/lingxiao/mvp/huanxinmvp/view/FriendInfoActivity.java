package com.lingxiao.mvp.huanxinmvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel;
import com.lingxiao.mvp.huanxinmvp.model.ContactsModel_Table;
import com.lingxiao.mvp.huanxinmvp.utils.GlideHelper;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.widget.SignalCardLayout;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendInfoActivity extends BaseActivity {

    @BindView(R.id.img_friend)
    CircleImageView imgFriend;
    @BindView(R.id.friend_info_sex)
    SignalCardLayout friendInfoSex;
    @BindView(R.id.friend_info_desc)
    SignalCardLayout friendInfoDesc;
    @BindView(R.id.friend_info_phone)
    SignalCardLayout friendInfoPhone;
    @BindView(R.id.bt_send_message)
    Button btSendMessage;
    @BindView(R.id.friend_info_age)
    SignalCardLayout friendInfoAge;
    @BindView(R.id.friend_nickname)
    TextView friendNickname;
    @BindView(R.id.tv_friend_username)
    TextView friendUsername;
    @BindView(R.id.toolbar_friend)
    Toolbar toolbarFriend;
    private ContactsModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarFriend);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("详细信息");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        final String userName = intent.getStringExtra("name");
        model = SQLite
                .select()
                .from(ContactsModel.class)
                .where(ContactsModel_Table.contactUserName.eq(userName))
                .querySingle();

        if (model.exists()) {
            GlideHelper.loadImageWithData(
                    model.getProtrait(),
                    imgFriend,
                    model.getUpdateAt());
            friendInfoSex.setRightText(model.sex == 0 ? "男" : "女");
            friendInfoDesc.setRightText(model.desc);
            friendInfoPhone.setRightText(model.phone);
            friendInfoAge.setRightText(model.age + "");
            friendNickname.setText("昵称："+model.nickName);
            friendUsername.setText("环聊号："+userName);
        } else {
            ToastUtils.showToast("联系人初始化失败");
        }

        btSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //单击跳转到聊天界面
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("name", userName);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
