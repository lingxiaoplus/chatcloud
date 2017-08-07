package com.lingxiao.mvp.huanxinmvp.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 避免toast显示时间问题
 */

public class ToastUtils {
    private static Toast mToast;
    public static void showToast(String text){
        if (mToast == null){
            //第一次，创建toast对象
            mToast = Toast.makeText(UIUtils.getContext(),text,Toast.LENGTH_SHORT);
        }else{
            //如果toast对象存在，，只需要修改文字
            mToast.setText(text);
        }
        mToast.show();
    }
}
