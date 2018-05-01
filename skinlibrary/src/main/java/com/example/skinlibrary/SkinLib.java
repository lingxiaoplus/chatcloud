package com.example.skinlibrary;

import android.app.Application;
import android.content.Context;

import skin.support.SkinCompatManager;
import skin.support.app.SkinCardViewInflater;
import skin.support.constraint.app.SkinConstraintViewInflater;
import skin.support.design.app.SkinMaterialViewInflater;

public class SkinLib {
    public static void init(Application app){
        //初始化皮肤加载框架
        SkinCompatManager.withoutActivity(app)                         // 基础控件换肤初始化
                .addInflater(new SkinMaterialViewInflater())            // material design 控件换肤初始化[可选]
                .addInflater(new SkinConstraintViewInflater())          // ConstraintLayout 控件换肤初始化[可选]
                .addInflater(new SkinCardViewInflater())                // CardView v7 控件换肤初始化[可选]
                .setSkinStatusBarColorEnable(true)                     // 关闭状态栏换肤，默认打开[可选]
                .setSkinWindowBackgroundEnable(true)                   // 关闭windowBackground换肤，默认打开[可选]
                .loadSkin();
    }

    /**
     * 根据color后缀加载替换皮肤
     */
    public static void changeSkinDef(String skinName){
        SkinCompatManager
                .getInstance()
                .loadSkin(skinName,
                        SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN); // 后缀加载
        //SkinCompatManager
        // .getInstance().
        // loadSkin("night",
        // SkinCompatManager.SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN); // 前缀加载
    }

    public static void restoreDefaultTheme(){
        // 恢复应用默认皮肤
        SkinCompatManager.getInstance().restoreDefaultTheme();
    }
}
