# ActFramework简介

## <a name="prerequisites"></a>准备工作

你需要JDK和Maven来使用ActFramework创建应用程序. 因为需要Maven通过网络从中央库下载依赖包，一个良好的互联网链接也是必须的

1. JDK (Java Development Kit), version 1.7或以上
1. Maven (Project Management Tool), version 3.5或以上

### 安装JDK

从[Java官网](http://www.oracle.com/technetwork/java/javase/downloads/index.html)下载JDK并安装

### 安装Maven

从[Maven官网](http://maven.apache.org/)下载Maven. 参照[教程](http://maven.apache.org/install.html)安装Maven到你的操作系统

## <a name="create_hello_world_app"></a>创建一个"Hello world"应用程序

ActFramework使用maven构建项目. 

### 1. 使用maven生成应用框架:

```
mvn archetype:generate -DarchetypeGroupId=org.actframework -DarchetypeArtifactId=archetype-quickstart -DarchetypeVersion=1.8.6.1 -B -DgroupId=com.mycom.helloworld -DartifactId=helloworld -DappName=helloworld
``` 

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

### 2. 启动应用

进入 `helloworld` 项目目录，键入 `mvn compile act:run` 启动应用，应该能看到类似下面的信息：

```
       _           _            _    _        _  
 |_|  |_  |   |   / \  \    /  / \  |_)  |   | \ 
 | |  |_  |_  |_  \_/   \/\/   \_/  | \  |_  |_/ 
                                                 
              powered by ActFramework r1.8.6-6acf

 version: v1.0-SNAPSHOT-180404_2248
scan pkg: com.mycom.helloworld
base dir: /tmp/helloworld
     pid: 2026
 profile: dev
    mode: DEV

     zen: Simple is better than complex.

2018-04-04 22:48:10,013 INFO  a.Act@[main] - loading application(s) ...
2018-04-04 22:48:10,019 INFO  a.a.App@[main] - App starting ....
2018-04-04 22:48:10,096 WARN  a.c.AppConfig@[main] - Application secret key not set! You are in the dangerous zone!!!
2018-04-04 22:48:10,189 WARN  a.h.b.ResourceGetter@[main] - URL base not exists: META-INF/resources/webjars
2018-04-04 22:48:10,200 WARN  a.a.DbServiceManager@[main] - DB service not initialized: No DB plugin found
2018-04-04 22:48:10,968 INFO  a.a.App@[main] - App[helloworld] loaded in 949ms
2018-04-04 22:48:10,971 INFO  a.a.ApiManager@[jobs-thread-3] - start compiling API book
2018-04-04 22:48:10,989 INFO  o.xnio@[main] - XNIO version 3.3.8.Final
2018-04-04 22:48:11,012 INFO  o.x.nio@[main] - XNIO NIO Implementation Version 3.3.8.Final
2018-04-04 22:48:11,142 INFO  a.Act@[main] - network client hooked on port: 5460
2018-04-04 22:48:11,142 INFO  a.Act@[main] - CLI server started on port: 5461
2018-04-04 22:48:11,144 INFO  a.Act@[main] - app is ready at: http://192.168.1.5:5460
2018-04-04 22:48:11,144 INFO  a.Act@[main] - it takes 2614ms to start the app
```

启动浏览器并打开<a href="http://localhost:5460"><code>http://localhost:5460</code></a> 能看到默认的主页:

![image](https://user-images.githubusercontent.com/216930/38310001-cbc749a2-385e-11e8-8a87-f505ccee767c.png)


### 3. 导入项目到你的IDE

基本上所有的IDE都支持maven项目. 下面使用IntelliJ IDEA做演示.

选择`File/Open...` 并导航到生成的项目目录:

![image](https://user-images.githubusercontent.com/216930/38247063-4b1967d6-3787-11e8-9257-d5971ea86acf.png)

点击 Okay 之后打开项目:

![image](https://user-images.githubusercontent.com/216930/38247205-b2e735b4-3787-11e8-8cd3-a47f30713a1f.png)


**小贴士** 使用ActFramework开发不需要重启应用，改完代码后直接刷F5即可看到效果

### 4. 理解 AppEntry 类

打开 AppEntry.java 文件我们看到下面两个方法：

```java
    @GetAction
    public void home(@DefaultValue("World") @Output String who) {
    }

    public static void main(String[] args) throws Exception {
        Act.start();
    }
```

#### 4.1 启动应用

`main` 方法中调用 `Act.start()` 启动整个应用, 因此我们可以在 `pom.xml` 文件中将 AppEntry 类定义为 `<app.entry>`:

```xml
<app.entry>com.mycom.helloworld.AppEntry</app.entry>
```

#### 4.2 主页响应方法

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

下面是 home 方法更加冗长的表达方式:

```java
@GetAction("/")
public Result home(ActionContext context) {
    String who = context.req().paramVal("who");
   if (null == who) who = "World";
   context.renderArg("who", who);
   return RenderTemplate.get();
}
```

这里可以看出 ActFramework 支持不同的表达方式, 我们推荐使用更加简洁的方式让编码和阅读都更简单.

##### 4.3.1 模板的路径

`homne` 方法中我们并没有看到指定模板文件路径的地方, ActFramework 在程序没有指定模板路径的时候按照下面的规则来寻找模板文件:

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

下面是模板文件内容:

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

### 5. 加入更多的请求响应器

没有哪个Web应用只能响应一个请求. 现在加入另一个响应器到`AppEntry.java`文件中，该响应器处理发送到`/bye`的请求.

```java
    @GetAction("/bye")
    public String sayBye() {
        return "Bye!";
    }
``` 

加完方法后, 切换到你的浏览器打开<a href="http://localhost:5460/bye"><code>http://localhost:5460/bye</code></a>, 你应该能看到如下效果:

![image](https://user-images.githubusercontent.com/216930/38310207-573d6908-385f-11e8-9e06-2a2d28be87cc.png)


## <a name="anatomy"></a>ActFramework应用项目剖析

ActFramework使用标准的maven项目布局来组织文件. 第一次编译后的项目看起来是这样的:

```
.
├── pom.xml
├── src
│   ├── main
│   │   ├── java                                -> Java 源码
│   │   │   └── com
│   │   │       └── mycom
│   │   │           └── myprj
│   │   │               ├── Application.java    -> 应用程序入口(提供main()方法)
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
│   │       │   ├── common                      -> 存放缺省配置
│   │       │   ├── sit                         -> "sit" 配置
│   │       │   └── dev                         -> "dev" 配置
│   │       ├── messages.properties             -> 国际化资源文件
│   │       ├── routes                          -> 路由表
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

1. 包组织方式完全由项目决定。你的项目中可能使用了`service`包而不是`controller`来存放所有的RESTful控制器类。 而你的业务层也许不是一个`model`包, 而是分布在多个不同的包里. ActFramework在项目文件组织上没有任何限制
1. ActFramework使用`common`配置目录来获取缺省的配置信息。而上例所示的`sit`和`dev`目录则完全由项目决定，你可以使用其他任何名字，你也可以增加另一种配置组，比如`uat`。如果项目不需要多个配置组，使用`common`即可。
1. 如果路由都通过注解方式指定，`routes`文件可以不用提供. 一旦`routes`文件被检测到，其中的条目可以覆盖注解指定路由


## 总结

本单元说明了以下内容

1. 准备工作
1. 通过maven生成项目文件，如何拷贝示例`pom.xml`文件覆盖生成的`pom.xml`文件
1. 在`main`方法调用`RunApp.start`方法来启动ActFramework应用程序
1. 添加请求响应方法
1. 使用模板来生成复杂响应
1. 处理请求参数
1. ActFramework程序结构解析

[返回目录](index.md)
