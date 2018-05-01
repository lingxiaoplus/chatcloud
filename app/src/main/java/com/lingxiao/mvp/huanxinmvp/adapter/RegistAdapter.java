package com.lingxiao.mvp.huanxinmvp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lingxiao.mvp.huanxinmvp.view.fragment.BaseFragment;

import java.util.List;

public class RegistAdapter extends FragmentStatePagerAdapter{
    private List<BaseFragment> fragmentList;
    public RegistAdapter(FragmentManager fm,List<BaseFragment> list) {
        super(fm);
        this.fragmentList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList == null?0:fragmentList.size();
    }
}
