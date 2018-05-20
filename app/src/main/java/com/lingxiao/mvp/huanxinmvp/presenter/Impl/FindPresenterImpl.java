package com.lingxiao.mvp.huanxinmvp.presenter.Impl;

import android.util.Log;

import com.google.gson.Gson;
import com.lingxiao.mvp.huanxinmvp.model.FindBean;
import com.lingxiao.mvp.huanxinmvp.db.CacheNewsDao;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.presenter.FindPresenter;
import com.lingxiao.mvp.huanxinmvp.utils.HttpUtils;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;
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
    private CacheNewsDao cacheNewsDao;

    private int isInsert = 0; //判断是否需要存储到数据库
    private long exparTime;
    private ArrayList<FindBean.DetailMsg> cacheList;

    public FindPresenterImpl(FindView findView){
        this.findView = findView;
    }
    @Override
    public void newsUpDate(final int currentPage,String url) {
        HttpUtils.doGet(url+initPage+currentPage, new Callback() {
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
                //CacheUtils.setCache(ContentValue.news_url,result);
                //cacheNewsDao = CacheNewsDao.getInstance(UIUtils.getContext());
                if (cacheList != null){
                    //cacheNewsDao.deleteAll();
                }
                //cacheNewsDao.insert(newsList);
                Log.i("main", "刷新新闻内容");
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
    public void initNews(String url) {
        //初始化新闻
        //String result = CacheUtils.getCache(ContentValue.news_url);
        /*cacheList = new ArrayList<>();
        cacheNewsDao = CacheNewsDao.getInstance(UIUtils.getContext());
        if (cacheList != null){
            //newsList = parsingJson(cacheList);
            cacheList = cacheNewsDao.findAll();
            newsList = cacheList;
            Log.i("main", "读取缓存新闻");
        }else {
            if (newsList == null){
                newsUpDate(initPage,url);
                Log.i("main", "没有读取缓存新闻，更新新闻 ");
            }
        }*/
        newsUpDate(initPage,url);
        findView.onInitNews(newsList);
        Log.i("main", "初始化新闻内容");
    }
}
