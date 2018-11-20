# <a name="chapter_controller">第五章 控制器, 请求处理方法与响应返回


控制器 (Controller) 和响应返回是 MVC 中的 "C" 和 "V" 部分, 也是整个框架的核心. 下面是一个简单的控制器代码:

<a name="s0a"></a>

```java
// snippet 0a
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

## <a name="req_resp"></a>1. 请求与响应

HTTP 请求与响应是 Web 应用的输入和输出, 是所有 web 编程框架的核心数据结构。

Servlet 架构使用 `HttpServletRequest` 和 `HttpServletResponse` 两个类来封装 HTTP 请求与响应. 在 Servlet 刚刚开始的时候, Java Web 编程是围绕这两个类进行的, 应用开发人员必须手动从 `HttpServletRequest` 中获取请求参数, Header 变量等信息, 然后手动讲字串拼接并输出到 `HttpServletResponse` 响应对象提供的 `OutputStream`. 这并不是一种很好的开发体验. 后来慢慢出现了 JSP, Velocity 等模板技术, 让输出的处理变得非常方便. 但是请求参数解析的问题依然存在, 知道后来出现的 SpringMVC, PlayFramework 等框架提供了参数绑定特性. ActFramework 作为后来者, 立据前者肩头, 无疑在这方面提供更强大的支持, 让 Web 编程过程变得前所未有的简便.

虽然提供了各种高层封装手段, 在少数情况下, 开发人员可能还是需要直接对请求和输出进行操作. ActFramework 使用 [osgl-http](https://github.com/osglworks/java-http) 提供的 `H.Request` 类来封装 HTTP 请求, `H.Response` 则封装了 HTTP 响应对象。

### <a name="req_resp_usage"></a>1.1 使用请求与响应

在应用中使用请求与响应的示例代码:

<a name="s1-1a"></a>

```java
// snippet 1.1a
@GetAction("echo/a")
public void echo_a(H.Request req, H.Response resp) {
    String message = req.paramVal("message");
    resp.header("Content-Type", "text/plain").output().append(message).close();
}
```


**小贴士** ActFramework 对于输出响应有更多的表达方式, 上面的代码可以简化为:

<a name="s1-1b"></a>

```java
// snippet 1.1b
@GetAction("echo/b")
public void echo_b(H.Request req, H.Response resp) {
    String message = req.paramVal("message");
    resp.writeText(message);
}
```

而更简单的方式则是完全不使用 Request 和 Response 对象:

<a name="s1-1c"></a>
```java
// snippet 1.1c
import static Controller.Util.renderText;
...

@GetAction("echo/c")
public void echo_c(String message) {
    renderText(message);
}
```

甚至可以这样:

<a name="s1-1d"></a>
```java
// snippet 1.1d
@GetAction("echo/d")
public String echo_d(String message) {
    return message;
}
```



### <a name="req"></a>1.2 `H.Request` 请求对象

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

### <a name="resp"></a>1.3 `H.Response` 响应对象

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

## <a name="session_flash"></a>2. Session 与 Flash

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

### <a name="session_best_practice"></a>2.1 Session 应用技巧

鉴于 Session 的特点与使用限制, 下面是一些使用 Session 的一些技巧:

* 只存放简单的数据, 例如 username, userId 等
    - 复杂数据应该存放进数据库, 或者类似 redis 这样的 KV 存储
* 尽量不要存放敏感数据, 比如密码, 电话号码, 身份证号码之类的, 因为 session cookie 虽然不能篡改但可读.
    - 如果一定要存放敏感数据, 应该打开 session 加密配置. 当然这样会带来性能上的损耗


### <a name="session_flash_diff"></a>2.2 `H.Flash` 与 `H.Session` 的区别

`H.Flash` 与 `H.Session` 的区别在于 flash 中存入的信息只保存到下一次请求处理完毕. 另外 flash 提供了几个快捷方法:

* `error(String message)` - 相当于调用 `put("error", message)`
* `String error()` - 相当于调用 `get("error")`
* `success(String message)` - 相当于调用 `put("success", message)`
* `String success()` - 相当于调用 `get("success")`

**注意** Flash 通常之用于后端模板生成的系统架构. 对于前后端分离的应用一般都没有使用 Flash 的理由.

### <a name="session_flash_usage"></a>2.3 Session/Flash 使用例子

在应用中使用 session:

<a name="s2-3a"></a>
```java
// snippet 2.3a
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

<a name="s2-3b"></a>
```java
// snippet 2.3b
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

<a name="s2-3c"></a>
```html
<!-- 例 2.3c -->
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
### <a name="session_config"></a>2.4 Session 配置

* `session.secure` - 指定 session cookie 的 secure 选项. 默认值: 开发模式下为 `false`; 产品模式下为 `true`
    - **注意** 仅对给予 Cookie 的 session 存储有效. 对基于 Header 的 session 存储没有意义.
* `session.ttl` - 指定 session 无活动过期时间. 默认值: `60 * 30`, 即半小时
    - **注意** 每次请求都会刷新 session 的时间戳. `session.ttl` 的意思是当用户在这段时间里和应用没有任何交互会导致 session 过期
* `session.persistent` - 是否将 session cookie 定义为长效 cookie (persistent cookie). 如果激活这个选项, 即使用户关闭浏览器, 在 `session.ttl` 到来之前 session 都不会过期. 默认值: `false`
    - **注意** 仅对给予 Cookie 的 session 存储有效. 对基于 Header 的 session 存储没有意义.
* `session.encrypt` - 是否加密 session 字串. 默认值: `false`
    - **注意** 对 JWT 输出无效

关于 Session/Flash 在框架实现方面更详尽的信息, 参考 [Session 与 Flash 的处理详解](reference/session_flash.md)

## <a name="context"></a>3. ActionContext

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

### <a name="context_usage"></a>3.1 使用 `ActionContext`

下面的代码演示了 `ActionContext` 在用户登陆逻辑上的应用:

<a name="s3-1a"></a>
```java
// snippet 3.1a
@PostAction("/login")
public void login(String username, char[] password, ActionContext context) {
    if (!authenticate(username, password)) {
        context.flash().error("authentication failed")
        redirect("/login");
    }
    context.loginAndRedirect(username, "/");
}
```

## <a name="controller_request-handler"></a>4. 控制器与请求处理方法

在[1. 介绍](#intro)中我们引入了控制器与请求处理方法的概念并提供了一段简单的代码演示如何使用控制器和请求处理方法来处理请求并返回响应. 本节我们会详细讨论下面几点:

1. 请求处理方法参数
2. 控制器的依赖注入
3. 单例还是多例

### <a name="request-handler_params"></a>4.1 请求处理方法参数

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

<a name="s4-1a"></a>
```java
// snippet 4.1a
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

上面的代码在 ActFramework 的表达则简洁很多:

<a name="s4-1b"></a>
```java
// snippet 4.1b
@Action("{employeeId}/messages")
public void handleRequest(String employeeId, int months) {
    String msg = "employee request by id for paystub for previous months : " +
              employeeId + ", " + months;
    render("my-page", msg);
}
```

#### 案例 2

Jersey 使用 `@Context` 在请求处理方法中注入系统对象:

<a name="s4-1c"></a>
```java
// snippet 4.1c
@GET
@PATH("foo")
public String foo(@Context HttpServletRequest req) {
    return req.getParameter("foo");
}
```

ActFramework 无需注解, 直接在参数列表中声明即可:

<a name="s4-1d"></a>
```java
// snippet 4.1d
@GetAction("foo")
public String foo(H.Request req) {
    return req.paramVal("foo");
}
```

#### 案例 3

ActFramework 在参数列表中混合不同的参数类型:

<a name="s4-1e"></a>
```java
// snippet 4.1e
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

### <a name="controller-di"></a>4.2 控制器的依赖注入

在上面的例子中我们都是将依赖对象注入到方法中. 这样做的问题是如果某个控制器有很多方法, 都需要某个依赖对象, 方法的参数就会变得复杂, 依照 DRY 原则, 我们不希望每个方法上面都重复同样的参数声明. 解决的办法是将依赖注入控制器类. 例如

<a name="s4-2a"></a>
```java
// snippet 4.2a
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

<a name="s4-2b"></a>
```java
// snippet 4.2b
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

### <a name="single-multi"></a>4.3 单例还是多例

当我们将依赖对象注入一个控制器类带来的一个问题是: 这个控制器是否是线程安全的, 是否单例, 还是多例. 对此 ActFramework 的回答是: 依情况而定.

* 如果注入对象本身是有状态的, 比如 `ActionContext`, `H.Request` 等等, ActFramework 会就每个请求生成一个新的控制器实例
* 如果注入对象本身是无状态的, 或者说状态不影响当前计算,比如 `EventBus`, `JobManager`, `App` 等等, ActFramework 使用控制器的单例来响应新请求.

总的来说 ActFramework 会审查控制器类的每个实例字段, 如果有任何一个字段类是有状态的,就不会使用单例来启动该控制器. 这个审查过程也包含控制器的所有父类.

下面是 ActFramework 判断一个类是否有状态的过程:

1. 如果一个类被标注为 `@Singelton` 或 `@Stateless`, 或继承自 `SingletonBase`, 则该类无状态
    * 大部分 Act App 服务类都注册为 Singleton, 包括 `EventBus`, `JobManager` 等等
2. 如果一个类没有实例字段, 或者实例字段的类本身是无状态的, 则该类无状态.

最后需要解决的问题是如何将来自第三方库的类标注为无状态. 假如某个三方库提供了线程安全的微信接口类 WeixinIntf,这个类本身没有标注为 `@Singleton`, 但作为开发我们知道这是线程安全的, 所以不希望因为这个类的注入导致控制器放弃单例. 下面是解决办法:

<a name="s4-3a"></a>
```java
// snippet 4.3a
public class MyController_5_3a {
    @Inject @Stateless
    private IStorageService storageService;
}
```

我们在注入 `weixin` 字段的同时加上 `@Stateless` 注解, 这样 ActFramework 就知道这个字段不会影响到 `MyController` 的状态, 因此会使用单例来调用 `MyController` 请求处理方法.

## <a name="param-binding"></a>5. 请求参数绑定

ActFramework 在请求参数绑定方面提供了最大的灵活与方便. 请求参数可以来自不同的地方, 具有不同的编码方式, 绑定目标可以是请求处理方法的参数列表, 也可以是控制器类的实例字段. 在这之中, ActFramework 按照一下规则自动适配:

1. 优先级: URL 路径变量 > Query 参数 > Form 字段
2. 编码方式由请求的 `Content-Type` 头决定
3. 名字匹配: 请求参数名字和绑定对象 (请求处理方法参数或控制器字段) 名字必须一致

下面来看两个例子:

**1. 绑定到方法参数**

<a name="s5a"></a>
```java
// snippet 5a
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

<a name="s5b"></a>
```java
// snippet 5b
public class MyController_6b {
    private String foo;
    @GetAction("foo/{foo}")
    public String test() {
        return foo;
    }
}
```

或者

<a name="s5c"></a>
```java
// snippet 5c
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

### <a name="binding-resolving"></a>5.1 绑定与解析

请求参数绑定到方法参数通常会有两种情况, 

1. 方法参数为简单参数类型, 比如 int, String 等
2. 方法参数为复杂类型, 比如各种集合类型, 用户自定义的 POJO 类型等

对于第一种情况, 只需要从一个请求参数即可绑定到目标方法参数. 这个过程是一个字串到其他简单类型的转换过程, 我们称之为字串解析；与之对应的类, 我们称为 `StringValueResolver`．　下面是一个 `StringValueResolver` 的一个具体例子:

<a name="s5_1a"></a>
```java
// snippet 5.1a
public class BooleanResolver extends StringValueResolver<Boolean> {
    public Boolean resolve(String value) {
        return S.empty(value) ? Boolean.FALSE : Boolean.parseBoolean(value);
    }
}
```

上面的定义的 `BooleanResolver` 在需要绑定到布尔类型变量的时候就会被调用并用于类型转换. 已定义的解析器会被框架自动注册, 框架在参数绑定逻辑中根据目标类型自动查找需要的解析器并调用. 解析器对应用完全是透明的. **如果定义了多个从字串到某种类型的解析器, 后注册的解析器会覆盖先前注册的. 且注册顺序无法保证**

**注意** ActFramework 已经内置了所有的基本类型以及枚举型的解析器, 包括上面作为示例的 `BooleanResolver`. 应用几乎无需定义任何字串解析器.

对于第二种情况, 大部分情况都需要从多个请求参数取值最后生成一个目标方法参数. 这种多对一的解析过程我们称之为绑定；与之对应的类, 在 ActFramework 中称为　`Binder`. 下面是一个 `Binder` 的具体例子:

<a name="s5_1b"></a>
```java
// snippet 5.1b
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

<a name="s5_1c"></a>
```java
// snippet 5.1c
@GetAction("test/binder")
public String testBinder(@Bind(EmailBinder.class) String email) {
    return email;
}
```

**在本文中, 术语 "绑定" 泛指从请求参数中取值并转换为目标方法参数, 而不加区分单对单类型的解析还是多对单类型的绑定**

### <a name="data-source"></a>5.2 数据来源

**无需注解即可直接绑定的数据**

和 SpringMVC, Jersey 以及其他常见 Java Web 框架不同, ActFramework 自动匹配常见类型的请求参数和方法参数的名字而无需应用使用特殊注解, 包括:

1. URL 路径变量, 例如 `/order/{orderId}` 中的 `orderId`
2. Query 参数, 例如 `/order?orderId=aaa` 中的 `orderId`
3. Form 字段, 例如 `<input type="text" name="orderId">`
4. 上传文件, 例如 `<input type="file" name="file">`
5. Cookie 数据

**小贴士** 有可能会有方法参数和请求参数名字不匹配的情况, 这时候应该使用 `javax.inject.Named` 注解来适配, 例如:

<a name="s5_2a"></a>
```java
// snippet 5.2a
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

#### <a name="cookie-data-binding"></a>5.2.1 Cookie 数据绑定

对于直接绑定数据来源列表中的 URL 路径变量和 Query 参数绑定在前面的例 6.1 和例 6.2 中已有介绍. Form 表单绑定和上传文件我们会在后面详细介绍. 这里先讲一下 `Cookie` 的绑定, 看下面的例子:

<a name="s5_2_1a"></a>
```java
// snippet 5.2.1a
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

<a name="s5_2_1b"></a>
```java
// snippet 5.2.1b
@Global
@Before
public void countVisits(H.Session session) {
    session.incr("count");
}
```

**注意** Cookie 参数直接绑定是 act-1.8.8 提供的特性, 1.8.8 以前的版本需要这样做:

<a name="s5_2_1c"></a>
```java
// snippet 5.2.1c
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

#### <a name="header-data-binding"></a>5.2.2 Header 数据绑定

应用使用 `@HeaderVariable` 注解表明参数从请求头绑定:

<a name="s5_2_2a"></a>
```java
// snippet 5.2.2a
@GetAction("/header/user-agent")
public String header(@HeaderVariable("User-Agent") String userAgentString) {
    return userAgentString;
}
```

上面的代码相当于:

<a name="s5_2_2b"></a>
```java
// snippet 5.2.2b
@GetAction("/header/user-agent")
public String header(H.Request req) {
    return req.header("User-Agent");
}
```

**小贴士** 当方法参数变量名和请求头的名字可对应的时候可以省略 `@HeaderVariable` 注解里面的 `value` 参数:

<a name="s5_2_2c"></a>
```java
// snippet 5.2.2c
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

#### <a name="session-data-binding"></a>5.2.3 Session 数据绑定

应用使用 `@SessionVariable` 注解来标注某个参数需要从 Session 中绑定. 例如

<a name="s5_2_3a"></a>
```java
// snippet 5.2.3a
@GetAction("/session/username")
public String session(@SessionVariable String username) {
    return username;
}
```

上面的代码相当于:

<a name="s5_2_3b"></a>
```java
// snippet 5.2.3b
@GetAction("/session/username")
public String header(H.Session session) {
    return session.get("username");
}
```

如果 session 中的 key 和变量名不一致, 需要在 `@SessionVariable` 注解上设置 `value` 参数:

<a name="s5_2_3c"></a>
```java
// snippet 5.2.3c
@GetAction("/session/username")
public String session(@SessionVariable("user-name") String username) {
    return username;
}
```

### <a name="data-encoding"></a>5.3 请求数据编码

除了 URL 路径变量, Session, Cookie 和 Header, 其他的数据都存在不同编码方式的情况.

#### <a name="query-param-encoding"></a>5.3.1 Query 数组类型参数编码

对于下面的请求方法处理器:

<a name="s5_3_1a"></a>
```java
// snippet 5.3.1a
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

<a name="s5_3_1b"></a>
```java
// snippet 5.3.1b
@GetAction("test")
public List<Integer> test(List<Integer> i) {
    return i;
}
```

#### <a name="post-form-encoding"></a>5.3.2 POST Form 编码

对于 POST 方法, 当使用 `application/x-www-form-urlencoded` 或者 `multipart/form-data` 的时候, 可以采用两种不同的方式来编码数据.

使用下面的 POJO 与控制器代码为例来讲述:

<a name="s5_3_2a"></a>
```java
// snippet 5.3.2a
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

<a name="s5_3_2b"></a>
```html
<!-- snippet 5.3.2b -->
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

<a name="s5_3_2c"></a>
```html
<!-- snippet 5.3.2c -->
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

<a name="s5_3_2d"></a>
```html
<!-- snippet 5.3.2d -->
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

#### <a name="post-json-encoding"></a>5.3.3 POST JSON 编码

现在越来越多的前端代码使用 AJAX 和 JSON 方式和服务器交互. ActFramework 也支持 JSON 编码的数据绑定. 对于 [示例 5.3.2a](#s5_3_2a) 的代码, 当请求的 `Content-Type` 头为 `application/json` 时, ActFramework 按照 JSON 解析请求 body 并绑定到方法参数上, 对应的一个 JSON 格式数据示例为:

<a name="s5_3_3a"></a>
```json
// snippet 5.3.3a 
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


### <a name="simple-type-data-binding"></a>5.4 简单数据类型绑定

除了上传文件以外的大部分请求参数原始类型都是字串, 当绑定到方法参数的时候需要转换为声明的类型. ActFramework 可以转换请求参数字串(1个或者多个)为几乎所有的类型. 这里讨论简单数据类型的绑定. 所谓简单数据类型是指一下类型:

1. 所有的基本类型, 包括 `boolean`, `byte`, `char`, `short`, `int`, `float`, `long`, `double`
2. 所有基本类型对应的包装类型, 包括 `Boolean`, `Byte`, `Character`, `Short`, `Integer`, `Float`, `Long`, `Double` 
3. 字串
4. 枚举

在 [5.1 绑定与解析](#binding-resolving) 我们已经介绍了 `StringValueResolver` 是框架用来将字串类型的请求数据解析为目标参数类型的机制. 对于上面罗列的基本类型及其包装类型, Java JDK 库已经定义了明确的和字串之间的转换逻辑, 例如 `String` -> `Boolean`, 就是通过 `Boolean.parseBoolean(String)` 进行的, 这些众所周知的基本类型转换逻辑也是框架内定义的 `StringValueResolver` 的基础, 毋庸多谈. 

这里需要讲一下两个问题: 第一, 空值问题, 即当请求中没有数据, 目标参数如何设定值；第二错误值问题, 当请求数据无法转换到目标类型如何处理.

#### <a name="null-val"></a>5.4.1 空值处理

ActFramework 按照一下规则处理空值:

1. 对于所有对象,包括包装类型, 字串, 枚举, 集合类型以及其他对象类型, 如果请求中找不到绑定数据, 统一返回 `null`
2. 对于基本类型, 例如 boolean, int 等, 如果请求中找不到绑定数据, 按照字段默认值填入绑定目标参数.

基本类型默认值列表

<a name="t5_4_1a"></a>
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

#### <a name="bad-data"></a>5.4.2 错误数据处理

当用户请求传入错误数据, 例如需要绑定的数据类型是 `int`, 传入的数据是 `xyz`, 这时候 ActFramework 会返回一个 `400 Bad request` 响应给请求发起方

#### <a name="enum-binding"></a>5.4.3 枚举类型的绑定

当目标类型为 `Enum` 的时候, ActFramework 将请求数据中的字串和 enum 名字想匹配来查找绑定的 enum 值. 有两种绑定方式:

**1. 基于 Keyword 变化形式的非精确匹配**

假设有下面的 enum 定义:

<a name="s5_4_3a"></a>
```java
// Snippet 5.4.3a
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

### <a name="array-collection-binding"></a>5.5 数组与集合类型绑定

ActFramework 支持数组与集合类型的数据绑定. 对于数组, 列表(List) 和集合(Set), 请求数据的形式是一致的；对于 Map 类型的数据绑定, 请求参数的编码形式会有所变化. 这里我们介绍简单类型的数组与集合类型绑定, 对于复杂类型, 比如 POJO 的数组和集合类型在后面[POJO 绑定](#pojo-binding)会谈及

#### <a name="primitive-array-binding"></a>5.5.1 基本类型数组绑定

ActFramework 支持除 char 以外所有的基本类型数组绑定. char 数组绑定不被支持因为和字串绑定冲突. 我们以 int[] 类型来说明 ActFramework 对基本数据类型数组绑定的支持. 假设我们有下面的请求处理方法:

<a name="s5_5_1a"></a>
```java
// Snippet 5.5.1a
@Action("/test")
public int[] test(int[] n) {
    return n;
}
```

**GET 请求编码**

在[5.3.1 Query 数组类型参数编码](#query-param-encoding)中已经介绍过基本数组类型在 GET 请求中的三种编码方式. 这里就不再复述.

**POST 请求编码**

方式一 (Form 表单)

<a name="s5_5_1b"></a>
```html
<!-- snippet 5.5.1b -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="n" value="1,2,3">
</form>
```

方式二 (Form 表单)

<a name="s5_5_1c"></a>
```html
<!-- snippet 5.5.1c -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="n" value="1">
<input name="n" value="2">
<input name="n" value="3">
</form>
```

方式三 (Form 表单)

<a name="s5_5_1d"></a>
```html
<!-- snippet 5.5.1d -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="n[]" value="1">
<input name="n[]" value="2">
<input name="n[]" value="3">
</form>
```

方式四 (Form 表单)

<a name="s5_5_1e"></a>
```html
<!-- snippet 5.5.1e -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="n[0]" value="1">
<input name="n[2]" value="3">
</form>
```

**注意** 这里缺失 `n[1]` 的赋值, 因此解析出的 `int[]` 为: `{1,0,3}`

方式五 (JSON 数据)

<a name="s5_5_1f"></a>
```json
[1, 2, 3]
```

这种方式通常都是前端采用 AJAX 请求向服务端发送 JSON 数据, 需要请求的 `Content-Type` 头置为 `application/json` 才能正确解析

#### <a name="wrap-array-binding"></a>5.5.2 包装类型数组绑定

下面是使用包装数据类型绑定的请求处理方法演示代码:

<a name="s5_5_2a"></a>
```java
// Snippet 5.5.2a
@Action("/test")
public Integer[] test(Integer[] n) {
    return n;
}
```

包装类型数组和基本数据类型数组的处理几乎完全一致, 唯一不同的地方在于对空值(`null`)的处理. 基本数据类型的空值采用默认值填入, 包装类型的空值也填入空值. 因此对于上面[方法四](#s5_5_1e)的情况, 绑定的 `Integer[] n` 的值为: `{1, null, 3}`. 需要特别注意的是这种数组目前无法转换为合法的 JSON 字串, 应用开发人员应该小心处理.

字串和枚举数组和包装类型数组的处理类似, 无需多言.

#### <a name="list-set-binding"></a>5.5.3 List 和 Set 绑定

List 和 Set 绑定和包装类型数组绑定的处理与编码方式完全一样. 下面是使用 List/Set 类型的处理方法演示代码:

<a name="s5_5_3a"></a>
```java
// Snippet 5.5.3a
@Action("/test")
public void test(List<Integer> intList, Set<String> stringSet) {
    render(intList, stringSet);
}
```

采用[方式二](#s5_5_1c)来编码的例子:

<a name="s5_5_3b"></a>
```html
<!-- snippet 5.5.3b -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="intList" value="1">
<input name="intList" value="2">
<input name="intList" value="3">
<input name="stringSet" value="foo">
<input name="stringSet" value="bar">
</form>
```

获得的结果用 JSON 表达应该是: `{"stringSet":["bar","foo"],"intList":[1,2,3]}`

[5.5.1 节](#primitive-array-binding) 中提到的其他编码方式也都可以使用

#### <a name="map-binding"></a>5.5.4 Map 绑定

ActFramework 支持 Map 类型的绑定. Map 的 key 必须能从字串直接解析 (能找到对应的 `StringValueResolver`), Map 的 value 可以是任何类型. 我们这里先讨论 value 为基本类型的情况. 对于 value 为任何类型的讨论, 放到 [POJO 绑定](#param-binding) 详细讲述.

对于下面的请求处理方法:

<a name="s5_5_4a"></a>
```java
// 5.5.4a
@Action("/test/554")
public Map<String, Integer> test(Map<String, Integer> map) {
    return map;
}
```

假设需要获得 `{"a":1,"b":2}` 的结果下面是 GET 和 POST 请求的编码方式:

<a name="s5_5_4b"></a>
**GET 请求编码**

```
/test/554?map[a]=1&map[b]=2
```

**POST 请求编码**

方式一

<a name="s5_5_4c"></a>
```html
<!-- snippet 5.5.4c -->
<form action="/test/554" method="post" enctype="application/x-www-form-urlencoded">
<input name="map" value="a=1,b=2">
</form>
```

方式二

<a name="s5_5_4d"></a>
```html
<!-- snippet 5.5.4d -->
<form action="/test/554" method="post" enctype="application/x-www-form-urlencoded">
<input name="map[a]" value="1">
<input name="map[b]" value="2">
</form>
```

ActFramework 也支持以其他类型作为 Key, 比如:

<a name="s5_5_4e"></a>
```java
// Snippet 5.5.4e
@Action("/test/554")
public Map<Integer, String> test(Map<Integer, String> map) {
    return map;
}
```

只要请求发送的数据能够正确进行类型转换, ActFramework 都能完成绑定. 对于 GET 请求, 正确的请求参数为: `/test/554?1=a&2=b`. 对于 POST 请求也类似. 

**如非必要, 不推荐使用字串以外的类型作为 Map 的 key**, 因为这样的 Map 数据不能生成合法的 JSON 字串.

### <a name="date-data-binding"></a>5.6 日期数据绑定

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

#### <a name="date-format"></a>5.6.1 日期格式

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

<a name="s5_6_1a"></a>
```java
// Snippet 5.6.1a
@Action("test/561/local-date")
public LocalDate testLocalDate(LocalDate date) {
	return date;
}
```

如果应用在中国大陆, 发送的 GET 请求应该是 `/test/561/local-date?date=2018-4-23`, POST 请求的表单字段值也应类似.

#### <a name="date-format-localization"></a>5.6.2 日期格式本地化

当设置了 `i18n=true` 打开 ActFramework 的国际化支持后, 日期和时间的格式处理会更加复杂. 首先系统会从请求的 `Accept-Language` 解析用户端的 `Locale` 并存储在 `ActionContext` 中. 

在接受日期或者时间参数的时候, 不再仅仅通过 `fmt.date`, `fmt.time` 以及 `fmt.date-time` 设置来确定日期时间字串的模式, 而是依据当前请求的 `Locale` 来判断应该使用的模式. ActFramework 支持对特定的语言时区定义日期时间格式, 如下例所示:

<a name="s5_6_2a"></a>
```
# snippet 5.6.2a
fmt.zh-cn.date=yyyy-M-d
fmt.zh-tw.date=yyyy/M/d
```

在没有设置本地日期格式的情况下, 框架默认使用 `DateFormat.MEDIUM` 来获取相应的日期时间格式

#### <a name="date-format-specified"></a>5.6.3 在绑定参数上指定日期格式

有的时候对于特定的请求处理方法需要使用和全局设定不同的日期/时间模式, 这时候可以使用 `@act.data.annotation.DateTimeFormat` 注解, 如下例所示:

<a name="s5_6_3a"></a>
```java
// Snippet 5.6.3a
@Action("test/563/local-date")
public LocalDate testCustomeDatePattern(@DateTimeFormat("yy-M-d") LocalDate date) {
	return date;
}
```

### <a name="pojo-binding"></a>5.7 POJO 绑定

ActFramework 支持 POJO 绑定. 在[5.3.2 POST Form 编码](#post-form-encoding) 我们已经通过实例讲述了 POJO 对象 POST Form 编码的两种方式: JQuery 和 dot 格式. 下面采用 jQuery 方式来详细介绍 POJO 的绑定, 包括:

* 单个 POJO 对象绑定
* POJO 数组或列表绑定
* POJO Map绑定

我们在下面的 POJO 绑定讨论中使用如下 POJO 对象:

<a name="s5_7a"></a>
```java
// snipeet 5.7a
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

#### <a name="single-pojo-binding"></a>5.7.1 单个 POJO 绑定

请求处理方法:

<a name="s5_7_1a"></a>
```java
// snipeet 5.7.1a
@Action("/test/571")
public Employee pojo571(Employee emp) {
	return emp;
}
```

GET 编码:

<a name="s5_7_1b"></a>
```
GET /test/571?emp[no]=123&emp[name]=Bob&emp[address][street]=King%20st&emp[address][postCode]1234
```

POST 编码:

<a name="s5_7_1c"></a>
```html
<!-- snippet 5.7.1c -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="emp[no]" value="123">
<input name="emp[name]" value="Bob">
<input name="emp[address][street]" value="King st">
<input name="emp[address[postCode]" value="1234">
</form>
```

#### <a name="list-pojo-binding"></a>5.7.2 POJO 数组或列表绑定

请求处理方法:

<a name="s5_7_2a"></a>
```java
// snipeet 5.7.2a
@Action("/test/572")
public List<Employee> pojo672(List<Employee> empList) {
	return empList;
}
```

GET 编码:

<a name="s5_7_2b"></a>
```
GET /test/572?empList[0][no]=123&empList[0][name]=Bob&empList[0][address][street]=King%20st&empList[0][address][postCode]=1234
```

POST 编码:

<a name="s5_7_2c"></a>
```html
<!-- snippet 5.7.2c -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="empList[0][no]" value="123">
<input name="empList[0][name]" value="Bob">
<input name="empList[0][address][street]" value="King st">
<input name="empList[0][address[postCode]" value="1234">
</form>
```

#### <a name="map-pojo-binding"></a>5.7.3 POJO Map 绑定

请求处理方法:

<a name="s5_7_3a"></a>
```java
// snipeet 5.7.3a
@Action("/test/573")
public Map<String, Employee> pojo673(Map<String, Employee> empMap) {
	return empMap;
}
```

GET 编码:

<a name="s5_7_3b"></a>
```
GET /test/573?empMap[bob][no]=123&empMap[bob][name]=Bob&empMap[bob][address][street]=King%20st&empMap[bob][address][postCode]=1234
```

POST 编码:

<a name="s5_7_3c"></a>
```html
<!-- snippet 5.7.3c -->
<form action="/test" method="post" enctype="application/x-www-form-urlencoded">
<input name="empMap[bob][no]" value="123">
<input name="empMap[bob][name]" value="Bob">
<input name="empMap[bob][address][street]" value="King st">
<input name="empMap[bob][address[postCode]" value="1234">
</form>
```

### <a name="file-upload"></a>5.8 上传文件

先看一个简单的示例:

上传文件表单

<a name="s5_8a"></a>
```html
<!-- snippet 5.8a -->
<form action="/test/file" method="post" enctype="multipart/form-data">
	<input type="file" name="upload">
</form>
```

请求处理方法:

<a name="s5_8b"></a>
```java
// Snippet 5.8b
@PostAction("/test/file")
public void upload(File upload) {
	// save uploaded file
}
```

上面的代码使用 `java.io.File` 来声明 `upload` 参数的类型, 没有问题. 不过 ActFramework 推荐使用 `org.osgl.storage.ISObject` 来替代 `java.io.File`:

<a name="s5_8c"></a>
```java
// Snippet 5.8c
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

#### <a name="upload-in-memory-cache">5.8.1 上传文件内存缓存

通常来讲框架在将上传文件传递给用户应用的时候会事先生成临时文件, 方便应用对文件的各种处理, 比如存入某个永久存储, 或者进行图片剪裁等操作. 因为有了 osgl-storage 库, ActFramework 在此基础上提供了内存缓存的概念. 刚才讲到使用 `ISObject` 的好处第 3 项, 当上传文件长度小于某个阀值时, ActFramework 将生成内部实现为 `byte[]` 的 `ISObject` 实现, 这样无需因为临时文件而产生 IO 操作.

这个阀值的配置示例如下:

<a name="s5_8_1a"></a>
```
upload.in_memory.threshold=1024 * 100
```

上面将阀值配置为 `100k`, 即所有长度小于 100k 的上传文件都不会因为临时文件产生 IO 操作. 前提是应用使用了 `ISObject` 来声明上传文件, 而不是 `File`.

#### <a name="upload-base64">5.8.2 上传 BASE 64 编码

TBD

### <a name="customize-data-binding"></a>5.9 自定义数据绑定

ActFramework 提供了强大的请求参数绑定支持, 应用几乎没有定义自己的 `StringValueResolver` 或 `Binder` 的需要. 

#### <a name="customize-resolver"></a>5.9.1 自定义 `StringValueResolver`

假设应用对某种类型特殊编码方式, 可以采用自定义 `StringValueResolver`

自定义类型:

<a name="s5_9_1a"></a>
```java
// snippet 5.9.1a
public class Foo {
	public int id;
	public String name;
}
```

对于上面的类型 `Foo` 假设应用使用的编码方式为 `<id>-<name>`, 例如 `123-foobar`, 自定义的 `StringValueResolver` 为:

<a name="s5_9_1b"></a>
```java
// snippet 5.9.1b
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

<a name="s5_9_1c"></a>
```java
// snippet 5.9.1c
@GetAction("1/c")
@JsonView
public Foo testFooResolver(Foo foo) {
	return foo;
}
```

向 `http://localhost:5460/6/9/1/c?foo=12-abc` 发出请求得到下面的响应:

<a name="s5_9_1d"></a>
```json
{
    "id": 12, 
    "name": "abc"
}
```

#### <a name="customize-binder"></a>5.9.2 自定义 Binder

自定义 Binder 的方法和例子参见 [5.1 绑定与解析](#binding-resolving)

### <a name="data-validation"></a>5.10 绑定参数校验

Act 支持 JSR 303 Bean 校验, 如下例所示:

<a name="s5_10a"></a>
```java
// snippet 5.10a
@GetAction("notNull")
public Result notNull(@NotNull String value) {
	if (context.hasViolation()) {
		return text("Error(s): \n%s", context.violationMessage());
	}
	return text("not null success with %s", fmt);
}
```

当发送请求给上面的 `notNull` 端口没有指定 `value` 的时候, 将会得到如下响应:

<a name="s5_10b"></a>
```
Error(s): 
value: may not be null
```


TBD


## <a name="return-response"></a>6. 返回响应

ActFramework 提供非常灵活的方式(包括显式和隐式)让开发人员返回各种响应.

### <a name="return-template"></a>6.1 使用模板生成返回结果

可以使用隐式或显式两种方式指定模板路径

#### <a name="implicit-template-path"></a>6.1.1 隐式模板路径指定

对于下面的请求响应方法代码:

<a name="s6_1_1"></a>
```java
// snippet 6.1.1
package demo.controller;

public class TemplateDemo extends Controller.Util {

    @GetAction("/templateDemo/implicitTemplatePath")
    public void implicitTemplatePath(String name, int id) {
        render(name, id)
    }

}
```

框架会自动到 `/${view-id}/demo/controller/TemplateDemo/` 目录下寻找 `implicitTemplatePath.html` 模板文件来生成响应结果. 

* 路径中的 `${view-id}` 默认为 `rythm`, 如果应用引入了其他视图插件, 比如 `act-freemarker` 或者 `act-thymeleaf` 等, 也可以变成对应的 `freemarker` 或者 `thymeleaf`. 
* `demo/controller` 对应控制器类的 package: `demo.controller`
* `TemplateDemo` 对应控制器类的名字 `(Class.getSimpleName())`
* `implicitTemplatePath.html` 则对应请求响应方法名字以及当前请求的格式.

有趣的地方在于 `.html` 格式后缀, 这个灵活性在于开发人员可以使用不同后缀名的模板文件来定义不同的响应结果返回,以匹配请求的 `Accept` 头指定. 假设应用希望能同时处理 `text/html`, `text/plaintext` 和 `application/json` 三种方式, 则可以在相应目录下定义下面三个模板文件:

* `implicitTemplatePath.html`: 对应 `text/html` 请求
* `implicitTemplatePath.txt`: 对应 `text/plaintext` 请求
* `implicitTemplatePath.json`: 对应 `application/json` 请求

#### <a name="explicit-template-path"></a>6.1.2 显式模板路径指定

默认(隐式)模板路径通常都会很长, 因为缺乏 IDE 的支持, 用起来不是很方便, 所以在模板数量并不多的情况下可以采用显式模板路径指定:

<a name="s6_1_2"></a>
```java
// snippet 6.1.2
package demo.controller;

public class TemplateDemo extends Controller.Util {

    @GetAction("/templateDemo/explicitTemplatePath")
    public void explicitTemplatePath(String name, int id) {
        render("/explicit_templ_path", name, id)
    }

}
```

上面我们使用了 `"/explicit_templ_path"` 字面量来显式传递模板路径, 这个时候模板文件应该为: `/${view-id}/explicit_templ_path.${fmt-suffix}`, 其中的 `${view-id}` 依然是视图 id, `${fmt-suffix}` 也还是请求格式化后缀. 这两处都可以在上一节 [隐式模板路径指定](#implicit-template-path) 中找到解释

#### <a name="template-variable"></a>6.1.3 模板变量

在上面两节中我们看到 `render()` 语句中传递了 `name` 和 `id` 变量, 这两个变量会依其变量放到模板变量表里面, 在模板中可以分别使用 `name` 和 `id` 来获取变量值. 拿 [6.1.2](#s6_1_2) 作为例子, 这个过程大致相当于:

<a name="s6_1_3a"></a>
```java
// snippet 6.1.3a
package demo.controller;

public class TemplateDemo extends Controller.Util {

    @GetAction("/templateDemo/explicitTemplatePath2")
    public void explicitTemplatePath2(String name, int id, ActionContext context) {
        context.renderArg("name", name);
        context.renderArg("id", id);
        render("/explicit_templ_path")
    }

}
```

因为 ActFramework 在加载 TemplateDemo 控制器类的时候使用了 ASM 操作字节码, 所以自动帮助开发人员加上了 `context.renderArg()` 语句, 因此开发人员可以使用 

<a name="s6_1_3b"></a>
```java
// snippet 6.1.3a
render(name, id, ...)));
```

这样更加简介的方法来表达模板变量的传递. 我们注意到在显式指定模板路径的时候使用的是字串字面量 `"/explicit_templ_path"`, 而不是将值 `"/explicit_templ_path"` 放进某个字串变量, 比如 `templatePath` 中, 再传递给 `render()` 方法, 原因就在于当框架的字节码增强器检测到变量的时候, 认定这是模板变量, 而不是模板路径, 所以会将值 `"/explicit_templ_path"` 传递给模板, 而不是当作模板路径处理. 

#### <a name="supported-template-engines"></a>6.1.4 支持的模板引擎

ActFramework 提供了以下模板引擎集成用于生成基于文本的响应输出:

| 视图 ID |  模板引擎 | 插件 |
| ------- | ---- | --- |
| rythm | [Rythm](http://rythmengine.org) | 内置 | 
| beetl | [Beetl](http://ibeetl.com/) | [act-beetl](https://github.com/actframework/act-beetl) |
| freemarker | [FreeMarker](https://freemarker.apache.org/) | [act-freemarker](https://github.com/actframework/act-freemarker) |
| mustache | [Mustache](https://github.com/spullara/mustache.java) | [act-mustache](https://github.com/actframework/act-mustache) |
| thymeleaf | [Thymeleaf](https://www.thymeleaf.org/) | [act-thymeleaf](https://github.com/actframework/act-thymeleaf) |
| velocity | [Velocity](https://velocity.apache.org/) | [act-velocity](https://github.com/actframework/act-velocity) |

因为 ActFramework 采用视图 ID 来管理模板引擎文件, 很自然地提供了对多模板引擎的支持, 非常方便迁移老项目. 比如老项目是基于 spring + thymeleaf, 可以直接将以前的 thymeleaf 模板放置进 `resources/thymeleaf/` 目录下, 而新开发的特性则可以放进 `resources/rythm` 目录下, ActFramework 会自动查找到需要的模板引擎文件.

#### <a name="special-template-engine"></a>6.1.5 Excel 视图

在上一节我们提到的模板引擎都是用于生成基于文本的响应输出, ActFramework 的视图机制也同样适用于非文本响应, 例如 Excel 文件输出. 

目前唯一支持的非文本响应视图是由 [https://github.com/actframework/act-excel](act-excel) 插件提供 Excel 视图. 该插件依赖于 [jxls 库](http://jxls.sourceforge.net/getting_started.html) Excel 模板支持. Excel 视图 ID 为 `excel`.

当请求的 Accept 头为 Excel 的 MIME 类型的时候, act-excel 插件生成 Excel 下载文件. 值得一提的是这种机制对于控制器是完全透明的, 控制器代码只需要提供数据而无需关注视图实现, 对于下面的控制器代码:

<a name="s6_1_5"></a>
```java
// snippet s6.1.5
@GetAction("/foo")
public void foo(String fooName, int barNo) {
    render(fooName, barNo);
}
```

模板视图的选择机制为:

* 当发送的请求 Accept 头为 `text/html` 的时候, 框架从 `resources/${view-id}/...` 下寻找 `foo.html` 模板文件
* 当发送的请求 Accept 头为 `application/vnd.ms-excel` 的时候, 框架从 `resources/excel/...` 下寻找 `foo.xls` 模板文件
* 当发送的请求 Accept 头为 `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` 头的时候, 框架从 `resources/excel...` 下寻找 `foo.xlsx` 模板文件.

对应 [s6.1.5](#s6_1_5) 控制器代码的模板文件接受 `fooName` 和 `barNo` 两个模板变量, 生成响应的响应输出.

更多关于 Excel 视图的情况可以参考 [Excel 视图的示例项目](https://github.com/actframework/act-demo-apps/tree/master/excel)

### <a name="return-value"></a>6.2 直接返回数据

对于 RESTful 服务这样的应用来讲直接返回数据更加直观和简洁. 例如:

<a name="s6_2"></a>
```java
// snippet 6.2
@GetAction("/users/{id}")
public User getUser(Long id, User.Dao userDao) {
    return userDao.findById(id);
}
```

#### <a name="return-content_type"></a>6.2.1 内容类型与响应生成逻辑

直接返回数据的代码非常简洁, 一个有趣的问题是框架是如何从返回数据生成最终响应的呢. 关键在于请求的数据类型. HTTP 协议定义了 `Accept` 头, 用于指定响应应该返回的数据类型. ActFramework 依据这个来确定返回数据类型, 进而生成最终响应. 目前 ActFramework 支持的数据类型及响应生成方式有:

* `text/html` - 首先确定是否有和请求方法对应的模板, 模板寻找方法参见 [隐式模板路径指定](#implicit-template-path)
    - 如果找到对应模板, 返回数据以 `result` 名字传入模板变量列表, 并生成响应
    - 如果没有对应模板, 则直接在返回值对象上调用 `Object.toString()` 方法生成响应
* `application/json` - 首先确定是否有和请求方法对应的模板, 模板寻找方法参见 [隐式模板路径指定](#implicit-template-path), 注意这种类型对应的模板文件扩展名为 `.json` 而不是 `.html`
    - 如果找到对应模板, 返回数据以 `result` 名字传入模板变量列表, 并生成响应
    - 如果没有对应模板, 则调用内置 JSON 库生成响应
* `text/csv` - 首先确定是否有和请求方法对应的模板, 模板寻找方法参见 [隐式模板路径指定](#implicit-template-path), 注意这种类型对应的模板文件扩展名为 `.csv`
    - 如果找到对应模板, 返回数据以 `result` 名字传入模板变量列表, 并生成响应
    - 如果没有对应模板, 则调用内置 csv 工具生成响应
* `application/vnd.ms-excel` 
    - 如果找到对应模板 (后缀名为 `.xls`), 返回数据以 `result` 名字传入模板变量列表, 并生成响应
    - 如果没有对应模板, 则调用 `act-excel` 内置 excel 工具生成 excel 2003 格式的下载文件
* `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
    - 如果找到对应模板 (后缀名为 `.xlsx`), 返回数据以 `result` 名字传入模板变量列表, 并生成响应
    - 如果没有对应模板, 则调用 `act-excel` 内置 excel 工具生成 excel 2007 格式的下载文件

#### <a name="advice-return-value"></a>6.2.2 修改返回数据

有的时候我们希望提供统一的返回数据结构修改机制. 比如对于所有的返回数据 v, 希望最终客户端收到的数据格式为:

<a name="s6_2_2a"></a>

```json
{
    "code": 0,
    "data": v
}
```

这种情况可以通过 `ReturnValueAdvice` 来实现:

<a name="s6_2_2b"></a>

```java
// snippet 6.2.2b
public class MyAdvice implements ReturnValueAdvice {
    @Override
    public Object applyTo(Object o, ActionContext actionContext) {
        return C.Map("code", 0, "data", o);
    }
}
```

实现了上面的 `MyAdvice` 之后需要在应用配置文件里面加上:

<a name="s6_2_2c"></a>

```
# snippet 6.2.2c
globalReturnValueAdvice=com.myproj.MyAdvice
```

一旦配置 `globalReturnValueAdvice`, 该 Advice 将会对所有具有返回值的请求处理方法返回值进行处理. 如某些请求处理方法或控制器参数不需要该逻辑, 可以将 `@NoReturnValueAdvice` 注解加到方法或者类上来规避.

如果某个请求处理方法或者某个控制器的所有请求处理方法都需要某个特定的 `ReturnValueAdvice`, 可以选择使用 `@ReturnValueAdvisor` 注解:

<a name="s6_2_2d"></a>

```java
// snippet 6.2.2d
@ReturnValueAdvisor(MySecondAdvice.class)
public Pojo foo() {
    ...
}
```

注意:

1. 加载在方法上的 `@ReturnValueAdvisor` 注解覆盖加载在控制器上的注解.
2. 如果请求处理方法有对应的模板, 则返回值将不会被任何 Advice 修改.


### <a name="output-binary"></a>6.3 输出二进制内容

#### <a name="output-inline-content"></a>6.3.1 输出内嵌(inline)二进制内容

下面的代码输出内嵌二进制内容:

<a name="s6_3_1a"></a>
```java
// snippet s6.3.1a
public void renderImage(String imgId) {
    Image img = imgDao.findById(imgId);
    byte[] blob = img.blob();
    Controller.Util.renderBinary(blob);
}
```

`Controller.Util` 上其他生成内嵌二进制内容的 API:

```java
// dump file content to response
renderBinary(File file);

// dump binray content from input stream to response
renderBinary(InputStream inputStream);

// dump content from an `ISobject` instance
renderBinary(ISObject storageObject);
```

**小贴士** 每个 `renderBinary` 方法都有一个别名为 `binary` 的方法与其对应并提供完全一样的实现. 下面是 [s6.3.1a](#s6_3_1a) 的另一种表达方式:

<a name="s6_3_1b"></a>
```java
// snippet s6.3.1b
public Result renderImage(String imgId) {
    Image img = imgDao.findById(imgId);
    byte[] blob = img.blob();
    return Controller.Util.binary(blob);
}
```

#### <a name="download"></a> 6.3.2 输出下载(attachment)内容

下面的代码输出下载文件:

<a name="s6_3_2a"></a>
```java
// snippet s6.3.2a
public void downloadAttachment(String postId, int attachmentId) {
    Post post = postDao.findById(postId);
    Attachment attachment = post.getAttachmentById(attachmentId);
    byte[] blob = attachment.getBlob();
    String name = attachment.getName();
    Controller.Util.download(blob, name);
}
```

下面是功能完全一致的另一种表达方式:

<a name="s6_3_2b"></a>
```java
// snippet s6.3.2b
public Result downloadAttachment(String postId, int attachmentId) {
    Post post = postDao.findById(postId);
    Attachment attachment = post.getAttachmentById(attachmentId);
    byte[] blob = attachment.getBlob();
    String name = attachment.getName();
    return Controller.Util.download(blob, name);
}
```


`Controller.Util` 上其他生成下载内容的 API:

```java
// download URL content using specified name
download(URL url, String attachmentName);

// download URL content using inferred name
download(URL url);

// download File content using specified name
download(File file, String attachmentName);

// download File content using infferred name
download(File file);

// download content from inputstream using specified name
download(InputStream is, String attachmentName);

// download content from inputstream using inferred name
download(InputStream is);
```

##### <a name="download-return-value"></a> 6.3.2.1 直接下载返回数据

ActFramework 可以依据请求的 `Accept` 头来决定返回响应的格式. 有的响应格式是需要文件下载的, 例如 `text/csv` 和 `application/vnd.ms-excel`, 分别对应了 `.csv` 和 `.xls` 文件下载.

对于下面的请求处理方法:

<a name="s6_3_2_1"></a>

```java
// snippet 6.3.2.1
@GetAction("orders")
public List<Orders> listOrders() {
    ...
}
```

当请求的 `Accept` 头为 `text/csv` 的时候会生成 `orders.csv` 文件下载; 而当 `Accept` 头为 `application/vnd.ms-excel` 的时候会生成 `orders.xls` 下载 (需要应用引入 act-excel 插件的依赖). 这里下载文件的名字是 URL 最后一个路径部分 `orders`, 后缀名则更加下载文件格式自动决定.

##### <a name="download-filename-for-return-value"></a> 6.3.2.2 修改直接下载返回数据的文件名

在上一节中我们看到了下载文件的文件名是 URL 的最后一部分, 但有的时候我们希望应用能够自己控制下载文件名, 可以通过 `@DownloadFilename` 注解实现:

<a name="s6_3_2_2a"></a>

```java
// snippet 6.3.2.2a
@GetAction("orders")
@DownloadFilename("order-report")
public List<Orders> listOrders() {
    ...
}
```

注意在 `@DownloadFilename` 注解中不要加上文件后缀, 例如 `.xls`, 因为框架会自动根据下载文件格式添加后缀.

在需要动态文件名的时候可以使用 `ActionContext` 提供的 API:

<a name="s6_3_2_2b"></a>

```java
// snippet 6.3.2.2b
@GetAction("orders")
public List<Orders> listOrders(ActionContext context) {
    String dateTag = getDateTag(); // 返回当日标记, 类似这样的: 20180101 
    context.downloadFileName("order-report-" + dateTag);
    ...
}
```

基于同样的理由, 请不要在 `ActionContext.downloadFilename(String)` API 中传入文件后缀名.

**小贴士** 通常这种下载文件的 GET 请求都不会使用 JavaScript 来操控 AJAX 请求格式, 而是直接从浏览器发起, 所以很难设置 `Accept` 头. ActFramework 提供了 `content_suffix.aware` 配置, 当该配置设置为 `true` 的时候, 可以通过在 URL 路径后面加上 content suffix 的办法来篡改 `Accept` 头. 例如: `/report/xls` 将 `/report` 请求的 Accept 篡改为 `application/vnd.ms-excel` 而 `/report/xlsx` 则将 `Accept` 头篡改为 `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`.


### <a name="redirect"></a>6.4 重定向

HTTP 标准定义了多种重定向语义, 在 ActFramework 中都有对应的 API:

| 状态码 | 语义 | ActFramework API |
| --- | --- | --- |
| 301 Moved Permanently | 永久移动 | `Controller.Util.moved(String url)` |
| 302 Found | 临时移动 | `Controller.Util.found(String url)` |
| 303 See Other | 临时移动 | `Controller.Util.seeOther(String url)` |
| 307 Temporary Redirect | 临时重定向 | `Controller.Util.temporaryRedirect(String url)` |
| 308 Permanent Redirect | 永久重定向 | `Controller.Util.permanentRedirect(String url)` |

关于 5 种重定向的一些解释:

* 301 的语义非常清晰, 就是被请求的资源已永久移动到新位置
* 302 的情况比较复杂, 业界(浏览器)对这个状态的处理和标准定义不一致, 基本上是按照 303 的语义来处理的, 具体来说就是 POST 请求受到 302 响应之后发送 GET 请求到新路径
* 303 因为业界对 302 的实现不清晰, 因此出了个 303, 当 POST|PUT|DEETE 请求的响应为 303 的时候, 浏览器发出 GET 请求到新路径. **注意** HTTP/1.1 以前的浏览器不识别 303, 如果需要兼容旧浏览器的话, 应用应该选择 302 而不是 303.
* 307 对 302 原始语义的新定义, 当 POST 请求收到 307 响应的时候继续使用 POST 方法向新 URL 发出响应.
* 308 是 307 的永久版本. 308 和 307 的关系类似于 302 和 301 的关系.

#### <a name="ajax-redirect"></a>6.4.1 AJAX 重定向

应用开发中常常还会遇到另一种情况, 就是 ajax 请求的重定向, 上面所有标准制定的重定向方式都是针对 ajax 请求本身, 即当发送的 ajax 请求收到重定向响应之后, 再次发送 ajax 请求到新的 URL. 而应用常常会碰到另一种情形, 即 ajax 请求收到重定向指令之后将整个页面重定向到新的 URL. 最常见的情况就是当页面的 session 过期之后发送 AJAX 请求需要重定向到登录页面.

这种需求上面所有的重定向标准都不支持, 不过业界有一种约定俗成的做法: 返回 `278` 状态码和 `Location` 响应头. 在前端捕获到 `278` 状态码的时候使用 JavaScript 对 `window.location.href` 赋值来进行页面跳转.

ActFramework 提供了一下 API 对 AJAX 请求返回 `278`:

```java
Controller.Util.redirect(String url);
```

这个方法会首先判断请求是否是 AJAX, 如果是 AJAX 则使用 `278` 状态码, 否则使用 `302` 状态码.

ActFramwork 还提供了一个前端 JavaScript 库: `jquery.ext.js` 来扩展 jQuery 库支持对 `278` 的自动处理. 应用只需要在页面引入该库即可:

```html
<script src="/~/asset/js/jquery.js"></script>
<script src="/~/asset/js/jquery.ext.js"></script>
```

#### <a name="forward"></a>6.4.2 转发 (Forward)

Forward 严格来讲不属于重定向, 这个 API 实现了 Java Servlet 中的 `RequestDispatcher.forward(String url)` 语义:

```java
Controller.Util.forward(String url);
```

调用上面的 API 不会向请求方返回重定向响应, 而是通过 URL 解析出新 URL 的处理器并将请求路由过去.

### <a name="response-status-code"></a>6.5 响应状态码

ActFramework 遵循 HTTP 标准定义的语义自动设置返回响应的 HTTP 状态码:

| 状态码 | 返回条件 |
| --- | --- |
| 200 Okay | 一般正常返回 |
| 201 Created | 对 POST 请求的正常返回 |
| 204 No Content | 请求处理方法没有返回类型声明 |
| 400 Bad Request | 应用抛出 `IllegalArgumentException` |
| 400 Bad Request | 应用抛出 `IndexOutOfBoundsException` |
| 400 Bad Request | 应用抛出 `ValidationException` |
| 404 Not Found | 路由表中没有找到对应请求处理方法 |
| 404 Not Found | 请求处理方法有返回类型声明, 但返回值为 `null` |
| 409 Conflict | 应用抛出了 `IllegalStateException` |
| 500 Server Error | 应用抛出了其他未处理 `Exception` |
| 501 Not Implemented | 应用抛出了 `UnsupportedOperation` |

通过 API 调用返回状态码:

```java
Controller.Util.ok(); // send back 200 Okay
Controller.Util.created(); // send back 201 Created
Controller.Util.created(String); // send back 201 Created with new resource location URL
throw Controller.Util.NO_CONTENT; // send back 204 No Content
Controller.Util.badRequest(); // send 400 Bad request
Controller.Util.notFound(); // send 404 Not Found
Controller.Util.conflict(); // send 409 Conflict
```

在请求处理方法上加注解指定返回状态码:

```java
@ResponseStatus(H.Status.OK)
@PostAction("/users")
public void createUser(User user) {
    dao.save(user);
}
```

以上代码强制将对 POST 请求默认的 201 Created 改为 200 Okay.

### <a name="cache-response"></a> 6.6 缓存响应.

应用可以使用 `@CacheFor` 注解来缓存响应. 该注解接受一下参数:

* `value` - 缓存有效期时长 - 以秒为单位; 默认值为 3600, 即 1 小时
* `id` - CacheFor 的缓存 ID, 如忽略则使用控制器类名 + 请求处理方法名为 ID
* `keys` - 用于构造缓存 key 的请求参数名字数组, 如忽略则使用所有请求参数生成缓存 key
* `supportPost` - 是否缓存 POST 响应, 默认为 false
* `usePrivate` - 当设置为 `true` 的时候, 生成的 `Cache-Control` 头会使用 `private`, 否则使用 `public`. 默认为 `false`
* `noCacheControl` - 当设置为 `true` 的时候不会生成 `Cache-Control` 头. 默认为 `false`

下面是一个使用 `@CacheFor` 的例子:

<a name="s6_6a"></a>

```java
// snippt 6.6a
@GetAction("users")
@CacheFor
public Iterable<User> search(String q, User.Dao dao) {
    return dao.list(q);
}
```

以上代码将响应缓存 1 小时, 缓存的 key 和 请求参数 `q` 的值相关. 也就是如果下次请求的 `q` 有变化, 不会导致返回以前的缓存结果.

**注意** 缓存的 key 生成除了和请求参数值相关,还和一下因素相关:

1. 根据 User-Agent 头判断请求是否来自移动设备还是其他 - 对来自移动设备的请求响应和其他设备的请求响应用不同的缓存 key
2. 请求的 `Accept` 头 - 对不同的响应格式使用不同的缓存 key

### <a name="clear-cache-response"></a> 6.6.1 清除响应缓存

有的时候可能需要从程序中清除缓存, 以便让新的数据立刻生效. 这个时候可以通过 `CacheFor.Manager` 来清除缓存. 下面的 pseudo 代码可以演示这种场景:

<a name="s6_6_1a"></a>

```java
// snippet 6.6.1a
public class PostcodesService {

    // CacheFor 的缓存 ID
    private static final String CACHE_FOR_ID = "postcode-diff-report";

    @NoBind
    private Map<String, Object> report;

    @Inject
    private CacheFor.Manager cacheForManager;

    // 提供 postcodes geolocation 数据变更报表. 该接口使用 @CacheFor 标注为
    // 需要缓存响应.
    @GetAction("/postcodes")
    @CacheFor(id = CACHE_FOR_ID)
    public Map<String, Object> downloadReport() {
        return report;
    }

    private void calculateDiffReport() {
        // 清除 CacheFor 缓存
        cacheForManager.resetCache(CACHE_FOR_ID);
        // calculate postcodes diff report
        ...
    }

    public void save(List<PostCode> postCodeList) {
        backupCurrent();
        List<PostCode> sorted = C.newList(postCodeList).sorted();
        IO.write(JSON.toJSONString(sorted)).to(LIST_CURRENT);
        calculateDiffReport();
    }

    private void backupCurrent() {
        if (LIST_CURRENT.exists()) {
            IO.write(LIST_CURRENT).to(LIST_LAST);
        }
    }

    @Every("1d")
    @Command(name = "postcodes.reload", help = "reload postcode geolocation data from Aus Post")
    public void downloadFromAusPost() {
        // 从 AusPost 服务下载 postcodes geolocation 数据
        ...
        save(postCodeList);
    }
    ...
}
```

### <a name="async-response"></a> 6.7 异步响应.

当请求需要较长时间来完成的时候, 可以使用 `@act.util.Async` 来表示请求处理器为异步, 同时应用可以注入一个 `act.util.ProgressGauge` 对象用于跟踪处理进度:

<a name="s6_7a"></a>

```java
// snippt 6.7a
@Async
@PostAction("processOrders")
public void processOrders(ProgressGauge gauge) {
    int orderCount = getOrderCount();
    gauge.updateMaxHint(orderCount);
    try {
        for (int i = 0; i < orderCount(); ++i) {
            processOrder(i);
            gauge.step();
        }
    } finally {
        gauge.markAsDone();
    }
}
```

当框架发现某个请求处理函数被标注为 `@Async` 的时候会生成一个 Job 来执行该函数, 同时返回 jobId:


<a name="s6_7b"></a>

```json
{"jobId": "2k9b0d5iaC"}
```

前端应用可以使用 `jobId` 来获取该 Job 的执行情况. 有两种方式:

#### <a name="track-job-with-query"></a> 6.7.1 使用 GET 请求来获取 Job 执行情况:

<a name="s6_7_1a"></a>

```javascript
// 注意, 页面必须引入 `/~/asset/js/jquery.js` 文件
$.getJSON('/~/jobs/' + jobId + '/progress', function(data) {
    console.log(data)
})
```

执行上面的代码会获得下面的数据结构:


<a name="s6_7_1b"></a>

```json
{
  "currentSteps": 143,
  "destroyed": false,
  "done": false,
  "id": "2k9b0d5iaC",
  "maxHint": 1000,
  "progressPercent": 14
}
```

#### <a name="track-job-with-ws"></a> 6.7.2 链接到 websocket 端口让系统自动推送 Job 执行情况:

<a name="s6_7_2a"></a>

```javascript
// 注意, 页面必须引入 `/~/asset/js/jquery.js` 和 `/~/asset/js/jquery.ext.js` 文件
var ws = $.createWebSocket('/~/ws/jobs/' + jobId + '/progress')
ws.onmessage = function(frame) {
    var gauge = JSON.parse(frame.data).act_job_progress
    console.log(gauge)
}
```

上面的代码可以让系统在 Job 状态发生变化的时候自动推送到前端, 数据结构和上节中的完全一致

[返回目录](index.md)
