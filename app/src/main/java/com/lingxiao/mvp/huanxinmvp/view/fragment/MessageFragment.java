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
import com.lingxiao.mvp.huanxinmvp.event.MessageEvent;
import com.lingxiao.mvp.huanxinmvp.event.SkinChangeEvent;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.MessagePresenterImpl;
import com.lingxiao.mvp.huanxinmvp.presenter.MessagePresenter;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.view.ChatActivity;
import com.lingxiao.mvp.huanxinmvp.view.MessageView;
import com.lingxiao.mvp.huanxinmvp.widget.SwipeItemLayout;

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
        sf_message.setRefreshing(false);
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

            @Override
            public void onMsgDelete(View view, int pos, String username) {
                messagePresenter.deleteMessages(username,pos);
            }
        });
        rv_message.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_message.setAdapter(adapter);
        rv_message.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getActivity()));

        messagePresenter.getMessages();
        sf_message.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                messagePresenter.getMessages();
            }
        });
        sf_message.setRefreshing(true);
        sf_message.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorAccent,
                R.color.btnNormal);
        return view;
    }

    @Override
    public void onGetAllMessages(List<EMConversation> conversationList) {
        adapter.setConversationList(conversationList);
        adapter.notifyDataSetChanged();
        sf_message.setRefreshing(false);
        Log.i("fragment", "daxiao"+conversationList.size());
    }

    @Override
    public void onClearAllUnreadMark() {
        messagePresenter.getMessages();
    }

    @Override
    public void onDelete(boolean result, String msg) {
        if (result){
            adapter.notifyDataSetChanged();
        }else {
            ToastUtils.showToast("删除失败"+msg);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessageEvent(List<EMMessage> list){
        messagePresenter.getMessages();
    }

    /**
     * 订阅chatactivity发送过来的消息,通知消息列表刷新
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageResult(MessageEvent event){
        messagePresenter.getMessages();
        ToastUtils.showToast("更新消息列表");
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }
}
