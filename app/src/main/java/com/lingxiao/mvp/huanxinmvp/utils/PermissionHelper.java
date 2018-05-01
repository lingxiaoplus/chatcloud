package com.lingxiao.mvp.huanxinmvp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.lingxiao.mvp.huanxinmvp.MainActivity;
import com.lingxiao.mvp.huanxinmvp.view.BaseActivity;

public class PermissionHelper {
    private static Context mContext;
    private static BaseActivity mActivity;
    private static String mTitle,mMsg;
    private static final int REQUEST_TAKE_PHOTO_PERMISSION = 200;
    private static PermissionHelper helper;
    private static String mPermession;
    private PermissionHelper(){

    }
    public static PermissionHelper getInstance(Context context,BaseActivity activity,String title,String msg,String per){
        if (helper == null){
            helper = new PermissionHelper();
        }
        mContext = context;
        mActivity = activity;
        mTitle = title;
        mMsg = msg;
        mPermession = per;
        return helper;
    }
    /**
     * 获取权限
     * 如同Manifest.permission.CAMERA
     */
    public boolean getPermission(){
        if (ContextCompat.checkSelfPermission(mContext,
                mPermession)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mContext,
                        mPermession)
                        != PackageManager.PERMISSION_GRANTED) {
            //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
            showDialog();
        } else {
            //有权限了
            return true;
        }
        return false;
    }

    private void showDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);//注意这个是activity，不是context
        dialog.setTitle(mTitle);
        dialog.setMessage(mMsg);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{mPermession},
                        REQUEST_TAKE_PHOTO_PERMISSION);
            }
        });
        dialog.show();
    }
}
