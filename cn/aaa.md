# <a name="aaa"></a> 第九章 认证授权和记账

绝大多数 Web 应用都免不了对访问用户的认证及其与系统交互操作的授权. 很多正式 (Non-trivial) 的系统都会将用户的操作记录下来,以备后查. 我们把系统的这三个功能简称为 AAA (Authentication, Authorisation, Accounting).

ActFramework 通过 [act-aaa](https://github.com/actframework/act-aaa-plugin) 插件为应用提供 AAA 的实现框架.

## <a name="introduction"></a> 9.1 介绍

act-aaa 插件为 ActFramework 应用程序提供认证,授权以及记账的框架与工具. 和现有的安全工具包括 [Apache Shiro](https://shiro.apache.org/) 以及 [Spring Security](https://spring.io/projects/spring-security) 相比, act-aaa-plugin 提供了更为简便的集成, 面向资源的授权, 内置行级别权限分辨以及自动记账功能. 

## <a name="authentication"></a> 9.2 认证

### <a name="principal"></a> 9.2.1 认证主体

认证主体 (Principal) 在认证过程中代表系统中定义好的用户帐户. 认证主体携带一下信息:

1. username - 用户名
2. password - 密码
3. roles - 该用户的角色集合
4. permissions - 该用户的权限集合
5. privilege - 该用户的特权级别

通常情况下使用用户名和密码来确认某个登录会话的认证主体是否存在系统中.

act-aaa 提供了 `UserBase` 类 (集成 act-morphia 的应用则使用 `MorphiaUserBase` 类) 帮助应用定义自己的用户类以提供认证主题 (Principal). 

## <a name="authorisation"></a> 9.3 授权

## <a name="accounting"></a> 9.4 记账

## <a name="advanced_topic"></a> 9.5 高级课题

## <a name="plugin_authentication_mechanism"></a> 9.5.1 引入其他认证机制

## <a name=""></a> 9.5.2 自定义 TBD
