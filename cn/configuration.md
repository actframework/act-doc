# <a name="chapter_configuration">第二章 配置

**注意** 本章讲述 ActFramework 的配置处理以及使用方式，关于具体配置项的说明，请参见 [配置模板](https://github.com/actframework/archetype-support/blob/act-archetype-support-1.8.7.0/src/main/resources/archetype-resources/src/main/resources/conf/app.properties).

ActFramework 为应用程序开发人员提供了丰富的配置管理支持

<toc/>

## <a name="define"></a>1. 定义配置

ActFramework 读取 `resources/` 或 `resources/conf/` 下面的任何 `.properties` 文件来获得配置数据。这里是一个应用配置的例子：

```
resources
  ├── conf
  │   ├── app.properties         # 一般配置
  │   ├── db.properties          # 数据源配置
  │   ├── social.properties      # 社交网 （Oauth2) 配置
  │   ├── mail.properties        # SMTP 帐号配置
  │   └── cron.properties        # 作业调度配置
  ...
```

注意，配置文件的名字和个数没有限制，可以把所有的配置放到一个文件里面，虽然这样做会导致可读性降低

在配置文件中使用标准的 Java properties 方式来定义配置数据, 例如：

```
jwt=true

session.ttl=60*30

cors=true
cors.origin=*
cors.headers=Content-Type, X-HTTP-Method-Override, X-Act-Session, X-Requested-With, Location
cors.option.check=false

cron.withdraw-job.db-load=0 30 13 * * ?
```

### <a name="key_name"></a>1.1 配置项名字中的秘密

#### <a name="key_name-type_indicator"></a>1.1.1  值类型指示器

ActFramework 通过后缀来辨识配置项的值类型：

* `.bool`, `.boolean`, `.enabled` 或者 `.disabled` 表示 `boolean` 类型配置项. 例如: `secure.enabled`
* `.impl` 表示配置项为某种实现，可以为实例，也可以为类型名称. 例如: `cache.impl`
* `.dir`, `.home`, `.path` 表示配置项为某种路径配置. 例如 `ping.path` - URL 路径; `template.home` - 文件系统/资源路径
* `.int`, `.ttl`, `.len`, `.count`, `.times`, `.size`, `.port` 表示整型配置. 例如: `cli.session.ttl`, `cli.session.max.int`

这些类型指示器当中有些是纯粹的修饰, 包括: 
* `.enabled` 以及其他所有 `boolean` 类型的指示器, 
* `.int`
* `.impl`

对于纯粹的修饰类型指示器, 开发人员在配置的时候可以忽略, 比如下面两套配置的作用是完全一样的:

```
jwt=true
session.secure=false
cache=com.mycache.MyCacheServiceProvider
req.throttle=5
```

```
jwt.enabled=true
session.secure.enabled=false
cache.impl=com.mycache.MyCacheServiceProvider
req.throttle.int=5
```

**注意** 修饰类型指示器仅对 ActFramework 内置配置有效, 不能在应用自己的配置中使用.

另一些则是实际配置项名字的一部分, 只是起到了拥有类型指示器的作用, 包括:
* `.dir`
* `.home`
* `.path`
* `.ttl`
* `.port`
* `.len`
* `.count`
* `.size`
* `.times`

对于这种指示器则不可忽略, 例如下面的配置就是无效的:

```
ping=/ping
template=/templates
job.pool=10
```

正确的配置为:

```
ping.path=/ping
template.home=/templates
job.pool.size=10
```

#### <a name="key_name-enabled_disabled"></a>1.1.2 启用与禁用

对于 `.enabled` 型的类型指示器, ActFramework 
可以灵活处理 `.enabled` 与 `.disabled` 之间的互换, 下面的配置方式效果都是一样的:

方式一:

```
act.api_doc.enabled=true
```

方式二:

```
act.api_doc.disabled=false
```

方式三:

```
act.api_doc=true
```

### <a name="profile"></a>1.2 基于环境(profile)的配置

通常来讲一个正式的项目都有多个环境的配置, 
比如数据库的 URL 
对与本地环境和产品环境的配置不太可能是一样的. 
ActFramework 提供了基于环境的配置支持. 下面假设项目会在三种不同的环境当中部署运行:

* sit - 系统集成测试环境
* uat - 用户测试环境
* prod - 产品环境

针对以上三种环境, 项目的配置目录结构为:

```
resources
  ├── conf
  │   ├── prod
  │   │   ├── app.properties   - prod 应用特定配置
  │   │   └── db.properties    - prod 数据库特定配置
  │   ├── sit
  │   │   ├── app.properties   - sit 应用特定配置
  │   │   └── db.properties    - sit 数据库特定配置
  │   ├── uat
  │   │   ├── app.properties   - uat 应用特定配置
  │   │   └── db.properties    - uat 数据库特定配置
  │   ├── app.properties       - 公共应用配置文件
  │   ├── db.properties        - 公共数据库配置文件
  │   └── cron.properties      - 公共作业调度配置文件
  ...
```

对于上面的配置结构, ActFramework 
首先加载公共配置文件, 
然后依照当前的环境设定加载环境特定配置文件. 
如果环境配置文件中有配置项和公共配置文件中的配置项冲突, 则使用环境配置文件的配置设定来覆盖公共配置文件中的设定.

#### <a name="specify_profile"></a>1.2.1 应用运行环境设定

上面我们讲到 ActFramework 
根据当前运行环境加载配置文件, 带出来一个问题, 如何设定运行环境. 答案是在启动应用的时候通过 JVM 参数设定应用的运行环境:

```
java ... -Dprofile=uat ...
```

其中 `-Dprofile=uat` 设定应用的运行环境为 `uat`.

如果采用 ActFramework 推荐的项目结构, 应用部署的时候会生成 `run` 脚本, 可以通过下面的方式来设定运行环境:

```
./run -p uat
```

或者

```
./run --profile uat
```

## <a name="consume"></a>2. 使用配置

在 ActFramework 应用中可以通过多种方式来使用配置. 假设我们定义了如下配置:

```
myconf.foo.bar=100
```

下面我们会介绍如何在应用中使用 `myconf.foo.bar` 的配置:

### <a name="consume_pull"></a>2.1 从 `AppConfig` 实例获取配置

ActFramework 使用 `AppConfig` 实例来管理所有的配置项, 
`AppConfig` 实例可以通过 `Act.appConfig()` 来获得:

```java
@UrlContext("/conf")
public class ConfTest1 {
    @GetAction("pull")
    public int pull() {
		AppConfig conf = Act.appConfig();
		return $.convert(conf.get("myconf.foo.bar")).toInt();
    }
}
```

**小贴士** 上面的例子中我们用到了 OSGL 
工具库的类型转换将 `conf.get("myconf.foo.bar")` 
的值从字串转换为整型. 更多关于 OSGL 类型转换的信息可以参考 TBD 

### <a name="inject-configuration-value"></a>2.2 注入配置 - 方式一

通过 ActFramework 的依赖注入框架, 应用程序可以直接将配置注入到字段中:

```java
@UrlContext("/conf")
public class ConfTest2 {

    @Configuration("myconf.foo.bar")
    private int fooBar;

    @GetAction("inject")
    public int inject() {
        return this.fooBar;
    }

}
```

对于请求响应方法, 也可以直接注入到方法参数列表:

```java
@UrlContext("/conf")
public class ConfTest3 {

    @GetAction("inject_param")
    public int injectParam(@Configuration("myconf.foo.bar") int fooBar) {
        return fooBar;
    }

}
```

### <a name="inject-into-static-fields-with-autoconf"></a>2.3 注入配置 - 方式二

除了通过标准依赖注入框架对字段和方法参数注入, ActFramework 还支持使用 `@AutoConfig` 注解将配置注入到静态字段中:

```java
@UrlContext("/conf")
@AutoConfig("myconf") // 注意: 这里 `myconf` 指定配置的前缀, 如果没有则默认为 `app`
public class ConfTest4 {

    private static final Const<Integer> FOO_BAR = $.constant();

    @GetAction("auto_conf")
    public int autoConf() {
        return FOO_BAR.get();
    }

}
```

关于 AutoConfig 的注入方式, 有几点值得注意:

1. 待静态字段的类必须有 `@AutoConfig` 注解. `@AutoConfig` 
注解可以指定配置前缀, 在上面的例子中, 配置前缀为 
`myconf`, 如果不指定, 则配置前缀默认为 `app`. ActFramework 
使用配置前缀在配置中寻找和静态字段名对应的配置项, 在我们的例子中, FOO_BAR 对应了 myconf.foo.bar.

2. 待注入配置的字段必须是静态的, 无需 public, 也无需加载任何注解.

3. 待注入字段的名字通过以下规则匹配配置项:
	* 所有的下划线被替换为 `.`: FOO_BAR -> FOO.BAR
	* 所有的大写转换为小写: FOO.BAR -> foo.bar
	* 用 `.` 和前缀相连: foo.bar -> myconf.foo.bar
	
4. 如果静态字段需要标注为 final, 则应该使用 
`org.osgl.util.Const` 来包裹注入配置类型, 这样 ActFramework 
(通过反射) 将配置值注入到 Const 常量中, 
而应用则通过 `Const.get()` 来获得配置值, 
如同我们上面的例子. 这样的方式可以在保持 final 语义的前提下最大程度地简化开发

### <a name="consume_complex_type"></a>2.4 注入复杂类型

ActFramework 支持复杂类型的注入,包括 Map, List 和服务实现

#### <a name="consume_map"></a>2.4.1 注入 Map

假设有下面的配置:

```
myconf.map.demo.one=1
myconf.map.demo.two=2
```

应用可以这样来注入一个 Map 类型的配置变量:

```java
@GetAction("map")
@ResponseContentType(H.MediaType.JSON)
public Object barMap(@Configuration("myconf.map.demo") Map<String, Integer> barMap) {
    return barMap;
}
```

**注意** 本文中所有方法参数的配置注入均可以用于字段, 以下不再赘述

发送请求到 `/conf/map` 可以得到下面的响应:

```json
{
  "one": 1,
  "two": 2,
}
```

如果按照下面的方式来注入:

```java
@GetAction("map2")
@ResponseContentType(H.MediaType.JSON)
public Object barMap2(@Configuration("myconf.map") Map<String, Integer> fooMap) {
    return fooMap;
}
```

发送请求到 `/conf/map2` 得到的响应变为:

```json
{
  "demo.one": 1,
  "demo.two": 2,
}
```

#### <a name="consume_list"></a>2.4.2 注入 List

假设有下面的配置:

```
myconf.list.demo=1,2,3
```

应用可以注入一个整型数组:

```java
@GetAction("list")
@ResponseContentType(H.MediaType.JSON)
public int[] listDemo(@Configuration("myconf.list.demo") int[] list) {
    return list;
}
```

或者一个整形 List

```java
@GetAction("list2")
@ResponseContentType(H.MediaType.JSON)
public List<Integer> listDemo2(@Configuration("myconf.list.demo") List<Integer> list) {
    return list;
}
```

发送请求到 `/conf/list` 和 `/conf/list2` 获得相同的响应:

```json
[
    1, 
    2, 
    3
]
```

如果注入字串数组或者列表, 响应会改变:

```java
@GetAction("list3")
@ResponseContentType(H.MediaType.JSON)
public List<String> list3(@Configuration("myconf.list.demo") List<String> list) {
    return list;
}
```

响应:

```json
[
    "1", 
    "2", 
    "3"
]
```

#### <a name="consume_impl"></a>2.4.3 注入接口实现

假设我们定义了一下接口:

```java
public interface GreetingService {
    String greet();

    default String getName() {
        return greet() + " service";
    }
}
```

以及若干接口的实现类:

```java
public class HelloService implements GreetingService {
    @Override
    public String greet() {
        return "Hello";
    }
}
```

和

```java
public class NiHaoService implements GreetingService {
    @Override
    public String greet() {
        return "NiHao";
    }
}
```

应用可以为不同的场景配置不同的接口实现:

```
greet.default=demo.HelloService
greet.west=demo.HelloService
greet.east=demo.NiHaoService
```

下面演示如何在应用代码中使用接口实现配置:

```java
@UrlContext("/conf")
public class ConfTest {

    @Configuration("greet.default")
    private GreetingService defaultService;
    
    @Configuration("greet.west")
    private GreetingService westService;

    @Configuration("greet.east")
    private GreetingService eastService;

    @GetAction("greet")
    public String greetDefault() {
        return defaultService.greet();
    }

    @GetAction("greet/west")
    public String greetWest() {
        return westService.greet();
    }

    @GetAction("greet/east")
    public String greetEast() {
        return eastService.greet();
    }
}
```

发送请求到 `/conf/greet` 和 `/conf/greet/west` 都获得 `Hello` 
的响应. 而发送请求到 `/conf/greet/east` 则获得 `NiHao` 的响应.

应用也可以注入接口实现到一个 Map 中:


```java
@UrlContext("/conf")
public class ConfTest {

    @Configuration("greet")
    private Map<String, GreetingService> greetingServiceMap;

    @GetAction("greet/all")
    public Object allGreetings() {
        return greetingServiceMap;
    }
}
```

发送请求到 `/conf/greet/all` 获得下面的响应:

```JSON
{
  "default": {
    "name": "Hello service"
  },
  "scenario2": {
    "name": "NiHao service"
  },
  "scenario1": {
    "name": "Hello service"
  }
}
```

注入接口实现到一个 List 的情况:

配置:

```
greets=act.HelloService,demo.NiHaoService
```

Java 代码:

```java
@UrlContext("/conf")
@ResponseContentType(H.MediaType.JSON)
public class ConfTest {
    
    @Configuration("greets")
    private List<GreetingService> greetingServices;

    @GetAction("greet/list")
    public Object greetingList() {
        return greetingServices;
    }

}
```

发送请求到 `/conf/greet/list` 会得到一下响应:

```JSON
[
  {
    "name": "Hello service"
  },
  {
    "name": "NiHao service"
  }
]
```

## <a name="third_party_conf"></a>3. 加载三方配置文件

如果应用引入的第三方库需要特殊的配置, 
往往需要提供配置文件或者 InputStream 给三方库, 
这些配置有时候并不是 `.properties` 
文件,而是采用其他格式, 比如 `.json`, `.yaml` 等等, 
对于这种情况, ActFramework 为应用提供了 `@act.inject.util.LoadConfig` 注解:

假设应用需要一个 `libx.json` 的配置文件, 下面是获得这个配置文件的办法:

```java
public class ConfTest {
	@LoadConfig("libx.json")
	private String libxContent;
	
    @LoadConfig("libx.json")
    private File libxFile;

    @LoadConfig("libx.json")
    private URL libxUrl;

    @LoadConfig("libx.json")
    private InputStream libxInputStream;
}
```

使用 `@LoadConfig` 和应用自行采用 `Class.getResource` 来加载的区别在于:

1. `@LoadConfig` 更加简单, 而且可以加载配置文件到不同类型的字段,如上例所示.
2. `@LoadConfig` 在不同的运行环境(profile)下可以加载环境目录下的配置文件

## <a name="pom_conf"></a>4. pom 文件的配置

TBD

## 总结

本篇详细讲述了如何在 ActFramework 应用中使用 ActFramework 提供的配置管理工具, 包括

* 配置值类型指示器
* 基于环境的配置
* 使用 `AppConfig` 来获取配置
* 注入配置到字段和请求处理方法参数
* 注入配置到静态字段
* 处理配置中的复杂类型
* 加载三方配置文件
* pom 文件配置

本文中的代码可以从下面的代码库中获得:

https://github.com/greenlaw110/act-doc-configuration

