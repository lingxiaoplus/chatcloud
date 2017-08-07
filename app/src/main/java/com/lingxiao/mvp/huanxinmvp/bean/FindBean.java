package com.lingxiao.mvp.huanxinmvp.bean;

import java.util.ArrayList;

/**
 * Created by lingxiao on 17-7-16.
 */

public class FindBean {
    public NewsData data;
    public class NewsData{
        public ArrayList<DetailMsg> list;

    }
    public class DetailMsg{
        public String title;    //标题
        public String headpic; //图片
        public String brief;   //简介
        public String origin_url;
        public siteInfo site_info;      //信息来源
        public String update_time;   //时间
        public String pub_date;
    }
    public class siteInfo{
        public String name;
        public String pic;
    }
}
