# chatcloud
基于环信sdk的即时通讯项目，由于用的是第三方服务端，所以在开发上有所限制

服务端还在开发中，所以用户数据都存入的leancloud提供的云数据库里，后期会迁移到个人服务端

## 客户端界面

![login](https://s1.ax1x.com/2018/06/03/CTm8de.png)
![message](https://s1.ax1x.com/2018/06/03/CTmtJA.png)
![message](https://s1.ax1x.com/2018/06/03/CTmtJA.png)
![phone](https://s1.ax1x.com/2018/06/03/CTmNRI.png)
![addfriend](https://s1.ax1x.com/2018/06/03/CTmUzt.png)
![settings](https://s1.ax1x.com/2018/06/03/CTmwsf.png)

[下载地址](https://www.pgyer.com/xEPF)



## 项目中用到的开源框架：

okhttp3 网络请求框架

glide 图片加载框架

zxing   二维码扫描，我对它做了一下简化

eventbus 事件总线 应用内各个线程之间的通讯

android skin-support 换肤框架

ImageSelector 仿微信图片选择器

## 注册流程：

先到环信注册用户，如果环信注册成功，再到腾讯云注册，如果腾讯云注册失败，那么要把环信的账号删除，然后重新到环信注册
这个项目是对mvp的入门，大概的思维导图是这样的：
![image](http://oqz3bypff.bkt.clouddn.com/mvp.png)

## 开发记录：

##### v1.0.0

1. 全局material design实现
2. RecyclerView + CardView 展示新闻列表
3. 自定义组合控件展示联系人列表
4. toolbar替代actionbar

##### 1.0.1

1. 主题换肤实现
2. 支持个人信息修改，头像、昵称、个人描述等修改
3. 添加语音、视频通话
4. 程序更新实现
5. 增加七牛对象存储sdk用于存储用户头像

