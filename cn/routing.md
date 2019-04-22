# <a name="chapter_routing">第四章 路由

本篇介绍 ActFramework 的路由表以及路径变量的处理规则.

## <a name="route_mapping"></a>1. 路由表

路由是从 (HTTP Method, URL path) 到响应器的映射定义. 在某个特定端口上所有的路由构成该端口上的路由表. ActFramework 允许定义一个默认 HTTP 端口和若干命名端口. 这里先介绍默认 HTTP 端口上的路由表配置, 对于命名端口的配置参见[第三节](#named_port).

ActFramework 应用可以通过不同的方式来定义路由映射, 包括在请求处理方法上标记路由注解和是用路由表文件.

### <a name="annotation"></a>1.1 路由注解

ActFramework 使用 [osgl-mvc](https://github.com/osglworks/osgl-mvc) 提供的路由注解:

1. `org.osgl.mvc.annotation.Action` - 定义全部或指定 HTTP 请求方法的路由
1. `org.osgl.mvc.annotation.GetAction` - 定义 HTTP GET 请求路由
1. `org.osgl.mvc.annotation.PostAction` - 定义 HTTP POST 请求路由
1. `org.osgl.mvc.annotation.PutAction` - 定义 HTTP PUT 请求路由
1. `org.osgl.mvc.annotation.DeleteAction` - 定义 HTTP DELETE 请求路由

示例代码：

```java

/**
 * 定义 GET /profile/{id} 到 getProfile(String) 方法的路由
 */
@GetAction("/profile/{id}")
public Profile getProfile(String id) {
    return dao.findById(id);
}

/**
 * 定义 POST /profile 到 createProfile(Profile) 方法的路由
 */
@PostAction("/profile")
public void createProfile (Profile profile) {
    dao.save(profile);
}

/**
 * 定义 PUT /profile/{id}/address 到 updateAddress(String, Address) 方法的路由
 */
@PutAction("/profile/{id}/address")
public void updateAddress(String id, Address address) {
    Profile profile = dao.findById(id);
    notFoundIfNull(profile);
    profile.setAddress(address);
    profile.update(profile);
}

/**
 * 定义 DELETE /profile/{id} 到 deleteProfile(String) 方法的路由
 */
@DeleteAction("/profile/{id}")
public void deleteProfile(String id) {
    dao.deleteById(id);
}
```

**小贴士**: 当某个响应方法处理多种不同的HTTP方法请求时可以使用`@Action`注解：

```java
@Action("/", methods = {H.Method.GET, H.Method.POST})
public void home() {}
```

**注意** 如果 `@Action` 注解没有提供 `methods` 参数, 则将该路径上的路由映射到所有的 HTTP 方法

**小贴士**: 你可以通过注解将不同的请求路径映射到同一个响应方法上:

```java
@GetAction({"/profile/{id}", "/profiles/{id}"})
public Profile getProfile(String id) {
    return dao.findById(id);
}
```

依据上例的配置`getProfile`可以处理下面两种请求:

1. `/profile/<profile_id>`
2. `/profiles/<profile_id>`

#### <a name="urlcontext"></a>1.1.1 URL context

如果某个控制器类的多个方法都在某一个 URL context 下面, 可以在控制器类上添加 `@UrlContext` 注解来简化方法上的 `@XxxAction` 注解定义:

未使用 `@UrlContext` 的代码:

```java
public class OrderAdmin {
    @GetAction("/admin/orders")
    public List<Order> list() {
		...
    }

    @GetAction("/admin/orders/{id}")
    public Order get(String id) {
		...
    }

    @PostAction("/admin/orders")
    public void create(Order order) {
		...
    }
}
```

使用 `@UrlContext` 的代码:

```java
@UrlContext("/admin/orders")
public class OrderAdmin {
    @GetAction
    public List<Order> list() {
		...
    }

    @GetAction("{id}")
    public Order get(String id) {
		...
    }

    @PostAction
    public void create(Order order) {
		...
    }
}
```

##### <a name="absolute_vs_relative"></a>1.1.1.1 绝对路径与相对路径

使用 `@UrlContext` 注解来简化 Action 方法注解的时候需要注意一个规则: 如果方法注解上的 URL 路径是绝对路径, 即使用 `/` 开头的路径, 则该方法上的路径不会和 `@UrlContext` 指定路径相合并, 例如:

```java
@UrlContext("/admin/orders")
public class OrderAdmin {
	...
	@GetAction("/login")
	public void loginForm() {}
}
```

上例所示的 `/login` 是一条绝对路径, 因此 `loginForm`
在路由表中的路径不是 `/admin/orders/login` 而是 `/login`.

##### <a name="urlcontext_inheritance"></a>1.1.1.2 URL context 继承

在上面的例子中我们对 `OrderAdmin` 控制器类使用了
`@UrlContext("/admin/orders)` 来指定其 URL context,
假设我们又开发了一个 `ProductAdmin` 类,
我们当然可以对其加上 `@UrlContext("/admin/products")
的注解. 还有一种办法是提取一个公共类 `AdminBase`, 让
`OrderAdmin` 和 `ProductAdmin` 控制器从 `AdminBase` 继承:

```java
@UrlContext("/admin")
public class AdminBase {}
```

```java
@UrlContext("orders") // real context will be `/admin/orders`
public class OrderAdmin extends AdminBase {...}
```

```java
@UrlContext("products") // real context will be `/admin/products`
public class ProductAdmin extends AdminBase {...}
```

在上面的代码中 `OrderAdmin` 和 `ProductAdmin` 都继承了
`AdminBase` 的 URL context: `/admin`, 和自己定义的 `orders`
以及 `products` 合并, 最终的 URL context 则分别为:
`/admin/orders` 和 `/admin/products`.

**注意**
上面一节讲述的绝对路径和相对路径的规则同样适用与 URL context 的继承, 因此, 假设 `OrderAdmin` 上的 `@UrlContext` 注解参数为 `/orders`, 就不能和 `AdminBase` 的 `/admin` 合并.

### <a name="route_table"></a>1.2 路由表文件

除了路由注解外, ActFramework
也支持使用路由表文件来配置路由, 默认路由表文件为
`/src/main/resources/routes.conf`. 和[1.1 节
路由注解](#annotation)中的示例代码相对应的路由表文件内容为 (假设控制器的类名为`com.mycom.myprj.MyController`):

```
GET /profile/{id} com.mycom.myprj.MyController.getProfile
POST /profile com.mycom.myprj.MyController.createProfile
PUT /profile/{id}/address com.mycom.myprj.MyController.updateAddress
DELETE /profile/{id}
```

规则：路由表条目由下面三个部分组成：

```
(GET|POST|DELETE|PUT|*)     <path>     <handler>
-----------------------
   HTTP请求方法
                            -------
                            请求路径
                                       -----------
                                        响应器规范
```

在上面的路由表文件示例中响应器规范部分是控制器类名加上请求处理方法名组成. 下面介绍另一种响应器规范的定义方式: 路由指令[修饰符]:参数

#### <a name="route_directive"></a>1.2.1 路由表指令与修饰符

```
# 对于 GET /tmp 请求加载 /tmp 文件系统目录下的文件
GET /tmp file[external]:/tmp

# 对于 GET /public 请求加载 /public 资源下的文件
GET /public resource:/public

# 对于 GET /3215430325 请求返回 "some-code" 响应
GET /3215430325 echo:some-code

# 对于 GET /google 请求重定向到 https://google.com
GET /google redirect:https://google.com
```

ActFramework内置四种路由表指令

1. `echo`: 直接发送 `echo` 参数
1. `file`: 发送静态文件
1. `resource`: 发送类加载器能够获得的资源 (ClassLoader.getResource())
1. `redirect`: 发送重定向响应

`echo` or `redirect` 很简单, 但 `file` 和 `resource` 两种指令需要详细介绍一下:

##### <a name="file_resource"></a> 1.2.1.1 `file` 和 `resource` 指令

* `file` 通过 `new File` 来加载文件
* `resource` 通过 `ClassLoader.getResource` 来获得资源

从开发者的角度来看, 简单地说, `file` 
从项目目录开始寻找文件 (在没有 [external] 
修饰符的情况下). `resource` 则从 `main/src/resources` 
目录开始寻找资源文件. 在运行时, `resource` 可以访问 `jar` 文件中的资源. 

`file` 和 `resource` 指令都可以指定目录或者文件:

```
GET /file/dir file:/dir
GET /file/file file:/file.txt
GET /rsrc/dir resource:/dir
GET /rsrc/file resource:/file.txt
```

对于上面的路由配置, 需要项目目录有一下结构:

```
├── dir -------------------------- 由 file 指令访问
│   ├── bar.txt
│   └── foo.txt
├── file.txt --------------------- 由 file 指令访问
├── src
│   ├── main
│   │   └── resources
│   │       ├── dir -------------- 由 resource 指令访问
│   │       │   ├── bar.txt
│   │       │   └── foo.txt
│   │       ├── file.txt --------- 由 resource 指令访问
```

访问示例

```
GET /file/dir/bar.txt # 访问 /dir/bar.txt 文件
GET /file/file        # 访问 /file.txt 文件
GET /rsrc/dir/foo.txt # 访问 /src/main/resources/dir/foo.txt 文件
GET /rsrc/file        # 访问 /src/main/resources/file.txt 文件
```

**提示** `file` 和 `resource` 
指令的参数部分可以是绝对路径也可以是相对路径,两者均指向同一资源, 下面两套路由作用完全一样:

路由配置 1:

```
GET /file/dir file:/dir
GET /file/file file:/file.txt
GET /rsrc/dir resource:/dir
GET /rsrc/file resource:/file.txt
```

路由配置 2:

```
GET /file/dir file:dir
GET /file/file file:file.txt
GET /rsrc/dir resource:dir
GET /rsrc/file resource:file.txt
```

**提示** 建议开发人员尽量使用 `resource` 指令, 原因在于:

* 更加安全 - `resource` 访问的资源永远在项目的管理范围之内
* 更加方便 - `resource` 访问的资源自动装配进 jar 文件, 
而 `file` 访问的资源则需要开发人员改写 pom.xml 文件以确保其进入了发布包

**指令修饰符**

在上面的路由指令中注意到有一个特殊的指令形式: `file[external]` , 这里 `file` 是路由指令, 而 `[external]` 则是指令修饰符. 目前 ActFramework 支持三种指令修饰符:

1. `[authenticated]` - 表示请求需经过 session resolving 过程, 
此过程可能(会有插件)对请求进行认证. 如果没有出现, 则直接交给相应的指令响应器.
1. `[external]` - 仅对 `file` 指令有效, 表明参数为项目外部文件
1. `[throttled]` - 表示对该资源的请求会被限流

语法上任何路由指令都可以和零个或者多指令修饰联合, 例如:

```
GET /static/protected resource[authenticated,throttled]:/asset/protected
```

上面的路由表达的意思是: 发送到 `/static/protected` 的 
`HTTP GET` 请求由资源响应器处理 (resource 指令 + 
/asset/protected payload), 处理的时候需要经过认证过程 
([authenticated] 修饰符), 并对该请求端点实施流量控制.

虽然应用可以对任何路由指令指定任何修饰符, 
但并非所有的修饰符都能和所有的指令一起生效. 
目前只有 [autenticated] 和 [throttled] 
两种修饰符能和所有的指令联合起效. [external] 修饰符只对 `file` 指令有效果

当 `file` 和 `[external]` 修饰符一起使用的时候, 表明参数为项目外部文件:

```
GET /syslog file[external]:/var/log/syslog
```

**注意** 如果一定要使用 `file[external]` 组合一定要非常小心, 防止安全信息的泄漏.

#### <a name="profile_based_routes"></a>1.2.2 基于环境的路由表

和其他所有的设置一样, ActFramework 下路由配置也可以是基于不同环境的.

如果 `routes.conf` 文件在 `resources/` 目录下, 
其中配置的路由映射在所有的环境下均起效. 
如果有一个 `routes.conf` 文件在 `resources/conf/uat/` 下面, 
其中配置的路由映射仅在应用运行在 `uat` 环境下才有效果.

### <a name="route_conflict"></a>1.3 系统内置路由

ActFramework 内置了一些服务帮助简化应用开发:

* GET /~/apibook - 访问应用的 API 文档 - 仅在开发模式有效
* GET /~/asset - 访问 ActFramework 内置 css/js 资源, 主要用于 
ActFramework 在开发模式下的错误页面
* POST /~/i18n/locale - 提供给应用使用, 作用是改变当前用户会话的 Locale
* POST /~/i18n/timezone - 提供给应用使用, 作用是改变当前用户会话的时区
* GET /~/info - 显示应用信息
* GET /~/job/{id}/progress - websocket 端口, 让应用查询特定后台任务的进度
* GET /~/pid - 显示应用进程号
* GET /~/version - 显示应用版本信息
* GET /~/zen - 显示箴言列表

关于这些内置响应器的具体作用和使用方法, 参考[系统内置服务](reference/builtin-handler.md)

### <a name="route_conflict"></a>１.4 路由冲突的处理

如果应用存在映射冲突, 即同一个服务端点映射到了不同的响应器上，ActFramework 会依据一下规则处理：

| 已注册路由映射来源 | 新注册路由映射来源 | 冲突处理方式 |
| --- | --- | --- |
| 系统内置 | 系统内置 | N/A |
| 路由注解 | 系统内置 | N/A |
| 路由表 | 系统内置 | 忽略新路由映射 |
| 系统内置 | 路由注解 | 报告错误 |
| 路由注解 | 路由注解 | 报告错误 |
| 路由表 | 路由注解 | 忽略新路由映射 |
| 系统内置 | 路由表 | 报告错误 |
| 路由注解 | 路由表 | 覆盖已注册路由映射 |
| 路由表 | 路由表 | 发出警告并覆盖已注册路由映射 |

**注意** 目前对覆盖系统内置路由映射的处理有问题, 
参见 [#598](https://github.com/actframework/actframework/issues/598)

## <a name="path_var"></a> 2. 路径变量

ActFramework 支持路由中的路径变量, 下面是一个简单的使用路径变量的例子:

```java
// sample: /users/5
@GetAction("/users/{userId}") 
public User getUserById(int userId) {
   ...
}
```

一个稍微复杂一点的例子:

```java
// sample: /books/978-3-16-148410-0/chapters/3
@GetAction("/books/{bookId}/chapters/{chapterNo}")
public Chapter getChapter(String bookId, int chapterNo) {
	...
}
```

或者这样:

```java
// sample: /spot/nearest/latitude=-33.8670522,longitude=151.1957362,distance=20
@GetAction("/spot/nearest/latitude={latitude},longitude={longitude},distance={distance}")
public List<Spot> searchRange(double latitude, double longitude, int distance) {
	...
}
```

### <a name="regex"></a> 2.1 正则表达式

ActFramework 支持在路径中使用正则表达式. 有两种方式来表达正则:

方式1: 

```java
// sample: /service/1234
@GetAction("/service/{<[0-9]{4}>accessCode}")
public void service(int accessCode) {
	...
}
```

方式2:

```java
// sample: /service/1234
@GetAction("/service/accessCode:[0-9]{4}")
public void service(int accessCode) {
	...
}
```

推荐使用第一种方式. 只有第一种方式可以用在下面这种路径设计:

```java
// sample: /service/code=1234
@GetAction("/service/code={<[0-9]{4}>accessCode}")
public void service(int accessCode) {
	...
}
```

正则表达式可以用来分派请求到不同的处理器:

```java
@GetAction("/service/{<[0-9]{4}>accessCode}")
public void service1(int accessCode) {
	...
}

@GetAction("/service/{<[0-9]{6}>accessCode}")
public void service2(int accessCode) {
	...
}
```

形式上两个处理方法都处理 `/service/<accessCode>` 这样的请求, 但是其正则规范是不一样的, 所以

* `GET /service/1234` 分派到 `service1` 方法
* `GET /service/12345` 分派的 `service2` 方法

而下面的这些请求都会的到 404 响应:

* `GET /service/123`
* `GET /service/123456`

**提示** 尽量避免使用正则规范来分派请求, 这样会让代码变得非常晦涩

#### <a name="regex-macro"></a> 2.1.1 正则表达式宏

如果同样的正则表达式出现很多次, 可以使用正则表达式宏.

1. 首先在应用的配置文件中加入宏定义:

```
router.macro.__access_code__=[0-9]{4}
```

2. 在 URL 路径中使用宏定义:

```java
@GetAction("/service/{<__access_code__>accessCode}")
public void service(int accessCode) {
	...
}
```

或者

```java
@GetAction("/service/accessCode:__access_code__")
public void service(int accessCode) {
	...
}
```

**注意** 路由使用的正则表达式宏名字必须是以 `__` 开头并结尾.

### <a name="dyna_var"></a> 2.2 动态变量

对于下面的请求, 没有办法使用上面讲到的任何方式来映射到一个处理器:

```
GET /data/k1=v1
GET /data/k1=v1,k2=v2
GET /data/k1=v1,k2=v2,...
```

这个时候需要使用 ActFramework 提供的动态变量特性:

```java
@GetAction("/data/{data}")
public void handleData(Map<String, String> data) {}
```

这里 `{data}` 是动态变量, 可以将 `k1=v1,k2=v2,...` 这样的部分放进一个 `Map` 结构中.

### <a name="dyan_path"></a>2.3 可变长路径

在 URL path 的最后部分如果是 `/...` 则创建了一条可变长路径. 有两项功能

#### <a name="seo_path"></a>2.3.1 用于生成 SEO 路径

典型的例子是 StackOverflow 的 URL, 例如 `"https://stackoverflow.com/questions/46483151/how-to-use-actframework-with-jwt-auth-and-social-login"`, 其中 `"https://stackoverflow.com/questions/46483151"` 才是路由的关键, 后面的 `"how-to-use-actframework-with-jwt-auth-and-social-login"` 是为 SEO (搜索引擎优化) 服务的, 方便搜索引擎的爬虫为该 URL 建立索引.

如果需要在应用中实现这种特性, 可以这样写路由:

```java
@Get("/questions/{question}/...")
public void renderQuestionPage(@DbBind @NotNull Question question) {
    render(question);
}
```

如果希望像 StackOverflow 那样把 `"https://stackoverflow.com/questions/46483151/aaa"` 重新定向到 `"https://stackoverflow.com/questions/46483151/how-to-use-actframework-with-jwt-auth-and-social-login"`, 则需要稍作处理:

```java
@Get("/questions/{question}/...")
public void renderQuestionPage(@DbBind @NotNull Question question, String __path) {
    redirectIfNot(S.eq(question.getDescriptionPath(), __path), "/questions" + question.getId() + question.getDescriptionPath());
    render(question);
}
```

上面的代码中, 如果收到的 URL 是 `/questions/46483151/aaa`, 那 `renderQuestionPage` 会拿到两个参数: 

1. `question`: 对应与 `46483151` 的 Question 数据对象
2. `__path`: `"/aaa"`

假设 `question` 数据对象的 `descriptionPath` 属性为 `"/how-to-use-actframework-with-jwt-auth-and-social-login"`, 那 `redirectIfNot` 中的条件就会为 `false`, 因此重定向会发生, 并重定向到 `/questions/46483151/how-to-use-actframework-with-jwt-auth-and-social-login`. 之后会再次收到请求, 这一次的处理 `__path` 就会变成 `"/how-to-use-actframework-with-jwt-auth-and-social-login"`, 和 `question` 对象的 `descriptionPath` 匹配, 于是会继续下一行 `render(question)` 生成 Question[46483151] 的页面.

**注意**

1. `__path` 变量是系统定义的, 专门为了传递 `"..."` 这种表达的后续路径部分. `__path` 是有两个下划线前缀: `_`
2. `__path` 变量的值总是以 `/` 开头


#### <a name="hierarchical_path"></a>2.3.2 用于创建需要处理请求路径的处理器

另一种使用 `...` 的情况是需要处理路径参数的场合, 比如 `"https://gitee.com/actframework/actframework/blob/master/src/main/java/act/Act.java"
`, 其中的 `"/src/main/java/act/Act.java"` 就是需要请求响应器处理的参数, 处理这样的参数也需要在路径中使用 `"..."`:

```java
@GetAction("/{group}/{prj}/blob/{branch}/...")
public void renderSourcePage(
    @DbBind @NotNull Group group,
    String prj,
    String branch,
    String __path
) {
    Project project = group.getProject(prj);
    ...
}
```

对于请求 `/actframework/actframework/blob/master/src/main/java/act/Act.java`, 上面的 `renderSourcePage` 函数收到的参数为:

* 名为 `actframework` 的 Group 实例
* 名为 `actframework` 的 Project 实例
* 字串 `branch`: `"master"`
* 字串 `__path`: `"/src/main/java/act/Act.java"`

还有一种典型的请求路径处理场合是用户自定义的文件服务器:

```java
@GetAction("/file_server/...")
public Result handle(String __path) {
    File file = new File(BASE_DIR, __path);
    notFoundIfNot(file.exists());
    if (file.isFile()) {
        return download(file); // 下载文件
    } else {
        return render(file); // 生成目录页面
    }
}
```

## <a name="named_port"></a>3. 命名端口

本篇开头的时候提到过命名端口, 这是 ActFramework 
用来管理多个 HTTP 侦听端口的机制. 一个 ActFramework 
应用拥有至少一个 HTTP 侦听端口, 默认端口号为 5460. 
可以通过 `http.port` 配置来设定其他端口号. 如果需要多个 HTTP 侦听端口, 下面是一个例子:

### <a name="config_named_port"></a>3.1 配置命名端口

在应用的配置文件中设置命令端口, 可以配置任意数量的命名端口:

```
namedPorts=admin:8888;plan_a:8899;plan_b:9999
```

在上面的配置中, 定义了三个命名端口, 分别为:

1. `admin`, 端口号是 8888
2. `plan_a`, 端口号是 8899
3. `plan_b', 端口号是 9999

### <a name="use_named_port"></a>3.2 使用命名端口

一旦定义了命名端口, 应用可以将控制器, 或者路由表绑定到指定的命名端口.

#### <a name="use_named_port_in_controller"></a> 3.2.1 将控制器绑定到命名端口

使用 `@act.controller.annotation.Port` 注解将控制器绑定到命名端口, 如下例所示:

```java
@Port({"plan_a", "plan_b"})
public class HelloService {
	@GetAction("/hello")
	public String hello() {
		return "hello";
	}
}
```

上面的代码将 `GET /hello` 
服务端点同时绑定到了两个命名端口: `plan_a` 和 `plan_b`. 因此, 下面两个链接都会返回 `hello` 响应:

```
GET http://localhost:8899/hello
GET http://localhost:9999/hello
```

#### <a name="use_named_port_in_routes_conf"></a> 3.2.2 将路由表配置文件绑定到命名端口

将路由表绑定到命名端口的办法是重命名路由表文件:

* routes.conf # 默认端口路由表
* routes.admin.conf # admin 端口路由表
* routes.plan_a.conf # plan_a 端口路由表
* routes.plan_b.conf # plan_a 端口路由表

**小贴士** 命名端口路由表文件也适用与基于环境的配置

#### <a name="use_named_port_for_builtin_service"></a> 3.2.2 将内置服务绑定到命名端口

截至到 act-1.8.7 尚不支持将内置服务绑定到命名端口

## <a name="jax-rs"></a>4. JAX-RS 路由

ActFramework 通过 [act-jax-rs](https://github.com/actframework/act-jax-rs) 插件提供对 JAX-RS 路由的支持

TBD

[返回目录](index.md)
