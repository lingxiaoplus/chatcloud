package com.lingxiao.mvp.huanxinmvp.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.utils.UIUtils;

/**
 * Created by lingxiao on 2017/8/16.
 */

public class ViewPagerAdapter extends PagerAdapter{
    private String[] tabStr;
    private SwipeRefreshLayout sf_find;
    private RecyclerView rv_find;
    private View view;

    public ViewPagerAdapter(String[] tabStr,View view){
        this.tabStr = tabStr;
        this.view = view;
    }
    @Override
    public int getCount() {
        return tabStr.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        //view = View.inflate(container.getContext(),R.layout.pager_item,null);
        container.addView(view);
        Log.i("main","pager的instantiateItem: ");
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabStr[position];
    }
    public View getRootView(){
        return view;
    }
}
