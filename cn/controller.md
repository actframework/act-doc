# <a name="chapter_controller">第五章 控制器, 请求处理方法与响应返回

* [1 介绍](#intro)
* [2 请求与响应](#req_resp)
    * [2.1 使用请求与响应](#req_resp_usage)
    * [2.2 请求](#req)
    * [2.3 响应](#resp)
* [3 Session 和 Flash](#session_flash)
    * [3.1 Session 应用技巧](#session_best_practice)
    * [3.2 Session 和 Flash 的区别](#session_flash_diff)
    * [3.3 Session/Flash 应用例子](#session_flash_usage)
    * [3.4 Session 配置](#session_config)
* [4 ActionContext](#context)
    * [4.1 使用 ActionContext](#context_usage)
* [5 控制器与请求处理方法](#controller_request-handler)
    * [5.1 请求处理方法参数](#request-handler_params)
    * [5.2 控制器的依赖注入](#controller-di)
    * [5.3 单例还是多例](#single-multi)
* [6 参数绑定](#param-binding)
    * [6.1 绑定与解析](#binding-resolving)
    * [6.2 数据来源](#data-source)
        * [6.2.1 Cookie 数据绑定](#cookie-data-binding)
        * [6.2.2 Header 数据绑定](#header-data-binding)
        * [6.2.3 Session 数据绑定](#session-data-binding)
    * [6.3 请求数据编码](#data-encoding)
        * [6.3.1 Query 数组类型参数编码](#query-param-encoding)
        * [6.3.2 POST Form 编码](#post-form-encoding)
        * [6.3.3 POST JSON 编码](#post-json-encoding)
    * [6.4 简单类型绑定](#simple-type-data-binding)
        * [6.4.1 空值处理](#null-val)
        * [6.4.2 错误数据处理](#bad-data)
        * [6.4.3 枚举](#enum-binding)
    * [6.5 数组与集合类型绑定](#array-collection-binding)
        * [6.5.1 基本类型数组绑定](#primitive-array-binding)
        * [6.5.2 包装类型数组绑定](#wrap-array-binding)
        * [6.5.3 List 和 Set 绑定](#list-set-binding)
        * [6.5.4 Map 绑定](#map-binding)
    * [6.6 日期数据绑定](#date-data-binding)
        * [6.6.1 日期格式](#date-format)
        * [6.6.2 日期格式本地化](#date-format-localization)
        * [6.6.3 在绑定参数上指定日期格式](#date-format-specified)
    * [6.7 POJO 绑定](#pojo-binding)
		* [6.7.1 单个 POJO 绑定](#single-pojo-binding)
		* [6.7.2 POJO 数组或列表绑定](#list-pojo-binding)
		* [6.7.3 POJO Map 绑定](#map-pojo-binding)
    * [6.8 上传文件绑定](#file-upload)
    * [6.9 自定义数据绑定](#customize-data-binding)
        * [6.9.1 自定义 StringValueResolver](#customize-resolver)
        * [6.9.2 自定义 Binder](#customize-binder)
    * [6.10 绑定参数校验](#data-validation)
* [7 返回响应](#return-response)
    * [7.1 返回数据]
        * [7.1.1 返回模板]
        * [7.1.2 返回 JSON 响应]
        * [4.1.3 文件下载]
    * [7.2 返回状态]
        * [7.2.1 默认状态返回规则]
            * [7.2.1.1 200 Okay]
            * [7.2.1.2 201 Created]
            * [7.2.1.3 404 Not Found]
            * [7.2.1.4 从 Java 异常映射为 HTTP 错误状态]
        * [7.2.1 指定返回状态]
        * [7.2.3 自定义错误页面]
    * [7.3 设定 HTTP Header]
        * [7.3.1 Content-Type]
        * [7.3.2 设定其他 HTTP Header]
* [8 异步返回]

## <a name="intro"></a>1. 介绍

控制器 (Controller) 和响应返回是 MVC 中的 "C" 和 "V" 部分, 也是整个框架的核心. 下面是一个简单的控制器代码:

<a name="s1a"></a>

```java
// snippet 1a
package actdoc.sample;

import org.osgl.mvc.GetAction;

public class AppEntry {
    @GetAction
    public void home() {}
}
```

上面的代码中 `@GetAction` 建立了从 `HTTP GET /` 请求到 `AppEntry.home()` 方法的映射. 当收到该请求时, `AppEntry.home()` 方法被调用, 并生成响应返回请求端. 注意到该方法没有执行任何指令, 框架会依据情况自动选择返回逻辑:

1. 如果能找到 `resources/rythm/actdoc/sample/AppEntry/home.html` 则用这个模板文件生成响应内容并返回. 否则
2. 返回一个没有内容的 200 Okay 状态响应

下面是 actFramework 对控制器和请求处理方法的定义:

1. **控制器**.

    一个包括了若干请求请求处理方法的Java类. 上面的例子中 `ControllerDemo` 类是一个控制器

        - ActFramework并不要求控制器继承某个特定的类，也不要求控制器加上某个特定注解

1. **请求处理方法**

    指某个方法提供了一定的逻辑代码响应发送到特定路径的请求。简单的说如果在应用运行的时候有路由条目配置到某个方法，该方法即为请求处理方法。上面的例子中 `home()` 是一个请求处理方法

        - 请求处理方法可以是静态方法也可以是虚方法

**注意** 本章讨论范围是控制器和请求处理方法, 包括请求与响应, 请求参数绑定, 控制器依赖注入以及各种输出. 不包括从 URL 到请求处理方法的映射部分, 需要了解 ActFramework 如何从 URL 映射到请求处理方法的, 参见 [第四章 路由](routing.md)

## <a name="req_resp"></a>2. 请求与响应

HTTP 请求与响应是 Web 应用的输入和输出, 是所有 web 编程框架的核心数据结构。

Servlet 架构使用 `HttpServletRequest` 和 `HttpServletResponse` 两个类来封装 HTTP 请求与响应. 在 Servlet 刚刚开始的时候, Java Web 编程是围绕这两个类进行的, 应用开发人员必须手动从 `HttpServletRequest` 中获取请求参数, Header 变量等信息, 然后手动讲字串拼接并输出到 `HttpServletResponse` 响应对象提供的 `OutputStream`. 这并不是一种很好的开发体验. 后来慢慢出现了 JSP, Velocity 等模板技术, 让输出的处理变得非常方便. 但是请求参数解析的问题依然存在, 知道后来出现的 SpringMVC, PlayFramework 等框架提供了参数绑定特性. ActFramework 作为后来者, 立据前者肩头, 无疑在这方面提供更强大的支持, 让 Web 编程过程变得前所未有的简便.

虽然提供了各种高层封装手段, 在少数情况下, 开发人员可能还是需要直接对请求和输出进行操作. ActFramework 使用 [osgl-http](https://github.com/osglworks/java-http) 提供的 `H.Request` 类来封装 HTTP 请求, `H.Response` 则封装了 HTTP 响应对象。

### <a name="req_resp_usage"></a>2.1 使用请求与响应

在应用中使用请求与响应的示例代码:

<a name="s2-1a"></a>

```java
// snippet 2.1a
@GetAction("echo/a")
public void echo_a(H.Request req, H.Response resp) {
    String message = req.paramVal("message");
    resp.header("Content-Type", "text/plain").output().append(message).close();
}
```


**小贴士** ActFramework 对于输出响应有更多的表达方式, 上面的代码可以简化为:

<a name="s2-1b"></a>

```java
// snippet 2.1b
@GetAction("echo/b")
public void echo_b(H.Request req, H.Response resp) {
    String message = req.paramVal("message");
    resp.writeText(message);
}
```

而更简单的方式则是完全不使用 Request 和 Response 对象:

<a name="s2-1c"></a>
```java
// snippet 2.1c
import static Controller.Util.renderText;
...

@GetAction("echo/c")
public void echo_c(String message) {
    renderText(message);
}
```

甚至可以这样:

<a name="s2-1d"></a>
```java
// snippet 2.1d
@GetAction("echo/d")
public String echo_d(String message) {
    return message;
}
```



### <a name="req"></a>2.2 `H.Request` 请求对象

ActFramework 使用 `H.Request` 来封装 HTTP 请求，提供应用开发访问 HTTP 请求所需的方法：

* `H.Method method()` - 返回 HTTP 方法枚举
* `String header(String name)` - 返回 `name` 对应的 HTTP 头的值
* `Iterable<String> headers(String name)` - 返回 `name` 对应的HTTP 头的所有的值
* `Format accept()` - 返回 HTTP `Accept` 头解析出来的格式
* `String referrer()` - 返回 HTTP `Referer` 头的值
* `String referer()` - `referrer()` 方法的别名方法, 返回 HTTP `Referer` 头的值
* `String etag()` - 对于 `GET` 方法返回 HTTP `If-None-Match` 头的值, 对于 `POST`, `PUT`, `PATCH`, `DELETE` 方法返回 HTTP `If-Match` 头的值
* `boolean etagMatches(String etag)` - 检测传入的 `etag` 字串是否匹配当前请求的 `etag()`
* `boolean isModified(String etag, long since)` - 就传入的 `etag` 与 `since` 时间戳检查是否该请求的对象是否已经更改, 即原响应是否已经失效
* `boolean isAjax()` - 判断是否请求为 AJAX 请求
* `String path()` - 返回请求路径. 相当于 `HttpServletRequest.getServletPath()` 和 `HttpServletRequest.getPathInfo()` 用 `/` 拼接起来
* `String contextPath()` - 返回请求 context 路径. 在 ActFramework 应用中总是为空
* `String fullPath()` - 用 `/` 将 `contextPath()` 和 `path()` 拼接起来. 在 ActFramework 中 `fullPath()` 返回值总是等于 `path()` 返回值
* `String url()` - `fullPath()` 的别名, 在 ActFramework 中 `url()` 返回值总是等于 `path()` 返回值
* `String fullUrl` - 返回 `<scheme>://<host>:<port>/<path>`, 其中 `scheme` 来自 `scheme()`, `host` 来自 `domain()`, `port` 来自 `port()`, `path` 的值则是 `path()` 方法的返回结果
* `String query()` - 返回 HTTP 请求的查询字串, 及用户请求路径中 `?` 之后的部分
* `boolean secure()` - 查看 HTTP 请求是否来自安全连接. 当请求头有如下情况系统判定请求来自安全连接, 否则为非安全连接
    - `X-Forwarded-Proto` = `https`
    - `X-Forwarded-Ssl` = `on`
    - `Front-End-Https` = `on`
    - `X-Url-Scheme` = `https`
* `String scheme()` - 当请求来自安全连接时返回 `https`, 否则返回 `http`
* `String domain()` - 返回当前请求的 `host` 名字. `host` 名字从一下途径获取:
    1. 首先检查 `X-Forwarded-Host` 头, 如果无值则
    2. 检查 `Host` 头, 如果发现无值则设定 host 为空字串. 否则
    3. `host` (i.e. domain) 为值 `:` 之前部分; `port` 为值 `:` 之后部分
* `String host()` - `String domain()` 方法的别名方法
* `String port()` - 返回当前请求的 port.
* `String ip()` - 返回当前请求的 remote ip. 该值的解析过程为:
    1. 检查 `X-Forwarded-For` 头, 如果无值或值为 `unknown` 则
    2. 检查 `Proxy-Client-ip` 头, 如果无值或值为 `unknown` 则
    3. 检查 `Wl-Proxy-Client-Ip` 头, 如果无值或值为 `unknown` 则
    4. 检查 `HTTP_CLIENT_IP` 头, 如果无值或值为 `unknown` 则
    5. 检查 `HTTP_X_FORWARDED_FOR` 头, 如果无值或值为 `unknown` 则
    6. 返回下层网络栈 (undertow) 提供的 ip 地址
* `String userAgentStr()` - 返回请求的 `User-Agent` 头字串
* `UserAgent userAgent()` - 返回解析 `User-Agent` 字串得到的 `org.osgl.web.util.UserAgent` 对象
* `H.Cookie cookie(String name)` - 返回名字为 `name` 的 cookie
* `List<H.Cookie> cookies()` - 返回该请求上所有的 cookie
* `Format contentType()` - 返回由 `Content-Type` 头解析的 `H.Format` 对象
* `String characterEncoding()` - 返回由 `Content-Type` 头解析的 character encoding 字串
* `Locale locale()` - 返回由 `Accept-Language` 头解析的第一个 `Locale`
* `List<Locale> locales()` - 返回由 `Accept-Language` 头解析的所有 `Locale` 列表
* `long contentLength()` - 返回由 `Content-Length` 头解析的请求大小, 通常对 `multipart/form-data` 请求有意义
* `InputStream inputStream()` - 返回一个 `InputStream` 用于读取请求 body
* `Reader reader()` - 返回一个 `Reader` 用于读取请求 body
* `String paramVal(String name)` - 返回请求 Query 指定名字参数的值
* `String[] paramVals(String name)` - 返回请求 Query 指定名字参数的所有值
* `Iterable<String> paramNames()` - 返回所有 Query 参数名字
* `String user()` - 返回 HTTP Basic 认证用户名
* `String password()` - 返回 HTTP Basic 认证用户密码
* `static <T extends Request> T current()` - 返回当前 `H.Request` 对象
    - 在 act-1.8.8 之前该方法总是返回 `null`

**小贴士** 在 ActFramework 中大部分情况应用都不需要直接调用 `H.Request` 对象的方法.

### <a name="resp"></a>2.3 `H.Response` 响应对象

ActFramework 使用 `H.Response` 来封装 HTTP 请求，提供应用开发访问 HTTP 响应所需的方法：

* `OutputStream outputStream()` - 从响应上创建一个 `OutputStream` 以便将内容写入响应
* `Writer writer()` - 从响应上创建一个 `Writer` 以便将内容写入响应
* `Output output()` - 从响应上创建一个 `org.osgl.util.Output` 对象以便将内容写入响应
    * **注意** `Output` 同时提供了面向字节和字串的方法, 相比 `OutputStream` 和 `Writer` 更容易使用
    * **注意** 当已经调用 `outputStream()` 方法创建 `OutputStream` 之后不应该继续调用 `writer()` 或者 `output()` 方法, 反之亦然
* `PrintWriter printWriter()` - 在 `writer()` 上封装一层 `PrintWriter`
* `String characterEncoding()` - 返回在响应对象上设置的 character encoding. ActFramework 中使用下面的方式在响应对象上设置 character encoding:
    1. 通过在配置文件中指定 `encoding`
    2. 通过调用 `H.Response.contentType(String)` 方法
* `characterEncoding(String encoding)` - 在响应对象上设定 character encoding
* `contentLength(long len)` - 在响应对象上设定响应长度
* `contentType(String type)` - 在响应对象上设定 Content Type
* `initContentType(String type)` - 如果响应对象上尚未指定 Content Type 则设定指定值, 否则忽略指定值
* `contentDisposition(String filename, boolean inline)` - 在响应对象上指定 `Content-Disposition` 头的值
* `prepareDownload(String filename)` - 相当于调用 `contentDisposition(filename, false)` - 即 `Content-Disposition` 设定为 `attachment; filename="<filename>"`
* `etag(String etag)` - 在响应对象上设定 `ETag` 头
* `locale(Locale locale)` - 依据传入的 Locale 对象在响应对象上设定 `Content-Language` 头
* `Locale locale()` - 返回在响应对象上设定的 Locale
* `addCookie(H.Cookie cookie)` - 在响应对象上加入指定的 cookie
* `boolean containsHeader(String name)` - 检查响应对象上是否设定了指定的响应头
* `sendError(int statusCode, String msg)` - 发送错误响应: 返回指定的状态码以及错误消息
* `sendError(int statusCode, String msg, Object... args)` - 发送错误响应: 返回指定的状态码以及错误消息模板与参数. 消息模板和参数使用 `String.format` 方法拼接
* `sendError(int statusCode)` - 发送错误响应: 返回指定状态吗以及默认错误消息
* `sendRedirect(String location)` - 发送重定向响应
* `header(String name, String value)` - 设定响应头, 如果该响应头已经设定则替代原值
* `addHeader(String name, String value)` - 设定响应头, 如果该响应头已经设定则添加新值
* `addHeaderIfNotAdded(String name, String value)` - 设定响应头, 如果该响应头已经设定则忽略指定值
* `status(int statusCode)` - 设定响应状态码
* `status(Status status)` - 设定响应状态码
* `int statusCode()` - 返回响应对象上设定的状态码
* `writeBinary(ISObject binary)` - 在响应写入指定二进制内容, 写入完成后关闭响应
* `writeContent(ByteBuffer buffer)` - 在响应写入指定二进制内容, 写入完成后关闭响应
* `writeContent(String s)` - 在响应上写入指定字符内容, 写入完成后关闭响应
* `writeText(String content)` - 设定响应 content type 为 `text/plain` 并在响应上写入指定字符内容, 写入完成后关闭响应
* `writeHtml(String content)` - 设定响应 content type 为 `text/html` 并在响应上写入指定字符内容, 写入完成后关闭响应
* `writeJSON(String content)` - 设定响应 content type 为 `application/json` 并在响应上写入指定字符内容, 写入完成后关闭响应
* `commit()` - 向底层网络栈 (undertow) 提交响应对象
* `close()` - 关闭响应对象
* `static <T extends Response> T current()` - 返回当前线程的响应对象
    * **注意** act-1.8.8 以前该方法总是返回 `null`

**小贴士** 在 ActFramework 中大部分情况应用都不需要直接调用 `H.Response` 对象的方法.

## <a name="session_flash"></a>3. Session 与 Flash

因为 HTTP 是无状态服务, 如果要在多次请求中跟踪用户与服务的交互信息, 需要某种形式的状态存储. ActFramework 使用 `H.Session` 和 `H.Flash` 两种 Scope 类型提供请求状态存取服务. `H.Session` 和 `H.Flash` 均为应用提供一下方法:

* `put(String key, Object val)` - 将对象 `val` 用 `key` 存放在 scope 中
    - 对象 `val` 将会被转换为字串存放
* `String get(String key)` - 从 scope 中取出 `key` 对应的值
* `Set<String> keySet()` - 返回 scope 中所有的 `key`
* `Set<Map.Entry<String, String>> entrySet()` - 返回 scope 中的 (key, val) 配对集合
* `boolean containsKey(String key)` - 检查是否某个 key 在 scope 中
* `boolean contains(String key)` - `containsKey(String)` 方法的别名
* `int size()` - 返回 scope 存放的数据数目
* `remove(String key)` - 从 scope 中删除 key
* `clear()` - 从 scope 中删除所有存放的数据

和 Servlet 架构的 `HttpSession` 不同, `H.Session` 对象没有存放在服务器端, 而是以 cookie 或者 header 的方式存放在客户端. ActFramework 依据此特性实现了无状态的应用服务器架构, 支持线性增长的横向扩展. 当然这种设计对存放在 session 中的数据有一定的要求:

1. 整个 session 和 flash 的数据加起来不能超过 4k
2. 存放的数据最终会转换为字符串. 取出来的时候也只能是字符串

### <a name="session_best_practice"></a>3.1 Session 应用技巧

鉴于 Session 的特点与使用限制, 下面是一些使用 Session 的一些技巧:

* 只存放简单的数据, 例如 username, userId 等
    - 复杂数据应该存放进数据库, 或者类似 redis 这样的 KV 存储
* 尽量不要存放敏感数据, 比如密码, 电话号码, 身份证号码之类的, 因为 session cookie 虽然不能篡改但可读.
    - 如果一定要存放敏感数据, 应该打开 session 加密配置. 当然这样会带来性能上的损耗


### <a name="session_flash_diff"></a>3.2 `H.Flash` 与 `H.Session` 的区别

`H.Flash` 与 `H.Session` 的区别在于 flash 中存入的信息只保存到下一次请求处理完毕. 另外 flash 提供了几个快捷方法:

* `error(String message)` - 相当于调用 `put("error", message)`
* `String error()` - 相当于调用 `get("error")`
* `success(String message)` - 相当于调用 `put("success", message)`
* `String success()` - 相当于调用 `get("success")`

**注意** Flash 通常之用于后端模板生成的系统架构. 对于前后端分离的应用一般都没有使用 Flash 的理由.

### <a name="session_flash_usage"></a>3.3 Session/Flash 使用例子

在应用中使用 session:

<a name="s3-3a"></a>
```java
// snippet 3.3a
@PutAction("my/preference/theme")
public void setTheme(String theme, H.Session session) {
    session.put("theme", theme);
}

@GetAction("my/preference/theme")
public String getTheme(H.Session session) {
    return session.get("theme");
}
```

在应用中使用 flash:

请求处理器代码

<a name="s3-3b"></a>
```java
// snippet 3.3b
@PostAction("login")
public void login(String username, char[] password, ActionContext context) {
    if (!(authenticate(username, password))) {
        context.flash().error("authentication failed");
        redirect("/login");
    }
    context.login(username);
    redirect("/");
}
```

模板文件代码

<a name="s3-3c"></a>
```html
<!-- 例 3.3c -->
<h1>Login form</h1>
@if(_flash.error()) {
<div class="alert alert-error">@_flash.error()</div>
}
<form action="/login" method="post">
    <input name="username">
    <input type="password" name="password">
    <button>Login</button>
</form>
```
### <a name="session_config"></a>3.4 Session 配置

* `session.secure` - 指定 session cookie 的 secure 选项. 默认值: 开发模式下为 `false`; 产品模式下为 `true`
    - **注意** 仅对给予 Cookie 的 session 存储有效. 对基于 Header 的 session 存储没有意义.
* `session.ttl` - 指定 session 无活动过期时间. 默认值: `60 * 30`, 即半小时
    - **注意** 每次请求都会刷新 session 的时间戳. `session.ttl` 的意思是当用户在这段时间里和应用没有任何交互会导致 session 过期
* `session.persistent` - 是否将 session cookie 定义为长效 cookie (persistent cookie). 如果激活这个选项, 即使用户关闭浏览器, 在 `session.ttl` 到来之前 session 都不会过期. 默认值: `false`
    - **注意** 仅对给予 Cookie 的 session 存储有效. 对基于 Header 的 session 存储没有意义.
* `session.encrypt` - 是否加密 session 字串. 默认值: `false`
    - **注意** 对 JWT 输出无效

关于 Session/Flash 在框架实现方面更详尽的信息, 参考 [Session 与 Flash 的处理详解](reference/session_flash.md)

## <a name="context"></a>4. `ActionContext`

`ActionContext` 是 ActFramework 为应用提供的一个封装类, 封装了处理 HTTP 请求需要用到的数据, 包括:

* `H.Request req()` - 返回当前请求
* `H.Response resp()` - 返回当前响应
* `H.Session session()` - 返回当前 Session
* `H.Flash flash()` - 返回当前 Flash

还有一些工具方法:

* `String paramVal(String name)` - 获取请求 URL 路径参数, 查询参数, 或者 POST 表单字段.
    - **注意** `H.Request.paramVal(String)` 调用只能返回查询参数, 不能返回 URL 路径参数和 POST 表单字段
* `String session(String key)` - 相当于调用 `session().get(key)`
* `session(String key, String value)` - 相当于调用 `session().put(key, value)`
* `String sessionId()` - 相当于调用 `session().id()`
* `String flash(String key)` - 相当于调用 `flash().get(key)`
* `flash(String key, String value)` - 相当于调用 `flash().put(key, value)`
* `H.Cookie cookie(String name)` - 相当于调用 `req().cookie(name)`
* `renderArg(String name, Object val)` - 设置模板参数
* `templatePath(String templatePath)` - 设置模板路径
* ｀accept(H.Format fmt)｀ - 更改请求　`Accept` 头
* `UserAgent userAgent()` - 返回 UserAgent 对象, 由请求的 `User-Agent` 头解析得出
* `String username()` - 返回 session 中的 username 数据.
    - 拿到 username 的 key 由 `session.key.username` 配置设定, 默认为 `username`
* `boolean isLoggedIn()` - 检查是否 session 中有 username 数据
* `String body()` - 返回请求 body 内容
* `ISObject upload(String name)` - 返回指定名字的上传文件
* `forceResponseStatus(H.Status status)` - 指定响应状态码
* `login(String username)` - 将指定用户名存入 session.
* `loginAndRedirect(String username, String url)` - 将指定用户名存入 session 然后重定向到指定 URL
* `loginAndRedirectBack(String username)` - 将指定用户名存入 session 然后重定向到 login 之前的 URL
* `loginAndRedirectBack(String username, String defaultLandingUrl)` - 将指定用户名存入 session 然后重定向到 login 之前的 URL, 如果没有找到之前 URL 则重定向到 `defaultLandingUrl`
* `logout()` - 清空当前 session
* `Locale locale(boolean required)` - 返回当前请求的 Locale, 当 `required` 是 `true` 的时候, 如果当前请求没有指定 locale, 则返回系统 Locale

### <a name="context_usage"></a>4.1 使用 `ActionContext`

下面的代码演示了 `ActionContext` 在用户登陆逻辑上的应用:

<a name="s4-1a"></a>
```java
// snippet 4.1a
@PostAction("/login")
public void login(String username, char[] password, ActionContext context) {
    if (!authenticate(username, password)) {
        context.flash().error("authentication failed")
        redirect("/login");
    }
    context.loginAndRedirect(username, "/");
}
```

## <a name="controller_request-handler"></a>5. 控制器与请求处理方法

在[1. 介绍](#intro)中我们引入了控制器与请求处理方法的概念并提供了一段简单的代码演示如何使用控制器和请求处理方法来处理请求并返回响应. 本节我们会详细讨论下面几点:

1. 请求处理方法参数
2. 控制器的依赖注入
3. 单例还是多例

### <a name="request-handler_params"></a>5.1 请求处理方法参数

请求处理方法可以有 0 到多个参数, 参数数目不受限制. 通常来讲请求处理方法的参数分为两种:

1. 来自请求的数据, 包括
    * URL 路径参数
    * Query 参数
    * Form 字段
    * 上传文件
2. 系统注入对象, 包括
    * `ActionContext`
    * `H.Request`
    * `H.Response`
    * `H.Session`
    * `H.Flash`
    * `App`
    * `EventBus`
    * `JobManager`
    * 其他 App 服务
    * 数据访问对象 (DAO)
    * 各种单例 (继承 `SingletonBase`, 或者有 `@Singleton` 注解的类)
    * 所有其他在框架 IOC 容器中注册了 Provider 的类

不管哪种参数, ActFramework 不要求特别的注解, 这样可以让代码读写都更加简洁.

#### 案例 1

在 SpringMVC 代码中需要不同的注解来区分 URL 路径参数和请求 Query 参数, 如下例所示:

<a name="s5-1a"></a>
```java
// snippet 5.1a
@RequestMapping("{id}/messages")
public String handleRequest(
    @PathVariable("id") String employeeId,
    @RequestParam("months") int previousMonths,
    Model model
) {
    model.addAttribute("employee request by id for paystub for previous months : "+
                        employeeId + ", " + previousMonths);
    return "my-page";
}
```

上面的代码在 ActFramework 的表达简洁很多:

<a name="s5-1b"></a>
```java
// snippet 5.1b
@Action("{id}/messages")
public void handleRequest(@Named("id") String employeeId, int months) {
    String msg = "employee request by id for paystub for previous months : " +
              employeeId + ", " + months;
    render("my-page", msg);
}
```

#### 案例 2

Jersey 使用 `@Context` 在请求处理方法中注入系统对象:

<a name="s5-1c"></a>
```java
// snippet 5.1c
@GET
@PATH("foo")
public String foo(@Context HttpServletRequest req) {
    return req.getParameter("foo");
}
```

ActFramework 无需注解, 直接在参数列表中声明即可:

<a name="s5-1d"></a>
```java
// snippet 5.1d
@GetAction("foo")
public String foo(H.Request req) {
    return req.paramVal("foo");
}
```

#### 案例 3

ActFramework 在参数列表中混合不同的参数类型:

<a name="s5-1e"></a>
```java
// snippet 5.1e
@PostAction("/login")
public void login(String username, char[] password, ActionContext context) {
    if (!authenticate(username, password)) {
        context.flash().error("authentication failed")
        redirect("/login");
    }
    context.loginAndRedirect(username, "/");
}
```

上面代码中的 `username` 和 `password` 来自 POST 请求的表单字段, 而 `context` 则是由框架注入当前运算的 `ActionContext` 实例. ActFramework 有足够的能力分辨那些参数应该从请求中获得, 那些参数需要由 IOC 容器注入. 另外参数的位置不会影响参数注入过程.

### <a name="controller-di"></a>5.2 控制器的依赖注入

在上面的例子中我们都是将依赖对象注入到方法中. 这样做的问题是如果某个控制器有很多方法, 都需要某个依赖对象, 方法的参数就会变得复杂, 依照 DRY 原则, 我们不希望每个方法上面都重复同样的参数声明. 解决的办法是将依赖注入控制器类. 例如

<a name="s5-2a"></a>
```java
// snippet 5.2a
@UrlContext("users")
public class UserService_5_2a {

    @GetAction("{id}")
    public User findOne(@NotNull Long id, User.Dao userDao) {
        return userDao.findById(id);
    }

    @PostAction
    public User create(User user, User.Dao userDao) {
        return userDao.save(user);
    }
}
```

在上面的例子中 `userDao` 是一个数据访问对象, `UserService` 控制器中的每个方法中都需要使用这个对象, 因此上面的代码选择将 `userDao` 作为 `UserService` 控制器类的字段来注入, 而不是在每个方法中注入. 和方法参数注入不一样的地方是, 字段注入需要使用 `@javax.inject.Inject` 注解. `@Inject` 注解也可以放在构造函数上面:

<a name="s5-2b"></a>
```java
// snippet 5.2b
@UrlContext("users")
public class UserService_5_2b {

    private User.Dao userDao;

    @Inject
    public UserService(User.Dao userDao) {
        this.userDao = $.requireNotNull(userDao);
    }
    ...   
}
```

在控制器中可以注入的对象包括:

* `ActionContext`
* `H.Request`
* `H.Response`
* `H.Session`
* `H.Flash`
* `App`
* `EventBus`
* `JobManager`
* 其他 App 服务
* 数据访问对象 (DAO)
* 各种单例 (继承 `SingletonBase`, 或者有 `@Singleton` 注解的类)
* 所有其他在框架 IOC 容器中注册了 Provider 的类

### <a name="single-multi"></a>5.3 单例还是多例

当我们将依赖对象注入一个控制器类带来的一个问题是: 这个控制器是否是线程安全的, 是否单例, 还是多例. 对此 ActFramework 的回答是: 依情况而定.

* 如果注入对象本身是有状态的, 比如 `ActionContext`, `H.Request` 等等, ActFramework 会就每个请求生成一个新的控制器实例
* 如果注入对象本身是无状态的, 或者说状态不影响当前计算,比如 `EventBus`, `JobManager`, `App` 等等, ActFramework 使用控制器的单例来响应新请求.

总的来说 ActFramework 会审查控制器类的每个实例字段, 如果有任何一个字段类是有状态的,就不会使用单例来启动该控制器. 这个审查过程也包含控制器的所有父类.

下面是 ActFramework 判断一个类是否有状态的过程:

1. 如果一个类被标注为 `@Singelton` 或 `@Stateless`, 或继承自 `SingletonBase`, 则该类无状态
    * 大部分 Act App 服务类都注册为 Singleton, 包括 `EventBus`, `JobManager` 等等
2. 如果一个类没有实例字段, 或者实例字段的类本身是无状态的, 则该类无状态.

最后需要解决的问题是如何将来自第三方库的类标注为无状态. 假如某个三方库提供了线程安全的微信接口类 WeixinIntf,这个类本身没有标注为 `@Singleton`, 但作为开发我们知道这是线程安全的, 所以不希望因为这个类的注入导致控制器退出单例状态. 下面是解决办法:

<a name="s5-3a"></a>
```java
// snippet 5.3a
public class MyController_5_3a {
    @Inject @Stateless
    private IStorageService storageService;
}
```

我们在注入 `weixin` 字段的同时加上 `@Stateless` 注解, 这样 ActFramework 就知道这个字段不会影响到 `MyController` 的状态, 因此会使用单例来调用 `MyController` 请求处理方法.

## <a name="param-binding"></a>6. 请求参数绑定

ActFramework 在请求参数绑定方面提供了最大的灵活与方便. 请求参数可以来自不同的地方, 具有不同的编码方式, 绑定目标可以是请求处理方法的参数列表, 也可以是控制器类的实例字段. 在这之中, ActFramework 按照一下规则自动适配:

1. 优先级: URL 路径变量 > Query 参数 > Form 字段
2. 编码方式由请求的 `Content-Type` 头决定
3. 名字匹配: 请求参数名字和绑定对象 (请求处理方法参数或控制器字段) 名字必须一致

下面来看两个例子:

**1. 绑定到方法参数**

<a name="s6a"></a>
```java
// snippet 6a
@GetAction("/foo/{foo}")
public String test(String foo) {
    return foo;
}
```

上面的代码将路径变量 `foo` 以及请求参数 `foo` 同时绑定到了请求处理方法 `test(String foo)` 的参数 `foo` 上.

* 发送 `GET http://localhost:5460/foo/xyz` 将会得到 `xyz`
* 发送 `GET http://localhost:5460/foo?foo=abc` 将会得到 `abc`
* 发送 `GET http://localhost:5460/foo/xyz?foo=abc` 将会得到 `xyz`

**2. 绑定到控制器字段**

<a name="s6b"></a>
```java
// snippet 6b
public class MyController_6b {
    private String foo;
    @GetAction("foo/{foo}")
    public String test() {
        return foo;
    }
}
```

或者

<a name="s6c"></a>
```java
// snippet 6c
@UrlContext("/foo/{foo}")
public class MyController_6c {
    private String foo;
    @GetAction
    public String test() {
        return foo;
    }
}
```

上面两段代码和[例 6a]的代码功能是一样的, 不同之处在于参数 `foo` 被绑定到了字段而不是请求处理方法参数上.

**小贴士** 绑定到字段可以被子类继承, 缺点在于每次调用请求方法都必须启用一个新的控制器实例

### <a name="binding-resolving"></a>6.1 绑定与解析

请求参数绑定到方法参数通常会有两种情况, 

1. 方法参数为简单参数类型, 比如 int, String 等
2. 方法参数为复杂类型, 比如各种集合类型, 用户自定义的 POJO 类型等

对于第一种情况, 只需要从一个请求参数即可绑定到目标方法参数. 这个过程是一个字串到其他简单类型的转换过程, 我们称之为字串解析；与之对应的类, 我们称为 `StringValueResolver`．　下面是一个 `StringValueResolver` 的一个具体例子:

<a name="s6_1a"></a>
```java
// snippet 6.1a
public class BooleanResolver extends StringValueResolver<Boolean> {
    public Boolean resolve(String value) {
        return S.empty(value) ? Boolean.FALSE : Boolean.parseBoolean(value);
    }
}
```

上面的定义的 `BooleanResolver` 在需要绑定到布尔类型变量的时候就会被调用并用于类型转换. 已定义的解析器会被框架自动注册, 框架在参数绑定逻辑中根据目标类型自动查找需要的解析器并调用. 解析器对应用完全是透明的. **如果定义了多个从字串到某种类型的解析器, 后注册的解析器会覆盖先前注册的. 且注册顺序无法保证**

**注意** ActFramework 已经内置了所有的基本类型以及枚举型的解析器, 包括上面作为示例的 `BooleanResolver`. 应用几乎无需定义任何字串解析器.

对于第二种情况, 大部分情况都需要从多个请求参数取值最后生成一个目标方法参数. 这种多对一的解析过程我们称之为绑定；与之对应的类, 在 ActFramework 中称为　`Binder`. 下面是一个 `Binder` 的具体例子:

<a name="s6_1b"></a>
```java
// snippet 6.1b
public class EmailBinder extends Binder<String> {
    @Override
    public String resolve(String bean, String model, ParamValueProvider params) {
        String username = params.paramVal("username");
        String host = params.paramVal("host");
        return S.builder(username).append("@").append(host).toString();
    }
}
```

在上面的示例代码中, 我们看到 `Binder` 是如何从两个请求参数取值最后生成一个字串值返回. 和字串解析器不同, Binder 需要应用使用 `@Bind` 注解来调用:

<a name="s6_1c"></a>
```java
// snippet 6.1c
@GetAction("test/binder")
public String testBinder(@Bind(EmailBinder.class) String email) {
    return email;
}
```

**在本文中, 术语 "绑定" 泛指从请求参数中取值并转换为目标方法参数, 而不加区分单对单类型的解析还是多对单类型的绑定**

### <a name="data-source"></a>6.7 数据来源

**无需注解即可直接绑定的数据**

和 SpringMVC, Jersey 以及其他常见 Java Web 框架不同, ActFramework 自动匹配常见类型的请求参数和方法参数的名字而无需应用使用特殊注解, 包括:

1. URL 路径变量, 例如 `/order/{orderId}` 中的 `orderId`
2. Query 参数, 例如 `/order?orderId=aaa` 中的 `orderId`
3. Form 字段, 例如 `<input type="text" name="orderId">`
4. 上传文件, 例如 `<input type="file" name="file">`
5. Cookie 数据

**小贴士** 有可能会有方法参数和请求参数名字不匹配的情况, 这时候应该使用 `javax.inject.Named` 注解来适配, 例如:

<a name="s6_2a"></a>
```java
// snippet 6.2a
/**
 * 获得指定日期范围内创建的 order 列表. 
 * 请求示例: /orders?date_start=20180202&date_end=20180303
 */
@GetAction("orders")
public Iterable<Order> searchOrder(@Named("date_start") DateTime start, @Named("date_end") DateTime end) {
    ...
}
```

上面的代码里参数 `start` 与 `end` 和 Query 参数名字 `date_start` 与 `date_end` 都不一样, 因此使用 `@Named` 注解来适配.

**需要在参数声明前加上注解进行绑定的数据**

处于性能考虑, 对于一些不常见的数据绑定, ActFramework 要求使用特定注解来表达, 包括:

1. 请求头数据
2. Session 数据

#### <a name="cookie-data-binding"></a>6.2.1 Cookie 数据绑定

对于直接绑定数据来源列表中的 URL 路径变量和 Query 参数绑定在前面的例 6.1 和例 6.2 中已有介绍. Form 表单绑定和上传文件我们会在后面详细介绍. 这里先讲一下 `Cookie` 的绑定, 看下面的例子:

<a name="s6_2_1a"></a>
```java
// snippet 6.2.1a
@Global
@Before
public void countVisits(H.Cookie count) {
    if (null == count) {
        count = new H.Cookie("count", "1");
    } else {
        count.incr();
    }
    count.addToResponse();
}
```

上面这段代码实现访问会话的自增计数. 在这例子中我们能看到一下几点:

1. `@Global` 和 `@Before` 放在一起实现了一个全局的拦截器 - 这个我们会在后面讲到
2. 该方法有一个类型为 `H.Cookie` 的注入参数 `count`. ActFramework 会自动将请求中名为 `count` 的 cookie 注入到该参数中

**小贴士** 如果上面参数名字改为 `countCookie` 也可以, ActFramework 会自动去掉后面的 `Cookie`, 然后用前面的 `count` 去请求中拿 Cookie

**小贴士** 上面使用 Cookie 来实现自增计数只是用来演示 Cookie 参数绑定的代码, 实际项目中如果需要这样的功能建议使用 Session 来做:

<a name="s6_2_1b"></a>
```java
// snippet 6.2.1b
@Global
@Before
public void countVisits(H.Session session) {
    session.incr("count");
}
```

**注意** Cookie 参数直接绑定是 act-1.8.8 提供的特性, 1.8.8 以前的版本需要这样做:

<a name="s6_2_1c"></a>
```java
// snippet 6.2.1c
@Global
@Before
public void countVisits(H.Request req, H.Response resp) {
    H.Cookie count = req.cookie("count");
    if (null == count) {
        count = new H.Cookie("count", "1");
    } else {
        int countValue = Integer.parseInt(count.value());
        count = new H.Cookie("count", S.string(countValue + 1));
    }
    resp.addCookie(count);
}
```

#### <a name="header-data-binding"></a>6.2.2 Header 数据绑定

应用使用 `@HeaderVariable` 注解表明参数从请求头绑定:

<a name="s6_2_2a"></a>
```java
// snippet 6.2.2a
@GetAction("/header/user-agent")
public String header(@HeaderVariable("User-Agent") String userAgentString) {
    return userAgentString;
}
```

上面的代码相当于:

<a name="s6_2_2b"></a>
```java
// snippet 6.2.2b
@GetAction("/header/user-agent")
public String header(H.Request req) {
    return req.header("User-Agent");
}
```

**小贴士** 当方法参数变量名和请求头的名字可对应的时候可以省略 `@HeaderVariable` 注解里面的 `value` 参数:

<a name="s6_2_2c"></a>
```java
// snippet 6.2.2c
@GetAction("/header/user-agent")
public String header(@HeaderVariable String userAgent) {
    return userAgent;
}
```

上面代码里面方法参数名 `userAgent` 可以按照规则转换为 `User-Agent` 因此 `@HeaderVariable` 注解省略了 `value` 参数 `"User-Agent"`, 也能正常工作. 注意下面的方法参数名因为无法找出 user 和 agent 两个部分, 所有都不能正确映射到 `"User-Agent"` 字串:

* `userAgentString`
* `useragent`
* `ua`
* `agent`

下面的参数名字都能够正确映射到 `"User-Agent"` 字串:

* `userAgent`
* `user_agent`
* `UserAgent`

当然推荐使用第一种方式 `userAgent`, 因为符合 Java 标准变量命名规范.

总结一下 ActFramework 从变量名转换为请求头名的规则:

1. 按照大小写或者下划线将名字划分为不同部分
2. 每个部分格式化为首字母大写其他部分小写
3. 用 `-` (hyphen) 将所有部分串接起来

#### <a name="session-data-binding"></a>6.2.3 Session 数据绑定

应用使用 `@SessionVariable` 注解来标注某个参数需要从 Session 中绑定. 例如

<a name="s6_2_3a"></a>
```java
// snippet 6.2.3a
@GetAction("/session/username")
public String session(@SessionVariable String username) {
    return username;
}
```

上面的代码相当于:

<a name="s6_2_3b"></a>
```java
// snippet 6.2.3b
@GetAction("/session/username")
public String header(H.Session session) {
    return session.get("username");
}
```

如果 session 中的 key 和变量名不一致, 需要在 `@SessionVariable` 注解上设置 `value` 参数:

<a name="s6_2_3c"></a>
```java
// snippet 6.2.3c
@GetAction("/session/username")
public String session(@SessionVariable("user-name") String username) {
    return username;
}
```

### <a name="data-encoding"></a>6.3 请求数据编码

除了 URL 路径变量, Session, Cookie 和 Header, 其他的数据都存在不同编码方式的情况.

#### <a name="query-param-encoding"></a>6.3.1 Query 数组类型参数编码

对于下面的请求方法处理器:

<a name="s6_3_1a"></a>
```java
// snippet 6.3.1a
@GetAction("test")
public int[] test(int[] i) {
    return i;
}
```

可以采用三种不同的方式传送请求参数:

1. /test?i=1&i=2&i=3
2. /test?i=1,2,3
3. /test?i[0]=1&i[2]=3

**注意** 上面 #3 中数组元素 i[1] 缺失, 在 act-1.8.7 及以前的版本会导致 `NullPointerException`. 这个问题在 act-1.8.8 中修复了.

**小贴士** 所有数组类型的绑定也可以使用 List 来表达, 因此上面的代码也可以这样写:

<a name="s6_3_1b"></a>
```java
// snippet 6.3.1b
@GetAction("test")
public List<Integer> test(List<Integer> i) {
    return i;
}
```

#### <a name="post-form-encoding"></a>6.3.2 POST Form 编码

对于 POST 方法, 当使用 `application/x-www-form-urlencoded` 或者 `multipart/form-data` 的时候, 可以采用两种不同的方式来编码数据.

使用下面的 POJO 与控制器代码为例来讲述:

<a name="s6_3_2a"></a>
```java
// snippet 6.3.2a
public class Foo {
    public String name;
    public int[] scores;
}

public class Bar {
    public int id;
    public List<Foo> fooList;
}

public class BarService {
    @PostAction("/bars")
    Bar create(Bar bar) {
        return bar;
    }
}
```

对于发送到 `POST /bars` 的数据, 下面是两种编码的示例代码:

**1. JQuery 格式**

该方式采用 JQuery 对 Form 的序列化形式, 这也是 PHP 访问请求数据的表达方式

<a name="s6_3_2b"></a>
```html
<!-- snippet 6.3.2b -->
<form action="/bars" method="post">
    <input name="bar[id]">
    <input name="bar[fooList][0][name]">
    <input name="bar[fooList][0][scores][0]">
    <input name="bar[fooList][0][scores][1]">
    <input name="bar[fooList][1][name]">
    <input name="bar[fooList][1][scores][0]">
    <input name="bar[fooList][1][scores][1]">
    <input name="bar[fooList][1][scores][2]">
    ...
</form>
```

**2. dot 格式**

这是 ActFramework 支持的一种更方便读写的格式

<a name="s6_3_2c"></a>
```html
<!-- snippet 6.3.2c -->
<form action="/bars" method="post">
    <input name="bar.id">
    <input name="bar.fooList.0.name">
    <input name="bar.fooList.0.scores.0">
    <input name="bar.fooList.0.scores.1">
    <input name="bar.fooList.1.name]">
    <input name="bar.fooList.1.scores.0">
    <input name="bar.fooList.1.scores.1">
    <input name="bar.fooList.1.scores.2">
    ...
</form>
```

**3. 混合格式**

应用完全可以混合使用上面的两种编码方式. 下面的示例中对于数组部分采用了 JQuery 方式编码, 其他部分则使用 dot 编码方式:

<a name="s6_3_2d"></a>
```html
<!-- snippet 6.3.2d -->
<form action="/bars" method="post">
    <input name="bar.id">
    <input name="bar.fooList[0]name">
    <input name="bar.fooList[0]scores[0]">
    <input name="bar.fooList[0]scores[1]">
    <input name="bar.fooList[1]name]">
    <input name="bar.fooList[1]scores[0]">
    <input name="bar.fooList[1]scores[1]">
    <input name="bar.fooList[1]scores[2]">
    ...
</form>
```

#### <a name="post-json-encoding"></a>6.3.3 POST JSON 编码

现在越来越多的前端代码使用 AJAX 和 JSON 方式和服务器交互. ActFramework 也支持 JSON 编码的数据绑定. 对于 [示例 6.3.2a](#s6_3_2a) 的代码, 当请求的 `Content-Type` 头为 `application/json` 时, ActFramework 按照 JSON 解析请求 body 并绑定到方法参数上, 对应的一个 JSON 格式数据示例为:

<a name="s6_3_3a"></a>
```json
// snippet 6.3.3a 
{
    "id": 1,
    "fooList": [
        {
            "name": "tom",
            "scores": [
                93,
                80
            ]
        },
        {
            "name": "peter",
            "scores": [
                88,
                73
            ]
        }
    ]
}
```


### <a name="simple-type-data-binding"></a>6.4 简单数据类型绑定

除了上传文件以外的大部分请求参数原始类型都是字串, 当绑定到方法参数的时候需要转换为声明的类型. ActFramework 可以转换请求参数字串(1个或者多个)为几乎所有的类型. 这里讨论简单数据类型的绑定. 所谓简单数据类型是指一下类型:

1. 所有的基本类型, 包括 `boolean`, `byte`, `char`, `short`, `int`, `float`, `long`, `double`
2. 所有基本类型对应的包装类型, 包括 `Boolean`, `Byte`, `Character`, `Short`, `Integer`, `Float`, `Long`, `Double` 
3. 字串
4. 枚举

在 [6.1 绑定与解析](#binding-resolving) 我们已经介绍了 `StringValueResolver` 是框架用来将字串类型的请求数据解析为目标参数类型的机制. 对于上面罗列的基本类型及其包装类型, Java JDK 库已经定义了明确的和字串之间的转换逻辑, 例如 `String` -> `Boolean`, 就是通过 `Boolean.parseBoolean(String)` 进行的, 这些众所周知的基本类型转换逻辑也是框架内定义的 `StringValueResolver` 的基础, 毋庸多谈. 

这里需要讲一下两个问题: 第一, 空值问题, 即当请求中没有数据, 目标参数如何设定值；第二错误值问题, 当请求数据无法转换到目标类型如何处理.

#### <a name="null-val"></a>6.4.1 空值处理

ActFramework 按照一下规则处理空值:

1. 对于所有对象,包括包装类型, 字串, 枚举, 集合类型以及其他对象类型, 如果请求中找不到绑定数据, 统一返回 `null`
2. 对于基本类型, 例如 boolean, int 等, 如果请求中找不到绑定数据, 按照字段默认值填入绑定目标参数.

基本类型默认值列表

<a name="t6_4_1a"></a>
| 类型 | 默认值 |
| --- | ---: |
| boolean | `falase` |
| byte | `0` |
| char | `'\0'` |
| short | `0` |
| int | `0` |
| float | `0f` |
| long | `0l` |
| double | `0d` |

#### <a name="bad-data"></a>6.4.2 错误数据处理

当用户请求传入错误数据, 例如需要绑定的数据类型是 `int`, 传入的数据是 `xyz`, 这时候 ActFramework 会返回一个 `400 Bad request` 响应给请求发起方

#### <a name="enum-binding"></a>6.4.3 枚举类型的绑定

当目标类型为 `Enum` 的时候, ActFramework 将请求数据中的字串和 enum 名字想匹配来查找绑定的 enum 值. 有两种绑定方式:

**1. 基于 Keyword 变化形式的非精确匹配**

假设有下面的 enum 定义:

<a name="s6_4_3a"></a>
```java
// Snippet 6.4.3a
public enum TestEnum {FOO_BAR}
```

下面的字串都能解析到 `TestEnum.FOO_BAR`:

* `FOO_BAR` - 原始匹配
* `Foo-Bar` - HTTP-Header 格式
* `Foo.Bar` - dotted 格式
* `foo-bar` - hyphenated 格式
* `foo_bar` - 下划线格式
* `FooBar` - 驼峰格式
* `fooBar` - Java 变量格式

非精确匹配是 ActFramework 默认 enum 解析方式

**2. 精确匹配**

精确匹配要求字串数据和 enum 的名字完全相同. 对于上面的例子, 只有 `"FOO_BAR"` 才能解析到 `TestEnum.FOO_BAR`

打开精确匹配的方式是在配置文件中加入下面配置:

```
enum.resolving.exact_match=false
```

当枚举匹配不成功的时候 ActFramework 将返回 `400 BadRequest` 响应. 当字串为 `null` 的时候不认定为匹配失败, 而是直接注入 `null` 到绑定参数上

**注意** act-1.8.8 之前的枚举解析

1. act-1.8.8 之前的枚举解析不支持非精确匹配, 但是支持大小写不敏感匹配.
2. act-1.8.8 之前匹配不成功会注入 `null` 到绑定参数, 不会返回 `400 Bad Request` 响应

### <a name="array-collection-binding"></a>6.5 数组与集合类型绑定

ActFramework 支持数组与集合类型的数据绑定. 对于数组, 列表(List) 和集合(Set), 请求数据的形式是一致的；对于 Map 类型的数据绑定, 请求参数的编码形式会有所变化. 这里我们介绍简单类型的数组与集合类型绑定, 对于复杂类型, 比如 POJO 的数组和集合类型在后面[POJO 绑定](#pojo-binding)会谈及

#### <a name="primitive-array-binding"></a>6.5.1 基本类型数组绑定

ActFramework 支持除 char 以外所有的基本类型数组绑定. char 数组绑定不被支持因为和字串绑定冲突. 我们以 int[] 类型来说明 ActFramework 对基本数据类型数组绑定的支持. 假设我们有下面的请求处理方法:

<a name="s6_5_1a"></a>
```java
// Snippet 6.5.1a
@Action("/test")
public int[] test(int[] n) {
    return n;
}
```

**GET 请求编码**

在[6.3.1 Query 数组类型参数编码](#query-param-encoding)中已经介绍过基本数组类型在 GET 请求中的三种编码方式. 这里就不再复述.

**POST 请求编码**

方式一 (Form 表单)

<a name="s6_5_1b"></a>
```html
<!-- snippet 6.5.1b -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="n" value="1,2,3">
</form>
```

方式二 (Form 表单)

<a name="s6_5_1c"></a>
```html
<!-- snippet 6.5.1c -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="n" value="1">
<input name="n" value="2">
<input name="n" value="3">
</form>
```

方式三 (Form 表单)

<a name="s6_5_1d"></a>
```html
<!-- snippet 6.5.1d -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="n[]" value="1">
<input name="n[]" value="2">
<input name="n[]" value="3">
</form>
```

方式四 (Form 表单)

<a name="s6_5_1e"></a>
```html
<!-- snippet 6.5.1e -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="n[0]" value="1">
<input name="n[2]" value="3">
</form>
```

**注意** 这里缺失 `n[1]` 的赋值, 因此解析出的 `int[]` 为: `{1,0,3}`

方式五 (JSON 数据)

<a name="s6_5_1f"></a>
```json
[1, 2, 3]
```

这种方式通常都是前端采用 AJAX 请求向服务端发送 JSON 数据, 需要请求的 `Content-Type` 头置为 `application/json` 才能正确解析

##### <a name="wrap-array-binding"></a>6.5.2 包装类型数组绑定

下面是使用包装数据类型绑定的请求处理方法演示代码:

<a name="s6_5_2a"></a>
```java
// Snippet 6.5.2a
@Action("/test")
public Integer[] test(Integer[] n) {
    return n;
}
```

包装类型数组和基本数据类型数组的处理几乎完全一致, 唯一不同的地方在于对空值(`null`)的处理. 基本数据类型的空值采用默认值填入, 包装类型的空值也填入空值. 因此对于上面[方法四](#s6_5_1e)的情况, 绑定的 `Integer[] n` 的值为: `{1, null, 3}`. 需要特别注意的是这种数组目前无法转换为合法的 JSON 字串, 应用开发人员应该小心处理.

字串和枚举数组和包装类型数组的处理类似, 无需多言.

#### <a name="list-set-binding"></a>6.5.3 List 和 Set 绑定

List 和 Set 绑定和包装类型数组绑定的处理与编码方式完全一样. 下面是使用 List/Set 类型的处理方法演示代码:

<a name="s6_5_3a"></a>
```java
// Snippet 6.5.3a
@Action("/test")
public void test(List<Integer> intList, Set<String> stringSet) {
    render(intList, stringSet);
}
```

采用[方式二](#s6_5_1c)来编码的例子:

<a name="s6_5_3b"></a>
```html
<!-- snippet 6.5.3b -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="intList" value="1">
<input name="intList" value="2">
<input name="intList" value="3">
<input name="stringSet" value="foo">
<input name="stringSet" value="bar">
</form>
```

获得的结果用 JSON 表达应该是: `{"stringSet":["bar","foo"],"intList":[1,2,3]}`

[6.5.1 节](#primitive-array-binding) 中提到的其他编码方式也都可以使用

#### <a name="map-binding"></a>6.5.4 Map 绑定

ActFramework 支持 Map 类型的绑定. Map 的 key 必须能从字串直接解析 (能找到对应的 `StringValueResolver`), Map 的 value 可以是任何类型. 我们这里先讨论 value 为基本类型的情况. 对于 value 为任何类型的讨论, 放到[6.7 POJO 绑定]详细讲述.

对于下面的请求处理方法:

<a name="s6_5_4a"></a>
```java
// 6.5.4a
@Action("/test/654")
public Map<String, Integer> test(Map<String, Integer> map) {
    return map;
}
```

假设需要获得 `{"a":1,"b":2}` 的结果下面是 GET 和 POST 请求的编码方式:

<a name="s6_5_4b"></a>
**GET 请求编码**

```
/test/654?map[a]=1&map[b]=2
```

**POST 请求编码**

方式一

<a name="s6_5_4c"></a>
```html
<!-- snippet 6.5.4c -->
<form action="/test/654" method="post" enctype="application/x-www-form-urlencoded">
<input name="map" value="a=1,b=2">
</form>
```

方式二

<a name="s6_5_4d"></a>
```html
<!-- snippet 6.5.4d -->
<form action="/test/654" method="post" enctype="application/x-www-form-urlencoded">
<input name="map[a]" value="1">
<input name="map[b]" value="2">
</form>
```

ActFramework 也支持以其他类型作为 Key, 比如:

<a name="s6_5_4e"></a>
```java
// Snippet 6.5.4e
@Action("/test/654")
public Map<Integer, String> test(Map<Integer, String> map) {
    return map;
}
```

只要请求发送的数据能够正确进行类型转换, ActFramework 都能完成绑定. 对于 GET 请求, 正确的请求参数为: `/test/654?1=a&2=b`. 对于 POST 请求也类似. 

**如非必要, 不推荐使用字串以外的类型作为 Map 的 key**, 因为这样的 Map 数据不能生成合法的 JSON 字串.

### <a name="date-data-binding"></a>6.6 日期数据绑定

日期数据类型相对比较复杂, ActFramework 支持以下几种日期类型:

* `java.util.Date`
* `java.sql.Date`
* `java.sql.Timestamp`
* `org.joda.time.DateTime`
* `org.joda.time.LocalDateTime`
* `org.joda.time.LocalDate`
* `org.joda.time.LocalTime`

**注意** java8 DateTime 系列类型目前暂不支持

**ActFramework 推荐在系统中优先使用 `joda` 包的时期时间类型**

#### <a name="date-format"></a>6.6.1 日期格式

ActFramework 支持三种日期格式的配置:

* `fmt.date` - 日期格式, 适用于 `org.joda.time.LocalDate` 类型数据
* `fmt.time` - 时间格式, 适用于 `org.joda.time.LocalTime` 类型数据
* `fmt.date-time` - 日期及时间格式, , 适用于一下类型数据:
	- `java.util.Date`
	- `java.sql.Date`
	- `java.sql.Timestamp`
	- `org.joda.time.DateTime`
	- `org.joda.time.LocalDateTime`

三种格式的默认配置都是 `medium`, 对应 `java.text.DateFormat.MEDIUM`; 三种格式通用的配置还包括:

* `long`
* `short`

除了 `short`, `medium` 和 `long` 之外, 还可以配置相应的模式 (Pattern), 例如:

* `fmt.date=yyyy年MM月dd日`
* `fmt.time=HH:mm`
* `fmt.date-time=yyyy年MM月dd日 HH:mm`

**注意** 如果没有配置特定模式, 日期和时间的模式会随当前应用服务器的语言时区而变化. 比如澳洲的 `medium` 日期模式为: `dd/MM/yyyy`, 而中国的 `medium` 日期模式为 `yyyy-M-d`

当请求发送日期数据时,其格式必须符合系统配置(默认或指定). 对于下面的请求处理方法:

<a name="s6_6_1a"></a>
```java
// Snippet 6.6.1a
@Action("test/661/local-date")
public LocalDate testLocalDate(LocalDate date) {
	return date;
}
```

如果应用在中国大陆, 发送的 GET 请求应该是 `/test/661/local-date?date=2018-4-23`, POST 请求的表单字段值也应类似.

#### <a name="date-format-localization"></a>6.6.2 日期格式本地化

当设置了 `i18n=true` 打开 ActFramework 的国际化支持后, 日期和时间的格式处理会更加复杂. 首先系统会从请求的 `Accept-Language` 解析用户端的 `Locale` 并存储在 `ActionContext` 中. 

在接受日期或者时间参数的时候, 不再仅仅通过 `fmt.date`, `fmt.time` 以及 `fmt.date-time` 设置来确定日期时间字串的模式, 而是依据当前请求的 `Locale` 来判断应该使用的模式. ActFramework 支持对特定的语言时区定义日期时间格式, 如下例所示:

<a name="s6_6_2a"></a>
```
# snippet 6.6.2a
fmt.zh-cn.date=yyyy-M-d
fmt.zh-tw.date=yyyy/M/d
```

在没有设置本地日期格式的情况下, 框架默认使用 `DateFormat.MEDIUM` 来获取相应的日期时间格式

#### <a name="date-format-specified"></a>6.6.3 在绑定参数上指定日期格式

有的时候对于特定的请求处理方法需要使用和全局设定不同的日期/时间模式, 这时候可以使用 `@act.data.annotation.DateTimeFormat` 注解, 如下例所示:

<a name="s6_6_3a"></a>
```java
// Snippet 6.6.3a
@Action("test/663/local-date")
public LocalDate testCustomeDatePattern(@DateTimeFormat("yy-M-d") LocalDate date) {
	return date;
}
```

### <a name="pojo-binding"></a>6.7 POJO 绑定

ActFramework 支持 POJO 绑定. 在[6.3.2 POST Form 编码](#post-form-encoding) 我们已经通过实例讲述了 POJO 对象 POST Form 编码的两种方式: JQuery 和 dot 格式. 下面采用 jQuery 方式来详细介绍 POJO 的绑定, 包括:

* 单个 POJO 对象绑定
* POJO 数组或列表绑定
* POJO Map绑定

我们在下面的 POJO 绑定讨论中使用如下 POJO 对象:

<a name="s6_7a"></a>
```java
// snipeet 6.7a
public class Address {
	public String street;
	public int postCode;
}

public class Employee {
	public int no;
	public String name;
	public Address address;
}
```

#### <a name="single-pojo-binding"></a>6.7.1 单个 POJO 绑定

请求处理方法:

<a name="s6_7_1a"></a>
```java
// snipeet 6.7.1a
@Action("/test/671")
public Employee pojo671(Employee emp) {
	return emp;
}
```

GET 编码:

<a name="s6_7_1b"></a>
```
GET /test/671?emp[no]=123&emp[name]=Bob&emp[address][street]=King%20st&emp[address][postCode]1234
```

POST 编码:

<a name="s6_7_1c"></a>
```html
<!-- snippet 6.7.1c -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="emp[no]" value="123">
<input name="emp[name]" value="Bob">
<input name="emp[address][street]" value="King st">
<input name="emp[address[postCode]" value="1234">
</form>
```

#### <a name="list-pojo-binding"></a>6.7.2 POJO 数组或列表绑定

请求处理方法:

<a name="s6_7_2a"></a>
```java
// snipeet 6.7.2a
@Action("/test/672")
public List<Employee> pojo672(List<Employee> empList) {
	return empList;
}
```

GET 编码:

<a name="s6_7_2b"></a>
```
GET /test/672?empList[0][no]=123&empList[0][name]=Bob&empList[0][address][street]=King%20st&empList[0][address][postCode]=1234
```

POST 编码:

<a name="s6_7_2c"></a>
```html
<!-- snippet 6.7.2c -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="empList[0][no]" value="123">
<input name="empList[0][name]" value="Bob">
<input name="empList[0][address][street]" value="King st">
<input name="empList[0][address[postCode]" value="1234">
</form>
```

#### <a name="map-pojo-binding"></a>6.7.3 POJO Map 绑定

请求处理方法:

<a name="s6_7_3a"></a>
```java
// snipeet 6.7.3a
@Action("/test/673")
public Map<String, Employee> pojo673(Map<String, Employee> empMap) {
	return empMap;
}
```

GET 编码:

<a name="s6_7_3b"></a>
```
GET /test/673?empMap[bob][no]=123&empMap[bob][name]=Bob&empMap[bob][address][street]=King%20st&empMap[bob][address][postCode]=1234
```

POST 编码:

<a name="s6_7_3c"></a>
```html
<!-- snippet 6.7.3c -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="empMap[bob][no]" value="123">
<input name="empMap[bob][name]" value="Bob">
<input name="empMap[bob][address][street]" value="King st">
<input name="empMap[bob][address[postCode]" value="1234">
</form>
```

### <a name="file-upload"></a>6.8 上传文件

先看一个简单的示例:

上传文件表单

<a name="s6_8a"></a>
```html
<!-- snippet 6.8a -->
<form action="/test/file" method="post" enctype="multipart/form-data">
	<input type="file" name="upload">
</form>
```

请求处理方法:

<a name="s6_8b"></a>
```java
// Snippet 6.8b
@PostAction("/test/file")
public void upload(File upload) {
	// save uploaded file
}
```

上面的代码使用 `java.io.File` 来声明 `upload` 参数的类型, 没有问题. 不过 ActFramework 推荐使用 `org.osgl.storage.ISObject` 来替代 `java.io.File`:

<a name="s6_8c"></a>
```java
// Snippet 6.8c
@PostAction("/test/file")
public void upload(ISObject upload) {
	// save uploaded file
}
```

使用 `ISObject` 的好处在于:

1. 可以方便地转换为其他类型, 包括 `String`, `byte[]`, `InputStream`
2. 可以直接被 osgl-storage 中定义的 `IStorageService` 存储, 包括 
	- `FileStorage`: 本地文件系统
	- `S3Storage`: AWS S3 存储
	- `BlobStorage`: Azure Blob 存储
3. 当上传文件长度小于某个特定阀值 (默认 10K) 时, 不会在服务器上产生临时文件触发 IO 操作

#### <a name="upload-in-memory-cache"> 6.8.1 上传文件内存缓存

通常来讲框架在将上传文件传递给用户应用的时候会事先生成临时文件, 方便应用对文件的各种处理, 比如存入某个永久存储, 或者进行图片剪裁等操作. 因为有了 osgl-storage 库, ActFramework 在此基础上提供了内存缓存的概念. 刚才讲到使用 `ISObject` 的好处第 3 项, 当上传文件长度小于某个阀值时, ActFramework 将生成内部实现为 `byte[]` 的 `ISObject` 实现, 这样无需因为临时文件而产生 IO 操作.

这个阀值的配置示例如下:

<a name="s6_8_1a"></a>
```
upload.in_memory.threshold=1024 * 100
```

上面将阀值配置为 `100k`, 即所有长度小于 100k 的上传文件都不会因为临时文件产生 IO 操作. 前提是应用使用了 `ISObject` 来声明上传文件, 而不是 `File`.

#### <a name="upload-base64"> 6.8.2 上传 BASE 64 编码

TBD

### <a name="customize-data-binding"></a>6.9 自定义数据绑定

ActFramework 提供了强大的请求参数绑定支持, 应用几乎没有定义自己的 `StringValueResolver` 或 `Binder` 的需要. 

#### <a name="customize-resolver"></a>6.9.1 自定义 `StringValueResolver`

假设应用对某种类型特殊编码方式, 可以采用自定义 `StringValueResolver`

自定义类型:

<a name="s6_9_1a"></a>
```java
// snippet 6.9.1a
public class Foo {
	public int id;
	public String name;
}
```

对于上面的类型 `Foo` 假设应用使用的编码方式为 `<id>-<name>`, 例如 `123-foobar`, 自定义的 `StringValueResolver` 为:

<a name="s6_9_1b"></a>
```java
// snippet 6.9.1b
public static class FooResolver extends StringValueResolver<Foo> {
	@Override
	public Foo resolve(String s) {
		S.Pair pair = S.binarySplit(s, '-');
		int id = $.convert(pair.left()).toInt();
		String name = pair.right();
		return new Foo(id, name);
	}
}
```

ActFramework 会自动注册 `FooResolver`, 并对所有的 `Foo` 对象尝试使用该 `resolver` 来解析, 下面是示例代码:

<a name="s6_9_1c"></a>
```java
// snippet 6.9.1c
@GetAction("1/c")
@JsonView
public Foo testFooResolver(Foo foo) {
	return foo;
}
```

向 `http://localhost:5460/6/9/1/c?foo=12-abc` 发出请求得到下面的响应:

<a name="s6_9_1d"></a>
```json
{
    "id": 12, 
    "name": "abc"
}
```

#### <a name="customize-binder"></a>6.9.2 自定义 Binder

自定义 Binder 的方法和例子参见 [6.1 绑定与解析](#binding-resolving)

### <a name="data-validation"></a>6.10 绑定参数校验

TBD

## <a name="return-response"></a>7 返回响应
* [7 返回响应]
    * [7.1 返回数据]
        * [7.1.1 返回模板]
        * [7.1.2 返回 JSON 响应]
        * [4.1.3 文件下载]
    * [7.2 返回状态]
        * [7.2.1 默认状态返回规则]
            * [7.2.1.1 200 Okay]
            * [7.2.1.2 201 Created]
            * [7.2.1.3 404 Not Found]
            * [7.2.1.4 从 Java 异常映射为 HTTP 错误状态]
        * [7.2.1 指定返回状态]
        * [7.2.3 自定义错误页面]
    * [7.3 设定 HTTP Header]
        * [7.3.1 Content-Type]
        * [7.3.2 设定其他 HTTP Header]
* [8 异步返回]

Act 支持 JSR 303 Bean 校验, 如下例所示:

<a name="s6_10a"></a>
```java
@GetAction("notNull")
public Result notNull(@NotNull String value) {
	if (context.hasViolation()) {
		return text("Error(s): \n%s", context.violationMessage());
	}
	return text("not null success with %s", fmt);
}
```

当发送请求给上面的 `notNull` 端口没有指定 `value` 的时候, 将会得到如下响应:

<a name="s6_10b"></a>
```
Error(s): 
value: may not be null
```



---------------------- 分割线 -------------------------

下面的内容需要重写

**小贴士** 尽管控制器不需要继承任何类，ActFramework推荐你的控制器继承`act.controller.Controller.Util`类，这样你可以在你的控制器中方便的使用各种工具方法。当你的控制器已经继承了其他类的时候，你可以使用`import static`来实现相同的功能：

1. 继承 `act.controller.Controller.Util`:

    ```java
    import act.Controller;
    public class MyController extends Controller.Util {
        ...
    }
    ```

1. import static:

    ```java
    import static act.Controller.Util.*;
    public class MyController extends Controller.Util {
        ...
    }
    ```

**注意** 本页下面的代码例子都假设控制器继承了`Controller.Util`类


## <a name="parameter"></a>获得请求参数

ActFramework从以下来源自动填充请求处理方法参数：

1. URL路径参数
1. 查询参数
1. 表单参数

```java
@PutAction("/customer/{customerId}/order/{orderId}")
public void updateOrderAmount(String customerId, String orderId, int amount) {
    ...
}
```

如上例所示URL路径变量`customerId`和`orderId`被自动填充为请求处理方法参数，参数`amount`则来自查询参数或者表单参数

### <a name="binding"></a>POJO绑定

ActFramework可以将复杂的表单变量绑定到域模型对象（POJO实例）. 假设你有如下类:

```java
public class Order {
    private String id;
    private String customerId;
    private List<Item> items;

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        private String description;
        private int amount;

        public String getDescription() {
            return description;
        }

        public void setDecsription(String desc) {
            this.description = desc;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }
}
```

你的订单表单如下:

```html
<form action="/customer/@customer.getId()/order" method="POST">
<div class="line-item">
    <span class="desc"><input name="order[items][][description]"></span>
    <span class="amount"><input name="order[items][][amount]"></span>
</div>
<div class="line-item">
    <span class="desc"><input name="order[items][][description]"></span>
    <span class="amount"><input name="order[items][][amount]"></span>
</div>
<div class="line-item">
    <span class="desc"><input name="order[items][][description]"></span>
    <span class="amount"><input name="order[items][][amount]"></span>
</div>
...
</form>
```

你可以在请求处理方法中直接声明`Order`类型变量:

```java
@PostAction("/customer/{customerId}/order")
public void createOrder(String customerId, Order order) {
    order.setCustomerId(customerId);
    dao.save(order);
}
```

### <a name="json-param"></a>JSON内容绑定

上面的`createOrder`请求处理方法也可以从类似下面的JSON内容绑定:

```JSON
{
    "items": [
        {
            "description": "item 1",
            "amount": 10000
        },
        {
            "description": "item 2",
            "amount": 12300
        },
        ...
    ]
}
```

**Note** ActFramework暂不支持从XML内容的绑定

### <a name="file"></a>获取上传文件

假设你的文件上传表单如下:

```html
<form method="POST" enctype="multipart/form-data" action="/upload">
    Please specify file to upload: <input type="file" name="myfile"><br />
    <input type="submit" value="submit">
</form>
```

你可以直接在你的请求处理方法中申明`File`类型参数：

```java
public void handleUpload(File myfile) {
    ...
}
```


## <a name="response"></a>发回响应

ActFramework提供多种不同的方法让开发人员指定响应内容，每种方式都简单易用。

### <a name="implicity-200"></a>自动返回200 Okay

当请求处理方法方法没有返回类型，也没有抛出异常ActFramework自动发回代码为`200 Okay`的空响应。如果有[相应的模板定义](templating.md#location)，则根据模板生成返回内容。自动返回可以让一些PUT和POST的请求处理方法非常简练：

```java
@PostAction("/order")
public void createOrder(Order order) {
    orderService.save(order);
}
```

### <a name="explicity-200"></a>程序中制定返回200 Okay

对于有轻微强迫症的猿们，一定要通过程序显式返回200 Okay才舒服，ActFramework提供两种方式：

1. 返回`org.osgl.mvc.result.Result`

    ```java
    @PostAction("/order")
    public Result createOrder(Order order) {
        orderService.save(order);
        return ok();
        // 或者 return new Ok();
    }
    ```

1. 抛出`org.osgl.mvc.result.Result`

    ```java
    @PostAction("/order")
    public void createOrder(Order order) {
        orderService.save(order);
        throw ok();
        // or throw new Ok();
    }
    ```

    你甚至可以将`Result`隐式抛出:

    ```java
    @PostAction("/order")
    public void createOrder(Order order) {
        orderService.save(order);
        ok();
    }
    ```

**注意** ActFramework会对控制器的响应方法做字节码增强，当某一条语句返回`Result`类型，但没有返回上级调用，框架会自动将Result作为异常抛出，这就是上例可以简单写一句`ok()`的原因所在

### <a name="return-404"></a>返回404 Not Found

对于http服务来讲，当请求的资源无法找到的时候服务器应该返回`404 NotFound`响应。ActFramework程序可以使用如下方式返回`404`错误：

```java
@GetAction("/order/{orderId}")
public Order getOrder(String orderId) {
    Order order = dao.findById(orderId);
    if (null == order) {
        throw new NotFound();
    }
}
```

对上述代码的一种更为简洁的表述为：

```java
@GetAction("/order/{orderId}")
public Order getOrder(String orderId) {
    Order order = dao.findById(orderId);
    notFoundIfNull(order);
}
```

而极简方式则为：

```java
@GetAction("/order/{orderId}")
public Order getOrder(String orderId) {
    return dao.findById(orderId);
}
```

你没有看错，没有任何语句检查返回订单对象是否为空。ActFramework将自动检查，如果请求处理方法返回空值，且方法申明有返回类型，则自动返回`404`错误

### <a name="return-400"></a>返回其他错误

下面的代码演示了如何返回其他错误类型：

```java
public void foo(int status) {
    badRequestIf(400 == status);
    unauthorizedIf(401 == status);
    forbiddenIf(403 == status);
    notFoundIf(404 == status);
    conflictIf(409 == status);
    // none of the above?
    throw ActServerError.of(status);
}
```

### <a name="exception-mapping"></a>从Java异常自动映射为HTTP错误响应

你的代码有异常抛出嘛? ActFramework会自动将它们映射为错误响应：

1. `IllegalArgumentException` -> 400 Bad Request
1. `IndexOutOfBoundsException` -> 400 Bad Request
1. `IllegalStateException` -> 409 Conflict
1. `UnsupportedOperationException` -> 501 Not Implemented
1. Other uncaught exception -> 500 Internal Error

### <a name="return-data"></a>返回数据

ActFramework允许返回任何类型的数据，并根据上下文情况判断最终返回格式。当请求的`Accept`http头设置为`application/json`的时候下面两组代码的效果是完全相同的:

```java
@GetAction("/order/{orderId}")
public Order getOrder(String orderId) {
    return dao.findById(orderId);
}
```

```java
@GetAction("/order/{orderId}")
public Result getOrder(String orderId) {
    Order order = orderService.findById(orderId);
    return renderJSON(order);
}
```

推荐使用第一种方式，原因在于：

1. 更加简洁
1. 当请求要求不同的返回格式的时候，ActFramework能够满足要求

### <a name="render-template"></a>使用模板

传统的MVC应用几乎都会设计模板。ActFramework支持下面三种方式来调用模板:

1. 隐式模板调用

    对于任何请求处理方法，如果定义了相应的模板文件，则总是启用模板文件来生成响应。

    如果请求处理方法返回某个对象，该对象可以在模板中使用`result`参数来引用

1. 显式模板调用

    ```java
    @GetAction("/order/editForm")
    public Result orderEditForm(String orderId) {
        Order order = orderService.findById(orderId);
        boolean hasWritePermission = ...;
        return render(order, hasWritePermission);
    }
    ```
    以上代码明确调用模板来生成响应结果。在调用模板的时候传进两个参数`order`和`hasWritePermission`，这两个参数可以在模板中被直接引用

1. 显式调用模板并制定路径

    ```java
    @GetAction("/order/editForm")
    public Result orderEditForm(String orderId) {
        Order order = orderService.findById(orderId);
        boolean hasWritePermission = ...;
        return renderTemplate("/myTemplateRoot/orderForm.html", order, hasWritePermission);
    }
    ```

    在上例中传递给`renderTemplate`的第一个参数是一个字串量(String literal)，而不是一个变量。在这种情况下，ActFramework将其作为模板路径处理，其他的参数则继续作为模板参数处理。


### <a name="render-binary"></a>发回二进制数据

1. 发回嵌入二进制流（例如图片或者嵌入式PDF）

    ```java
    @GetAction("/user/{userId}/avatar")
    public Result getAvatar(String userId) {
        User user = userDao.findById(userId);
        return binary(user.getAvatarFile());
    }
    ```

2. 发回下载文件

    ```java
    @GetAction("/invoice/{id}/photoCopy")
    public Result downloadInvoicePhotoCopy(String id) {
        Invoice invoice = dao.findById(id);
        return download(invoice.getPhoto());
    }
    ```

## <a name="content-negotiation"></a>内容格式

ActFramework检测请求的`Accept`头并根据其设定生成不同的响应内容

```java
@GetAction("/person/{id}")
public Person getPerson(String id) {
    return dao.findById(id);
}
```

对于上例代码，当`Accept`头设置为"application/json"的时候, 响应是JSON体:

```json
{
  "firstName": "John",
  "lastName": "Smith"
}
```

当设置为`text/html`或`text/plain`的时候, 响应将调用`Person.toString()`方法，生成下面的内容

```
John Smith
```

你甚至可以为请求处理方法定义多个不同的后缀名的模板文件。

`getPerson.html`

```
@args Person result
<div>
  <span class="label">First name</span><span>@result.getFirstName()</span>
</div>
<div>
  <span class="label">Last name</span><span>@result.getLastName()</span>
</div>
```

`getPerson.json`

```
@args Person result
{
    "firstName": "@result.getFirstName()",
    "lastName: "@result.getLastName()"
}
```

ActFramework根据`Accept`头的内容来选择适合的模板文件

## 会话和快闪对象

如果需要在多个HTTP请求之间保存数据, 可以将它们存入会话(Session)或者快闪(Flash)中. 在Session中的数据在整个用户会话过程中均可使用. 在Flash中的数据仅维持到下一个请求.

一个很重要的概念是Session以及Flash数据并非存在服务器中,而是通过Cookie机制在每个接下来的HTTP请求里携带这些数据. 因此数据大小是有限制的(最多4KB)而且只能保存为字符串

当然, ActFramework使用了应用配置的密匙来对cookie内容进行签名以确保其不会被篡改, 否则就会失效. 另外ActFramework的session并不是用来当做缓存(Cache)使用的. 如果你需要缓存一些和Session相关的结构化数据, 可以调用`Session.cache()` APIs. 例如:


```java
@GetAction
public void index(H.Session session, Message.Dao dao) {
    List<String> messages = session.cached("messages");
    if (null == messages) {
        // Cache miss
        messages = dao.findByUser(me);
        session.cacheFor30Min("messages", messages);
    }
    render(messages);
}
```

Session数据在用户关闭浏览器之后即失效, 除非你打开了[session.persistent](configuration#session_persistent)配置
需要注意的一点, 虽然都能保存结构化数据, 但是缓存和传统的Servlet HTTP Session对象有不同的语义. 你不能指望数据总是存在缓存中. 因此应用必须处理缓存失效的情况. 这也确保了你的应用完全无状态化.

## 总结

本章讲述了以下概念：

1. 控制器`Controller`和请求处理方法`Action handler`的概念
1. 如何写一个简单的控制器
1. 如何获取请求参数以及POJO绑定
1. 如何发回不同的响应代码
1. 如何返回数据
1. 如何隐式或显式的指定响应模板
1. 如何返回二进制流或下载文档
1. `Accept`头对ActFramework行为的影响
1. 如何使用回话和快闪对象

[返回目录](index.md)
