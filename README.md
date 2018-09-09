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



## 注册流程：

先到环信注册用户，如果环信注册成功，再到腾讯云注册，如果腾讯云注册失败，那么要把环信的账号删除，然后重新到环信注册
这个项目是对mvp的入门，大概的思维导图是这样的：
![image](http://oqz3bypff.bkt.clouddn.com/mvp.png)



注册逻辑在RegistPresenterImpl.java类里，在注册前先对密码进行md5加密，然后再注册，主要代码如下：

```java
public void registUser(final String username, final String psd,final String phone) {
        //注册时加密
        md5Psd = psd;
        try {
            md5Psd = MD5Util.getEncryptedPwd(psd);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final AVUser user = new AVUser();// 新建 AVUser 对象实例
        user.setUsername(username);// 设置用户名
        user.setPassword(md5Psd);// 设置密码
        user.setMobilePhoneNumber(phone); //设置手机号
        //在子线程中进行
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {

                if (e == null) {
                    // 注册成功，把用户对象赋值给当前用户 AVUser.getCurrentUser()
                    //在环信注册
                    //注册失败会抛出HyphenateException
                    final String objId = AVUser.getCurrentUser().getObjectId();
                    ThreadUtils.runOnSonUIThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().createAccount(username, md5Psd);
                                //注册成功 在主线程中通知界面跳转
                                ThreadUtils.runOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRegistView.onGetRegistState(objId,username,md5Psd,true,null);
                                    }
                                });
                            } catch (HyphenateException e1) {
                                e1.printStackTrace();
                                //注册失败，删除用户
                                try {
                                    user.delete();
                                } catch (AVException e2) {
                                    e2.printStackTrace();
                                }
                                //环信注册失败    通知界面显示注册失败
                          mRegistView.onGetRegistState(objId,username,md5Psd,false,e1.getDescription());
                                Log.i(TAG, "环信注册失败");
                            }
                        }
                    });

                } else {
                    // 失败的原因可能有多种，常见的是用户名已经存在。
                    //learncloud注册失败    通知界面显示注册失败
                    try {
                        user.delete();
                    } catch (AVException e2) {
                        e2.printStackTrace();
                    }
                    if (e.getCode() == 202){
                        mRegistView.onGetRegistState(null,username,psd,false,"用户名已经存在");
                    }else if (e.getCode() == 214){
                        mRegistView.onGetRegistState(null,username,psd,false,"手机号已经被注册");
                    }else {
                        mRegistView.onGetRegistState(null,username,psd,false,e.getMessage());
                    }

                }
            }
        });
    }
```



#### 界面启动流程

启动界面是LogoActivity，这个Activity不做任何界面渲染，只需要显示一个logo，这么做的目的是，由于使用太多第三方sdk，而且大部分sdk都需要在Application中初始化，大大拖慢了应用的启动速度，存在一段时间的白屏，用户体验不是很好，所以把大部分sdk在InitIalizeService中开一个线程初始化，但是在leancloud只能在主线程中初始化，所以就放在SplashActivity中对leancloud的sdk进行懒加载初始化。

![](http://oqz3bypff.bkt.clouddn.com/LogoActivity.png)

#### 图片上传到七牛

异步上传图片到七牛云，出于安全策略，七牛云需要客户端应向业务服务器每隔一段时间请求上传凭证，由于没有编写服务端代码，所以只能直接在客户端使用 AccessKey / SecretKey 生成对应的凭证。文件上传相关代码在QiNiuSdkHelper.java这个类里，使用者只需要在string.xml中将 AccessKey / SecretKey替换为自己在七牛云获取到的即可。

获取凭证的方法如下（ContentValue.BUCKET是存储空间名称 ）：

```java
 private String getToken(String key){
        //这句就是生成token  bucket:key 允许覆盖同名文件
        //insertOnly 如果希望只能上传指定key的文件，
        //并且不允许修改，那么可以将下面的 insertOnly 属性值设为 1
        String token = Auth.create(AccessKey,SecretKey)
            .uploadToken(ContentValue.BUCKET,
                        key,3600, new StringMap().put("insertOnly", 0));
        return token;
    }
```

文件上传：

```java
//第一个是本地文件路径 第二个是保存到七牛云的文件名，第三个是凭证
QiNiuSdkHelper.getInstance().upload(path,name,getToken(name));
```





## 感谢开源，用到的开源框架：

- okhttp3 网络请求
- glide 图片加载
- zxing   二维码扫描
- eventbus 事件总线 应用内各个线程之间的通讯
- android skin-support 主题换肤
- ImageSelector 仿微信图片选择器

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

