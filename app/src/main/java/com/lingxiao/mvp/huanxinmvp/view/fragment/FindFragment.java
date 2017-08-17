package com.lingxiao.mvp.huanxinmvp.view.fragment;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.adapter.FindAdapter;
import com.lingxiao.mvp.huanxinmvp.adapter.ViewPagerAdapter;
import com.lingxiao.mvp.huanxinmvp.bean.FindBean;
import com.lingxiao.mvp.huanxinmvp.listener.MyRecycleListener;
import com.lingxiao.mvp.huanxinmvp.presenter.FindPresenter;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.FindPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.utils.ThreadUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
import com.lingxiao.mvp.huanxinmvp.view.FindView;
import com.lingxiao.mvp.huanxinmvp.view.WebViewActivity;

import java.util.ArrayList;

/**
 * Created by lingxiao on 17-6-29.
 */

public class FindFragment extends BaseFragment implements FindView{
    private FindPresenter findPresenter;
    private SwipeRefreshLayout sf_find;
    private RecyclerView rv_find;
    private FindAdapter adapter;
    private LinearLayoutManager manager;
    private TabLayout tabLayout;
    private ViewPager vp_find;
    private String[] tabStr = new String[]{"文艺","科技","社会","生活"};

    @Override
    public void initData() {
        //findPresenter.newsUpDate(10);
        findPresenter.initNews();
    }

    @Override
    public View initView() {
        View view = View.inflate(getContext(), R.layout.fragment_find,null);

        View vpRootVIew = View.inflate(getContext(),R.layout.pager_item,null);
        tabLayout = view.findViewById(R.id.tablayout_find);
        //vp_find = view.findViewById(R.id.vp_find);
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
                findPresenter.newsUpDate(0);
            }
        });
        adapter.setOnFindClickListener(new FindAdapter.onFindClickListener() {
            @Override
            public void onFindClick(View v ,String url) {
                Intent intent = new Intent(mActivity, WebViewActivity.class);
                intent.putExtra("findUrl",url);
                mActivity.startActivity(intent);
            }
        });

        rv_find.setOnScrollListener(new MyRecycleListener(manager) {
            @Override
            public void onLoadMore(int currentPage) {
                findPresenter.newsUpDate(currentPage);
            }
        });
        for (int i = 0; i < tabStr.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabStr[i]));
        }
        return view;
    }

    @Override
    public void onNewsUpdate(ArrayList<FindBean.DetailMsg> newsList,int currentPage) {
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
        //findPresenter.initNews();
        adapter.setMsgArrayList(newsList);
        adapter.notifyDataSetChanged();
    }
}
