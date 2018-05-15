package com.lingxiao.mvp.huanxinmvp.view.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.event.SkinChangeEvent;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;
import com.lingxiao.mvp.huanxinmvp.widget.SkinTabLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by lingxiao on 17-6-29.
 */

public class FindFragment extends BaseFragment {

    @BindView(R.id.tablayout_find)
    SkinTabLayout tabLayout;
    @BindView(R.id.vp_find)
    ViewPager vpFind;
    private String[] tabStr = new String[]{"文艺", "科技", "社会", "生活"};
    private NewsFragment newsFragment;

    @Override
    public void initData() {
        for (int i = 0; i < tabStr.length; i++) {

            tabLayout.addTab(tabLayout.newTab().setText(tabStr[i]));
        }

        PagerAdapter adapter = new PagerAdapter(getFragmentManager());
        vpFind.setAdapter(adapter);
        tabLayout.setupWithViewPager(vpFind);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public View initView() {
        View view = View.inflate(getContext(), R.layout.fragment_find, null);

        //View vpRootVIew = View.inflate(getContext(),R.layout.pager_item,null);
        //tabLayout = view.findViewById(R.id.tablayout_find);

        return view;
    }

    private class PagerAdapter extends FragmentPagerAdapter{

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            newsFragment = new NewsFragment();
            Bundle bundle = new Bundle();
            if (position == 0){
                bundle.putString("url",ContentValue.art_url);
            }else if (position == 1){
                bundle.putString("url",ContentValue.science_url);
            }else if (position == 2){
                bundle.putString("url",ContentValue.society_url);
            }else {
                bundle.putString("url",ContentValue.life_url);
            }
            newsFragment.setArguments(bundle);
            return newsFragment;
        }

        @Override
        public int getCount() {
            return tabStr.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabStr[position];
        }
    }

}
