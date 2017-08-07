package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import android.util.Log;

import com.google.gson.Gson;
import com.lingxiao.mvp.huanxinmvp.bean.FindBean;
import com.lingxiao.mvp.huanxinmvp.global.Content;
import com.lingxiao.mvp.huanxinmvp.presenter.FindPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.CacheUtils;
import com.lingxiao.mvp.huanxinmvp.utils.HttpUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.view.FindView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by lingxiao on 17-7-16.
 */

public class FindPresenterImpl implements FindPresenter{
    private FindView findView;
    private FindBean findBean;
    private ArrayList<FindBean.DetailMsg> newsList;
    private int initPage = 20;

    public FindPresenterImpl(FindView findView){
        this.findView = findView;
    }
    @Override
    public void newsUpDate(final int currentPage) {
        HttpUtils.doGet(Content.news_url+initPage+currentPage, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                newsList = new ArrayList<FindBean.DetailMsg>();
                newsList = parsingJson(result);
                findView.onNewsUpdate(newsList,currentPage);
                //写缓存
                CacheUtils.setCache(Content.news_url,result);
            }
        });
    }

    private ArrayList<FindBean.DetailMsg> parsingJson(String result) {
        Gson gson = new Gson();
        findBean = gson.fromJson(result, FindBean.class);
        FindBean.NewsData data = findBean.data;
        newsList = data.list;
        return newsList;
    }

    @Override
    public void initNews() {
        //初始化新闻
        String result = CacheUtils.getCache(Content.news_url);
        if (result != null){
            newsList = parsingJson(result);
        }else {
            if (newsList != null){
                newsUpDate(initPage);
            }
        }
        findView.onInitNews(newsList);
    }
}
