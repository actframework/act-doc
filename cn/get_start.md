# 第一章 ActFramework简介

## <a name="prerequisites"></a>1. 准备工作

你需要JDK和Maven来使用ActFramework创建应用程序. 因为需要Maven通过网络从中央库下载依赖包，一个良好的互联网链接也是必须的

1. JDK (Java Development Kit), version 1.7或以上
1. Maven (Project Management Tool), version 3.5或以上
(目前仅支持JDK1.7，JDK1.8)

### <a name="install-jdk"></a>1.1 安装JDK

从[Java官网](http://www.oracle.com/technetwork/java/javase/downloads/index.html)下载JDK并安装.

**小贴士** 如果是 Debian 用户，可以参考更简便的 [Digitalocean 的 JDK 安装教程](https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-ubuntu-16-04)

### <a name="install-maven"></a>1.2 安装Maven

从[Maven官网](http://maven.apache.org/)下载Maven. 参照[教程](http://maven.apache.org/install.html)安装Maven到你的操作系统

## <a name="create_hello_world_app"></a>2. 创建一个"Hello world"应用程序

创建 ActFramework 应用项目最简便的方法是使用 maven archetype:

### <a name="use_maven_archetype"></a>2.1 使用 maven archetype 生成应用框架

```
mvn archetype:generate -B \
    -DgroupId=com.mycom.helloworld \
    -DartifactId=helloworld \
    -DappName=helloworld \
    -DarchetypeGroupId=org.actframework \
    -DarchetypeArtifactId=archetype-quickstart \
    -DarchetypeVersion=1.8.21.0
``` 

**注意** 对于真正的项目, 你可能需要替换上面的 `groupId`, `artifactId` 以及 `appName`

运行上述命令之后你的项目目录应该是下面的样子: 

```
helloworld/
├── .gitignore
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── mycom
    │   │           └── helloworld
    │   │               └── AppEntry.java
    │   └── resources
    │       ├── com
    │       │   └── mycom
    │       │       └── helloworld
    │       │           └── .version
    │       ├── logback.xml
    │       └── rythm
    │           └── com
    │               └── mycom
    │                   └── helloworld
    │                       └── AppEntry
    │                           └── home.html
    └── test
        └── java
            └── com
                └── mycom
                    └── helloworld
```

这是已经是一个完整的可以运行的应用项目了. 下面介绍如何运行新建项目.

### <a name="run_app"></a>2.2 启动应用

进入 `helloworld` 项目目录，键入 `mvn compile act:run` 即可启动应用，在控制台上能看到类似下面的信息：

```
       _           _            _    _        _  
 |_|  |_  |   |   / \  \    /  / \  |_)  |   | \ 
 | |  |_  |_  |_  \_/   \/\/   \_/  | \  |_  |_/ 
                                                 
              powered by ActFramework r1.8.7-2f28

 version: v1.0-SNAPSHOT-180410_1739
scan pkg: com.mycom.helloworld
base dir: /tmp/1/helloworld
     pid: 3209
 profile: dev
    mode: DEV

     zen: Simple is better than complex.

2018-04-10 17:39:57,264 INFO  a.Act@[main] - loading application(s) ...
2018-04-10 17:39:57,294 INFO  a.a.App@[main] - App starting ....
2018-04-10 17:39:57,762 WARN  a.h.b.ResourceGetter@[main] - URL base not exists: META-INF/resources/webjars
2018-04-10 17:39:57,794 WARN  a.a.DbServiceManager@[main] - DB service not initialized: No DB plugin found
2018-04-10 17:39:59,110 WARN  a.m.MailerConfig@[main] - smtp host configuration not found, will use mock smtp to send email
2018-04-10 17:40:00,046 INFO  a.a.App@[main] - App[helloworld] loaded in 2751ms
2018-04-10 17:40:00,058 INFO  a.a.ApiManager@[jobs-thread-3] - start compiling API book
2018-04-10 17:40:00,088 INFO  o.xnio@[main] - XNIO version 3.3.8.Final
2018-04-10 17:40:00,130 INFO  o.x.nio@[main] - XNIO NIO Implementation Version 3.3.8.Final
2018-04-10 17:40:00,372 INFO  a.Act@[main] - network client hooked on port: 5460
2018-04-10 17:40:00,374 INFO  a.Act@[main] - CLI server started on port: 5461
2018-04-10 17:40:00,377 INFO  a.Act@[main] - app is ready at: http://192.168.1.5:5460
2018-04-10 17:40:00,378 INFO  a.Act@[main] - it takes 4886ms to start the app
```

启动浏览器并打开<a href="http://localhost:5460"><code>http://localhost:5460</code></a> 能看到默认的主页:

![image](https://user-images.githubusercontent.com/216930/38310001-cbc749a2-385e-11e8-8a87-f505ccee767c.png)

### <a name="import_into_ide"></a>3. 将新建项目导入 IDE

基本上所有的IDE都支持maven项目. 下面使用IntelliJ IDEA做演示.

选择`File/Open...` 并导航到生成的项目目录:

![image](https://user-images.githubusercontent.com/216930/38247063-4b1967d6-3787-11e8-9257-d5971ea86acf.png)

点击 Okay 之后打开项目:

![image](https://user-images.githubusercontent.com/216930/38247205-b2e735b4-3787-11e8-8cd3-a47f30713a1f.png)

## <a name="understand_app_entry"></a>4. 理解 AppEntry 类

｀AppEntry｀ 是应用的入口类，也是这个简单应用唯一的类． 打开 AppEntry.java 文件我们看到下面两个方法：

```java
    @GetAction
    public void home(@DefaultValue("World") @Output String who) {
    }

    public static void main(String[] args) throws Exception {
        Act.start();
    }
```

非常明显 `public static void main(String[[])` 方法是整个应用程序的入口函数。这个方法的实现也非常简单，就是调用 `act.Act.start()` 即可。具有这个方法的类，称为应用入口类。

#### <a name="specify_app_entry"></a>4.1 在 pom.xml 文件中指定应用入口类

应用入口类一旦定义好，需要在 `pom.xml` 中指定其到　`app.entry` 属性:

```xml
<app.entry>com.mycom.helloworld.AppEntry</app.entry>
```

设置这个属性非常关键，act 的 maven 构造工具需要用到这个属性：

1. act-maven-plugin 需要这个属性来决定运行类，否则 `mvn compile act:run` 无法正常工作
2. `act-starter-parent` 需要这个属性来生成最后的运行文件，否则部署包解包后的 `run` 脚本不能正常工作

#### <a name="home_method"></a>4.2 主页响应方法

`home` 方法上有个 `@GetAction` 注解, 未带有任何参数, 其含义为 `@GetAction("/")` , 表示任何发送到 `/` 的请求都将被路由到该方法. 方法有一个参数:

```java
@DefaultValue("World") @Output String who
```

这个参数告诉 ActFramework 从 HTTP GET 请求中找到名为 `who` 的参数, 并将其注入到 `String who` 方法参数中. `@DefaultValue("World")` 的意思是如果没有 `who` 请求参数, 则使用 `World` 作为 `String who` 方法参数的默认值; `@Output` 告诉框架将 `String who` 放进模板输出变量中,对应的模板变量名字为 `who`. 如果不使用 `@DefaultValue("World")` 和 `@Output` 注解, 整个 `home` 方法应该这样表达:

```java
@GetAction("/")
public void home(String who) {
   if (null == who) who = "World";
   renderTemplate(who); // render template and add `who` into template argument list
}
```

下面是 home 方法更加冗长的表达:

```java
@GetAction("/")
public Result home(ActionContext context) {
    String who = context.req().paramVal("who");
   if (null == who) who = "World";
   context.renderArg("who", who);
   return RenderTemplate.get();
}
```

这里可以看出 ActFramework 的一个特点：同样的功能实现可以有不同的表达方式。当然推荐用户使用更加简练的表达，表达力是 ActFramework 设计的一个专注点。

##### <a name="template_path"></a>4.2.1 模板文件路径

`home` 方法中我们并没有看到指定模板文件路径的地方, ActFramework 在程序没有指定模板路径的时候按照下面的规则来寻找模板文件:

```
/src/main/resources/rythm/com/mycom/helloworld/AppEntry/home.html
-------------------
  资源文件根目录
                   rythm
                   ------
                   模板 
                   引擎 
                   id
                         com/mycom/helloworld/AppEntry
                         -----------------------------
                         控制器类的全名
                                                       /home.html
                                                       -----------
                                                       方法名.内容格式后缀
```


##### <a name="specify_template_path"></a>4.2.1.1 通过代码指定模板文件路径

如果模板文件放在其他地方，比如 `resources/rythm/home.html` 则需要使用 `act.controller.Controller.Util.renderTemplate` 方法来指定：

```java
    @GetAction
    public void home(@DefaultValue("World") String who) {
		renderTemplate("/home.html", who);
    }
```

上面的代码中模板路径是用字串字面量 (String literal) 来指定的，这个点非常重要，下面的方式制定模板路径是不行的：

```java
String path = "/home.html";
renderTemplate(path, who);
```

这里虽然使用了 path，但是 ActFramework 依旧会按照默认的方式去寻找木板，因为 `path` 变量不会被解释为模板路径，而是当作参数传递给模板了。

#### <a name="template_content"></a>4.2.2 模板文件内容

ActFramework 使用 [rythm](http://rythmengine.org) 作为默认的模板引擎. Hello world 项目的主页模板内容如下:

```html
<!DOCTYPE html>
<html lang="en">
@args String who
<head>
  <title>Hello World - ActFramework</title>
</head>
<body>
  <h1>Hello @who</h1>
  <p>
    Powered by ActFramework @act.Act.VERSION.getVersion()
  </p>
</body>
</html>
``` 

其中 @args String who 声明该模板用到的模板变量, 该变量可以使用 `@` 引用: `@who` 在模板输出变量 `who` 的值. `@` 还可以引入任何其它变量或者方法, 比如 `@act.Act.VERSION.getVersion()` 在模板上输出 `act.Act.VERSION` 静态变量的 `getVersion()` 静态方法的返回值.

**小贴士** [rythm 官网](http://rythmengine.org) 有详尽的文档可供参考

### <a name="add_req_handler"></a> 4.3 加入请求处理方法

现在加入另一个请求处理方法到`AppEntry.java`文件中，该方法处理发送到 `/bye` 的请求:

```java
    @GetAction("/bye")
    public String sayBye() {
        return "Bye!";
    }
``` 

加完方法后, 切换到你的浏览器打开<a href="http://localhost:5460/bye"><code>http://localhost:5460/bye</code></a>, 你应该能看到如下效果:

![image](https://user-images.githubusercontent.com/216930/38310207-573d6908-385f-11e8-9e06-2a2d28be87cc.png)

**小贴士** 开发模式下, 一旦 ActFramework 项目开始运行就无需重启 (除非引入新的依赖库). 无论是添加/改变源文件, 或者配置文件, 开发人员只需刷新浏览器就能看到更改结果. 这种来源于 PlayFramework v1 的热加载特性让应用的开发变得更加容易.

\newpage
## <a name="anatomy"></a>5. ActFramework应用项目剖析

ActFramework使用标准的maven项目布局来组织文件. 下面是一种常见 Act 应用的目录结构:

```
.
├── pom.xml
├── src
│   ├── main
│   │   ├── java                                -> Java 源码
│   │   │   └── com
│   │   │       └── mycom
│   │   │           └── myprj
│   │   │               ├── AppEntry.java       -> 应用程序入口(提供main()方法)
│   │   │               ├── controller          -> 控制器目录
│   │   │               ├── event               -> 事件和事件响应器目录
│   │   │               ├── mail                -> 邮件发送器目录
│   │   │               ├── model               -> 域模型以及数据访问对象目录
│   │   │               └── util                -> 工具类目录
│   │   └── resources                           -> 资源文件
│   │       ├── asset                           -> 静态资源, 可以直接通过"/asset"访问
│   │       │   ├── css                         -> CSS 文件
│   │       │   ├── img                         -> 图片文件
│   │       │   └── js                          -> Javascript 文件
│   │       ├── conf                            -> 配置根目录
│   │       │   ├── prod                        -> `prod` 产品环境配置
│   │       │   ├── sit                         -> `sit` 系统集成测试环境配置
│   │       │   └── uat                         -> "uat" 用户接受测试环境配置
│   │       ├── messages.properties             -> 国际化资源文件
│   │       ├── routes.conf                     -> 路由表
│   │       └── rythm                           -> Rythm模板根目录
│   │           ├── com
│   │           │   └── mycomp
│   │           │       └── myprj
│   │           │           ├── controller      -> 控制器模板目录
│   │           │           └── mail            -> 邮件发送器模板目录
│   │           └── __global.rythm              -> 全局模板工具
│   └── test
│       ├── java                                -> 单元测试源文件
│       │   └── com
│       │       └── mycom
│       │           └── myprj
│       └── resources                           -> 单元测试资源
└── target                                      -> maven项目构建目录
    ├── dist                                    -> 发布包目录
    └── tmp
        └── uploads                             -> 存放上传文件的临时目录
```

**注意** 根据你的应用程序的包组织方式，你看到的有可能和以上结构有不一样的地方
**注意** asset文件夹可能不会默认被创建，你可以在有需要的时候手动创建。
**注意** asset文件夹拥有默认的路由/asset/

1. 包组织方式完全由项目决定。你的项目中可能使用了`service`包而不是`controller`来存放所有的RESTful控制器类。 而你的业务层也许不是一个`model`包, 而是分布在多个不同的包里. ActFramework在项目文件组织上没有任何限制
1. ActFramework使用`common`配置目录来获取缺省的配置信息。而上例所示的`sit`和`dev`目录则完全由项目决定，你可以使用其他任何名字，你也可以增加另一种配置组，比如`uat`。如果项目不需要多个配置组，使用`common`即可。
1. 如果路由都通过注解方式指定，`routes`文件可以不用提供. 一旦`routes`文件被检测到，其中的条目可以覆盖注解指定路由

## <a name="deploy"></a>6. 打包发布你的应用

使用 maven archetype 生成的项目有完整的 ActFramework maven 工具链支持, 打包发布应用非常简单:

```
mvn clean package
```

运行上面的命令后, maven 会在 `target/dist` 目录下生成发布包:

```
-rw-rw-r-- 1 luog luog 20M Apr 11 16:21 helloworld-1.0-SNAPSHOT-b180411_1621.zip
```

将生成的 zip 文件通过 scp 或其他途径上传到产品服务器, 解包之后可以看到下面的文件结构:

```
drwxr-xr-x 4 luog luog 4.0K Apr 11 16:21 classes/
drwxr-xr-x 2 luog luog 4.0K Apr 11 16:21 lib/
-rw-rw-r-- 1 luog luog  20M Apr 11 16:21 helloworld-1.0-SNAPSHOT-b180411_1621.zip
-rwxrwxrwx 1 luog luog 2.2K Apr 11 16:21 run*
-rwxrwxrwx 1 luog luog  315 Apr 11 16:21 run.bat*
-rwxrwxrwx 1 luog luog   22 Apr 11 16:21 start*
-rwxrwxrwx 1 luog luog  316 Apr 11 16:21 start.bat*
```

执行 `./run` 启动应用, 默认配置环境为 `prod`. 如需在其他配置环境下运行应用使用 `-p` 参数, 例如:

```
./run -p uat
```

上面的命令会在 `uat` 配置环境下启动应用.

**小贴士** 执行 `./run --help` 能看到下面的帮助信息

```
./run start the app

     -d --debug           enable remote debugging
     --debug-port <port>  specify debug port (if not specified then debug port is 5005)
     -p --profile         specify the profile to start the app
     -g --group           specify the node group
     -Dprop=val           specify any JVM system properties
     -h --help            display this help message
```

`start` 和 `run` 命令基本相同, 不同点在于 `start` 在后台启动应用.

如果是在 windows 环境下, 可以是用 `run.bat` 或者 `start.bat` 命令. 当然如果是线上服务器, 还是推荐使用 Linux 系统.

*** <a name="frontend-http-server"></a>6.1 配置前端 http 服务器

通常来讲, 线上服务应该配置前端 HTTP 服务器, 并反向代理到 ActFramework 应用. 这样做的优势在与:

1. 可以帮助处理 https 请求
2. 可以在一个 80 端口通过域名分派到多个 ActFramework 应用上

用 nginx 为例来看看如何设置到 ActFramework 应用的反向代理. 假设应用 A 的 http 端口为 11000, 其对应的 nginx 配置文件内容大概为:

```
# 设置 443 到应用的反向代理
server {
  listen          443;
  client_max_body_size 8m;
  server_name     www.myawesomeproduct.com myawesomeproduct.com;

  ssl on;
  ssl_certificate     /home/ubuntu/cert/www_myawesomeproduct_com.crt;
  ssl_certificate_key /home/ubuntu/cert/www_myawesomeproduct_com.key;

  location / {
    proxy_pass        http://localhost:11000;
    proxy_set_header  X-Real-IP $remote_addr;
    proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header  Host $http_host;
  }
}

# 设置 80 到 443 的自动跳转
server {
        listen 80;
        server_name www.myawesomeproduct.com myawesomeproduct;
        return 301 https://$server_name$request_uri;
}
```

[返回目录](index.md)
