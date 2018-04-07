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
}
