# 第十章 从社交服务认证

面向互联网的应用现在已经离不开社交服务了, 用户通常在多个社交服务中都保存有账号, 通过这些社交服务来提供用户认证可以让用户很容易注册到应用提供的服务中, 无需繁琐的用户注册和密码设置过程.

## <a name='install'></a> 10.1 安装 SocialLink 插件

ActFramework 核心库并没有提供社交服务认证的工具, 而是通过 SocialLink 插件来帮助应用实现这个功能. 使用 SocialLink 插件的办法是在项目的 `pom.xml` 文件中加入依赖:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-social-link</artifactId>
    <version>${act-social-link.version}</version>
</dependency>
```

**小提示** 如果使用了 `act-starter-parent` 作为项目的 parent, 则无需提供 `<version>${act-social-link.version}</version>`

## <a name='usage'></a> 10.2 配置

目前 SocialLink 插件提供了一下几种社交服务的集成:

* Google
* Facebook
* GitHub
* LinkedIn

如果需要使用这些社交服务, 应用首先需要到这些服务特定的页面注册自己的应用账号, 获得应用 `key` 和 `secret`. 下面是各种社交服务的账号管理页面地址:

* Google - https://console.cloud.google.com/apis/credentials
* Facebook - https://developers.facebook.com/apps
* Github - https://github.com/settings/developers
* LinkedIn - https://www.linkedin.com/developer/apps

**注意** 每个社交服务注册页面中都有 origin 和 redirect URL 的设置, 其中的 origin 需要设置请求发起的站点名, 例如:

```
https://myapp.mycom.com
```

而 redirect URL 则需要设置为下面的方式:

```
https://myapp.mycom.com/~/social/callback?provider=<provider_id>
```

上面的 `provider-id` 可以是:

* google
* facebook
* github
* linkedin

在注册应用账号并获得服务 `key` 和 `secret` 之后将相关信息放进应用的配置文件中:

```
#
# Twitter
#
social_link.twitter.key=your_consumer_key
social_link.twitter.secret=your_consumer_secret

#
# Facebook
#
social_link.facebook.key=your_client_id
social_link.facebook.secret=your_client_secret
# this scope is the minimum SecureSocial requires.  You can add more if required by your app.
social_link.facebook.scope=email

#
# GitHub
#
social_link.github.key=your_consumer_key
social_link.github.secret=your_consumer_secret

#
# Google
#
social_link.google.scope=openid email profile
social_link.google.key=your_consumer_key
social_link.google.secret=your_consumer_secret

#
# LinkedIn
#
social_link.linkedin.scope=r_emailaddress r_basicprofile
social_link.linkedin.key=your_consumer_key
social_link.linkedin.secret=your_consumer_secret
```

## <a name="usage"></a> 10.3 使用

使用 `SocialLink` 的方法很简单:

1. 页面上需要提供社交服务认证链接
2. 后端应用提供处理用户 profile 的逻辑

### <a name="links"></a> 10.3.1 社交认证链接

所有社交服务都使用相同的社交认证链接:

```
/~/social/start?provider=<provider-id>
```

上面的 `provider-id` 可以是:

* google
* facebook
* github
* linkedin

下面是一种常见的社交服务链接页面组件:

```html
<ul id='social-links'>
    <li>
        <a href='/~/social/start?provider=google'><img src='/asset/img/social/google.gif'></a>
    </li>
    <li>
        <a href='/~/social/start?provider=facebook'><img src='/asset/img/social/facebook.gif'></a>
    </li>
    <li>
        <a href='/~/social/start?provider=github'><img src='/asset/img/social/github.gif'></a>
    </li>
    <li>
        <a href='/~/social/start?provider=linkedin'><img src='/asset/img/social/linkedin.gif'></a>
    </li>
</ul>
```

### <a name='handle-social-profile'></a> 10.3.2 处理用户的社交 Profile

当用户点击上节中讲到的社交链接之后, SocialLink 会向社交服务发起 OAuth 认证请求, 触发一系列交互过程, 包括用户在社交网上的登录以及认证确认. 这一系列操作都无需应用介入. 但认证最终结束之后 SocialLink 将会拿到用户的 Social Profile, 并触发一个 `SocialProfile.Fetched` 事件, 应用需要侦听该事件并完成用户登录. 下面是处理该事件的示例代码:

```java
@OnEvent
public void handleSocialLogin(SocialProfile.Fetched profileFetchedEvent, ActionContext context, User.Dao userDao) {
    SocialProfile profile = profileFetchedEvent.source();
    String email = profile.getEmail();
    User user = userDao.findByEmail(email);
    if (null == user) {
        // register new user
        user = new User(profile);
        userDao.save(user);
    }
    context.login(user.email);
}
```
