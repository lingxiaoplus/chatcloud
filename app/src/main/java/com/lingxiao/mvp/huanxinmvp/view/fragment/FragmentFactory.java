package com.lingxiao.mvp.huanxinmvp.view.fragment;

import java.util.HashMap;

/**
 * fragment工厂
 */

public class FragmentFactory {
    private static HashMap<Integer,BaseFragment> mHashMap = new HashMap<Integer,BaseFragment>();
    public static BaseFragment createFragment(int pos){
        BaseFragment fragment = mHashMap.get(pos);
        if (fragment == null){
            switch (pos){
                case 0:
                    fragment = new MessageFragment();
                    break;
                case 1:
                    fragment = new PhoneFragment();
                    break;
                case 2:
                    fragment = new FindFragment();
                    break;
                case 3:
                    fragment = new UserCardFragment();
                    break;
            }
            mHashMap.put(pos,fragment);
        }

        return fragment;
    }
}
