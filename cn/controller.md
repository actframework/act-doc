# <a name="chapter_controller">第五章 控制器, 请求处理方法与响应返回

* [1 介绍](#intro)
* [2 请求与响应](#req_resp)
	* [2.1 请求](#req)
	* [2.2 响应](#resp)
* [3 Session 和 Flash](#session_flash)
	* [3.1 SessionMapper](#session_mapper)
	* [3.2 SessionCodec](#session_codec)
* [4 ActionContext](#context)
* [5 控制器与请求处理方法](#controller_request-handler)
	* [5.1 请求方法参数]
	* [5.2 控制器的依赖注入]
	* [5.3 单例还是多例]
* [6 参数绑定](#param-binding)
	* [6.1 数据来源]
	* [6.2 简单类型绑定]
		* [6.2.1 基本类型与包装类型]
		* [6.2.2 字串]
		* [6.2.3 枚举]
		* [6.2.4 Locale]
	* [6.3 日期数据绑定]
	* [6.4 上传文件绑定]
	* [6.5 集合类型绑定]
	* [6.6 POJO 绑定]
	* [6.7 自定义数据绑定]
		* [6.7.1 StringValueResolver](#str-val-resolver)
		* [6.7.2 Binder](#binder)
	* [6.8 绑定参数校验]
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
	* [7.3 设定 HTTP Header]
		* [7.3.1 Content-Type]
		* [7.3.2 设定其他 HTTP Header]
* [8 异步返回]

## <a name="intro"></a>介绍

控制器 (Controller) 和响应返回是 MVC 中的 "C" 和 "V" 部分, 也是整个框架的核心. ActFramework 在方面提供了完善的支持和一些独到的设计

下面是一个简单的控制器代码:

```java
package com.proj;

import org.osgl.mvc.GetAction;

public class ControllerDemo {
	@GetAction
	public void home() {}
}
```

上面的代码中 `@GetAction` 建立了从 `HTTP GET /` 请求到 `ControllerDemo.home()` 方法的映射. 当收到该请求时, `ControllerDemo.home()` 方法被调用, 并生成响应返回请求端. 注意到该方法没有执行任何指令, 框架会依据情况自动选择返回逻辑:

1. 如果能找到 resources/rythm/com/proj/ControllerDemo/home.html 则用这个模板文件生成响应内容并返回. 否则
2. 返回一个没有内容的 200 Okay 状态响应

1. **控制器**. 控制器是指一个包括了若干请求请求处理方法的Java类
    
    **注意** ActFramework并不要求控制器集成某个特定的类，也不要求控制器加上某个特定注解

1. **请求处理方法** 指某个方法提供了一定的逻辑代码响应发送到特定路径的请求。简单的说如果在应用运行的时候有路由条目配置到某个方法，该方法即为请求处理方法。**注意** 请求处理方法可以是静态方法也可以是虚方法

## <a name="req_resp"></a>2. 请求与响应

HTTP 请求与响应是 Web 应用的输入和输出, 是所有 web 编程框架的核心数据结构。

Servlet 架构使用 `HttpServletRequest` 和 `HttpServletResponse` 两个类来封装 HTTP 请求与响应. 在 Servlet 刚刚开始的时候, Java Web 编程是围绕这两个类进行的, 应用开发人员必须手动从 `HttpServletRequest` 中获取请求参数, Header 变量等信息, 然后手动讲字串拼接并输出到 `HttpServletResponse` 响应对象提供的 `OutputStream`. 这并不是一种很好的开发体验. 后来慢慢出现了 JSP, Velocity 等模板技术, 让输出的处理变得非常方便. 但是请求参数解析的问题依然存在, 知道后来出现的 SpringMVC, PlayFramework 等框架提供了参数绑定特性. ActFramework 作为后来者, 立据前者肩头, 无疑在这方面提供更强大的支持, 让 Web 编程过程变得前所未有的简便.

虽然提供了各种高层封装手段, 在少数情况下, 开发人员可能还是需要直接对请求和输出进行操作. ActFramework 使用 [osgl-http](https://github.com/osglworks/java-http) 提供的 `H.Request` 类来封装 HTTP 请求, `H.Response` 则封装了 HTTP 响应对象。

### <a name="req"></a>2.1 H.Request 请求对象

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

### <a name="resp"></a>2.2 H.Response 响应对象

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

因为 HTTP 是无状态服务, 如果需要在多次请求中跟踪用户与服务的交互信息, 需要提供某种形式的状态存储. ActFramework 使用 `H.Session` 和 `H.Flash` Scope 提供请求状态存取服务. `H.Session` 和 `H.Flash` 均为应用提供一下方法:

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

和 Servlet 架构的 `HttpSession` 不同, `H.Session` 对象没有存放在服务器端, 而是以 cookie 或者 header 的方式存放在客户端. ActFramework 依据此特性实现了无状态的应用服务器架构, 支持线性增长的横向扩展. 

ActFramework 对 Session/Flash 的处理流程如下图所示:

![session-flow-chart](https://user-images.githubusercontent.com/216930/38778305-5bf285e2-40fb-11e8-8f65-98a433dd039e.png)

1. 处理请求
	1.1 ActFramework 使用 `SessionMapper` 将请求中的某个特定 Cookie 或者 Header 映射为一个字串
	1.2 然后使用 `SessionCodec` 将字串解析为 `H.Session` 或者 `H.Flash` Scope 对象
2. 处理响应
	2.1 ActFramework 使用 `SessionCodec` 将 `H.Session` 或者 `H.Flash` Scope 对象打包进一个字串
	2.2 然后使用 SessionMapper 将该字串映射到响应特定 Cookie 或者 Header 上

### <a name="session_mapper"></a>3.1 `SessionMapper`

`SessionMapper` 负责将序列化之后的 session/flash 字串映射到响应上, 以及从请求中获取 session/flash 字串. 具体来说 ActFramework 内置两大类型的 SessionMapper:

#### <a name="cookie_session_mapper"></a>3.1.1 `CookieSessionMapper`

`CookieSessionMapper` 将 session 字串写入特定名字的 cookie 之中:

1. session cookie 名字为 ${app-short-id}-session; flash cookie 名字为 ${app-short-id}-flash
	* 关于 `app-short-id` 的详细内容,参见[启动手册](reference/bootstrap.md#short_id)
2. cookie path: `/`
3. cookie domain: 当 localhost 为 host 时, 为空值, 否则为 host 配置
4. httpOnly: true
5. secure: `http.secure` 的配置值
6. value: 序列化之后的 session 或者 flash 字串
7. ttl: `session.ttl` 配置, 默认为 60 * 30, 即半小时

#### <a name="header_session_mapper"></a>3.1.1 `HeaderSessionMapper`


### <a name="context"></a>1.2 ActionContext

在 `H.Request` 和 `H.Response` 之外 ActFramework 还提供了一个更加方便的封装: ActionContext

### <a name=""></a>1.2 控制器与请求处理方法

**小贴士** 尽管控制器不需要继承任何类，ActFramework推荐你的控制器继承`act.controller.Controll.Util`类，这样你可以在你的控制器中方便的使用各种工具方法。当你的控制器已经继承了其他类的时候，你可以使用`import static`来实现相同的功能：

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
