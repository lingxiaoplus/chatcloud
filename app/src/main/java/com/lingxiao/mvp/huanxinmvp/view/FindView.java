package com.lingxiao.mvp.huanxinmvp.view;

import com.lingxiao.mvp.huanxinmvp.model.FindBean;

import java.util.ArrayList;

/**
 * Created by lingxiao on 17-7-16.
 */

public interface FindView {
    //更新新闻
    void onNewsUpdate(ArrayList<FindBean.DetailMsg> newsList,int currentPage);
    //初始化新闻
    void onInitNews(ArrayList<FindBean.DetailMsg> newsList);
}
