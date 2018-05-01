package com.lingxiao.mvp.huanxinmvp.global;

/**
 * Created by lingxiao on 17-6-28.
 */

public class ContentValue {
    public static final String KEY_USERNAME = "key_username";
    public static final String KEY_PSD = "key_psd";
    public static final String NEWS_URL = "http://reader.smartisan.com" +
            "/index.php?r=find" +
            "/GetArticleList&";
    //文艺
    public static final String art_url = NEWS_URL +"cate_id=10&" +
            "art_id=&" +
            "page_size=";
    //科技15
    public static final String science_url = NEWS_URL +"cate_id=15&" +
            "art_id=&" +
            "page_size=";
    //社会16
    public static final String society_url = NEWS_URL+
            "cate_id=16&" +
            "art_id=&page_size=20";
    //生活11
    public static final String life_url = NEWS_URL +"cate_id=11&" +
            "art_id=&" +
            "page_size=";

    /**
     * leancloud字段
     */
    public static final String DESCRIPTION = "description";
    public static final String AGE = "age";
    public static final String PROTRAIT = "protrait";
    public static final String SEX = "sex";
    public static final String NICKNAME = "nickname";
    public static final String OBJECTID = "objectid";

    /**
     * 通话状态
     */
    public static final int CALL_CONNECTING = 1;
    public static final int CALL_CONNECTED = 2;
    public static final int CALL_ACCEPTED = 3;
    public static final int CALL_DISCONNECTED = 4;
    public static final int CALL_NETWORK_UNSTABLE = 5;
    public static final int CALL_NETWORK_NORMAL = 6;
    public static final int CALL_ERROR = 7;

    /**
     * 更改的类型
     */
    public static final int CHANGE_PROTRAIT = 1;
    public static final int CHANGE_NAME = 2;
    public static final int CHANGE_SEX = 3;
    public static final int CHANGE_DESC = 4;
    public static final int CHANGE_PHONE = 5;
    public static final int CHANGE_AGE = 6;

    /**
     * 七牛桶
     */
    public static final String BUCKET = "smailchat";
    public static final String QINIU_BASE_URL = "http://chat.lingxiaosuse.cn/";


    /**
     * 设置
     */
    public static final String UPDATE = "update";
    public static final String FRIEND = "friend";
    public static final String MESSAGE_REMIND = "message_remind";
    public static final String MESSAGE_VOICE = "message_voice";
    public static final String MESSAGE_SNAKE = "message_snake";

    /**
     * 版本更新
     */
    public static final String VERSION_CODE = "version_code";
    public static final String DOWNLOAD_URL = "download_url";
    public static final String VERSION_DES = "version_des";
    public static final String UPDATEURL = "http://chat.lingxiaosuse.cn/update/update.json";


}
