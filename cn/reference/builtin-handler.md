# <a name="builtin-handler">附录 3 ActFramework 内置服务

ActFramework 提供了一些内置服务, 方便应用开发和使用. 所有的系统内置服务均被映射到以 `/~/` 开头的 URL 路径上.

1. [内置的响应器参考](#builtin-handler-reference)
	* [1.1 GET /~/apibook - 访问应用的 API 文档](#get-apibook) - 仅在开发模式有效
	* [1.2 GET /~/asset - 访问 ActFramework 内置 css/js 资源](#get-asset), 主要用于 ActFramework 在开发模式下的错误页面
	* [1.3 POST /~/i18n/locale - 改变当前用户会话的 Locale](#post-i18n-locale)
	* [1.4 POST /~/i18n/timezone - 是改变当前用户会话的时区](#post-i18n-timezone), 
	* [1.5 GET /~/info - 显示应用信息](#get-info)
	* [1.6 GET /~/job/{id}/progress - 让应用查询特定后台任务的进度](#get-job-progress) - 这是一个 websocket 服务端点
	* [1.7 GET /~/pid - 显示应用进程号](#get-pid)
	* [1.8 GET /~/version - 显示应用版本信息](#get-version)
	* [1.9 GET /~/zen - 显示箴言列表](#get-zen)
2. [安全考虑与禁用内置服务](#disable-builtin-handler) 

## <a name="builtin-handler-reference"></a>1. 内置服务参考

### <a name="get-apibook"></a>1.1 GET /~/apibook

ActFramework 在开发模式下扫描程序源代码生成 API 文档. 访问 API 文档的坐标是 GET /~/apibook:

![image](https://user-images.githubusercontent.com/216930/38463411-c21ea972-3b3d-11e8-9739-2267f56e3419.png)

### <a name="get-asset"></a>1.2 GET /~/asset

ActFramework 内置了一些 css/js 资源文件, 主要用来支持:

1. 开发模式下的错误页面
1. 开发模式下的 API 文档.

一般情况下应用程序不应该访问 /~/asset/ 下面的资源

### <a name="post-i18n-locale"></a>1.3 POST /~/i18n/locale

该端点为应用程序提供改变当前用户会话的 Locale 的服务. 具体信息请参考[国际化](../i18n.md)

### <a name="post-i18n-timezone"></a>1.4 POST /~/i18n/locale

该端点为应用程序提供改变当前用户会话时区的服务. 具体信息请参考[国际化](../i18n.md)

### <a name="get-info"></a>1.5 GET /~/info 

提供当前应用的信息.

当通过浏览器直接访问该端点, 获得类似下面的信息:

```
_           _            _    _        _  
 |_|  |_  |   |   / \  \    /  / \  |_)  |   | \ 
 | |  |_  |_  |_  \_/   \/\/   \_/  | \  |_  |_/ 
                                                 
              powered by ActFramework r1.8.7-2f28

 version: v1.0-SNAPSHOT-180408_1425
scan pkg: com.mycom.helloworld
base dir: /tmp/1/helloworld
     pid: 31898
 profile: dev
    mode: DEV

     zen: Explicit is better than implicit.
```

如果是 JavaScript 发出 JSON 类型的请求, 则获得类似下面的返回结果:

```json
{
    "actVersion": "r1.8.7-2f28", 
    "appName": "helloworld", 
    "appVersion": "v1.0-SNAPSHOT-180408_1425", 
    "baseDir": "/tmp/1/helloworld/.", 
    "group": "", 
    "mode": "DEV", 
    "pid": "31898", 
    "profile": "dev"
}
```

### <a name="get-job-progress"></a>1.6 GET /~/job/{id}/progress

这是一个 websocket 服务端点, 为应用提供查询异步作业进度服务. 具体信息参考[作业调度](../job.md)

### <a name="get-pid"></a>1.7 GET /~/pid

返回应用的进程号, 直接从浏览器访问结果示例:

```
31898
```

发出 `Accept=application/json` 的请求,返回结果为:

```json
{
    "pid": "31898"
}
```

### <a name="get-version"></a>1.8 GET /~/version

返回当前应用的版本信息. 访问该段口得到的返回结果示例:

```
{
    "act": {
        "artifactId": "act", 
        "buildNumber": "2f28", 
        "packageName": "act", 
        "projectVersion": "1.8.7", 
        "unknown": false, 
        "version": "r1.8.7-2f28"
    }, 
    "app": {
        "artifactId": "helloworld", 
        "buildNumber": "180408_1425", 
        "packageName": "com.mycom.helloworld", 
        "projectVersion": "1.0-SNAPSHOT", 
        "unknown": false, 
        "version": "v1.0-SNAPSHOT-180408_1425"
    }
}
```

这个结果和 HTTP Request 的 Accept 头设置没有关系. 

### <a name="get-zen"></a>1.9 GET /~/zen

返回应用的箴言列表. 这个服务端点对应用没有具体意义

## 2. <a name="disable-builtin-handler"></a>安全考虑与禁用内置服务

在 ActFramework 内置的服务端点中有些会泄露系统内部信息, 应该加以安全控制. 如果应用使用 [act-aaa](../aaa.md) 提供安全服务, 除下列服务外所有其他的内置服务端点都需要接受安全认证:

* GET /~/apibook
* GET /~/asset
* POST /~/i18n/locale
* POST /~/i18n/timezone
* GET /~/zen

明确地讲, 访问下面的内置服务需要接受认证:

* GET /~/info
* GET /~/job/{id}/progress
* GET /~/pid
* GET /~/version

如果需要完全禁用内置服务, 在应用配置文件中加上:

```
built_in_req_handler=false
```

**注意** 下面的服务端点可能会影响应用的功能, 因此不能被禁用:

* GET /~/asset
* POST /~/i18n/locale
* POST /~/i18n/timezone
* GET /~/job/{id}/progress

`apibook` 只能用于开发模式, 因此也不会被禁用.
