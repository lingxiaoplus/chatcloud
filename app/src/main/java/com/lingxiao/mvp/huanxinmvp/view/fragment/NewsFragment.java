package com.lingxiao.mvp.huanxinmvp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.adapter.FindAdapter;
import com.lingxiao.mvp.huanxinmvp.listener.MyRecycleListener;
import com.lingxiao.mvp.huanxinmvp.model.FindBean;
import com.lingxiao.mvp.huanxinmvp.presenter.FindPresenter;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.FindPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ThreadUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.lingxiao.mvp.huanxinmvp.view.FindView;
import com.lingxiao.mvp.huanxinmvp.view.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends BaseFragment implements FindView {
    private SwipeRefreshLayout sf_find;
    private RecyclerView rv_find;
    private FindAdapter adapter;
    private LinearLayoutManager manager;
    private FindPresenter findPresenter;
    private String url;
    private ArrayList<FindBean.DetailMsg> mList = new ArrayList<>();
    @Override
    public void initData() {
        Bundle bundle = getArguments();
        url = bundle.getString("url");
        findPresenter.initNews(url);
    }

    @Override
    public View initView() {
        View view = UIUtils.inflateView(R.layout.fragment_news);
        sf_find = (SwipeRefreshLayout) view.findViewById(R.id.sf_find);
        rv_find = (RecyclerView) view.findViewById(R.id.rv_find);
        adapter = new FindAdapter(null);
        manager = new LinearLayoutManager(getContext());
        rv_find.setLayoutManager(manager);
        rv_find.setAdapter(adapter);
        findPresenter = new FindPresenterImpl(this);
        sf_find.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                findPresenter.newsUpDate(0,url);
            }
        });
        adapter.setOnFindClickListener(new FindAdapter.onFindClickListener() {
            @Override
            public void onFindClick(View v ,String url,int position) {
                LogUtils.i("新闻详情url："+url);
                Intent intent = new Intent(mActivity, WebViewActivity.class);
                intent.putExtra("findUrl",url);
                intent.putExtra("title",mList.get(position).title);
                mActivity.startActivity(intent);
            }
        });

        rv_find.setOnScrollListener(new MyRecycleListener(manager) {
            @Override
            public void onLoadMore(int currentPage) {
                findPresenter.newsUpDate(currentPage,url);
            }
        });
        sf_find.setRefreshing(true);
        sf_find.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorAccent,
                R.color.btnNormal);
        return view;
    }

    @Override
    public void onNewsUpdate(ArrayList<FindBean.DetailMsg> newsList, int currentPage) {
        mList = newsList;
        adapter.setMsgArrayList(newsList);
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                sf_find.setRefreshing(false);
            }
        });
        //findPresenter.newsUpDate(currentPage);
    }

    @Override
    public void onInitNews(ArrayList<FindBean.DetailMsg> newsList) {
        mList = newsList;
        //findPresenter.initNews();
        adapter.setMsgArrayList(newsList);
        adapter.notifyDataSetChanged();
        //sf_find.setRefreshing(false);
    }


}
