# chatcloud
基于环信sdk的即时通讯项目，由于用的是第三方服务端，所以在开发上有所限制

服务端还在开发中，后期会迁移到个人服务端

# 项目中用到的开源框架：

okhttp3 

fresco  facebook的开源库

zxing   二维码扫描，我对它做了一下简化

eventbus 应用内各个线程之间的通讯

由于环信不提供对用户的数据存储，所以我使用了腾讯云提供的tab移动开发工具，用于存储用户的数据

# 注册流程：

先到环信注册用户，如果环信注册成功，再到腾讯云注册，如果腾讯云注册失败，那么要把环信的账号删除，然后重新到环信注册
这个项目是对mvp的入门，大概的思维导图是这样的：
![image](http://oqz3bypff.bkt.clouddn.com/mvp.png)

# 开发记录：

v1.0.1
全局material design实现

RecyclerView + CardView 展示新闻列表

自定义组合控件展示联系人列表

toolbar替代actionbar
