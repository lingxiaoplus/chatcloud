package com.lingxiao.mvp.huanxinmvp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.adapter.MessageAdapter;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.MessagePresenterImpl;
import com.lingxiao.mvp.huanxinmvp.presenter.MessagePresenter;
import com.lingxiao.mvp.huanxinmvp.view.ChatActivity;
import com.lingxiao.mvp.huanxinmvp.view.MessageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by lingxiao on 17-6-29.
 */

public class MessageFragment extends BaseFragment implements MessageView{

    private SwipeRefreshLayout sf_message;
    private RecyclerView rv_message;
    private MessagePresenter messagePresenter;
    private MessageAdapter adapter;

    @Override
    public void initData() {
    }
    @Override
    public View initView() {
        View view = View.inflate(getContext(),R.layout.fragment_message,null);
        sf_message = (SwipeRefreshLayout) view.findViewById(R.id.sf_message);
        rv_message = (RecyclerView) view.findViewById(R.id.rv_message);
        messagePresenter = new MessagePresenterImpl(this);
        adapter = new MessageAdapter(null);
        adapter.setOnMsgClickListener(new MessageAdapter.onMsgClickListener() {
            @Override
            public void onMsgClick(View v, String username) {
                Intent intent = new Intent(getContext(),ChatActivity.class);
                intent.putExtra("name",username);
                startActivity(intent);
            }
        });
        rv_message.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_message.setAdapter(adapter);
        messagePresenter.getMessages();
        sf_message.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                messagePresenter.getMessages();
            }
        });

        return view;
    }

    @Override
    public void onGetAllMessages(List<EMConversation> conversationList) {
        sf_message.setRefreshing(false);
        adapter.setConversationList(conversationList);
        adapter.notifyDataSetChanged();
        Log.i("fragment", "daxiao"+conversationList.size());
    }

    @Override
    public void onClearAllUnreadMark() {
        messagePresenter.getMessages();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessageEvent(List<EMMessage> list){
        messagePresenter.getMessages();
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
