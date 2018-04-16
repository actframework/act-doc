# 启动 ActFramework

本文讲述应用如何通过调用 `Act.start()` 接口启动 ActFramework 并设置应用的名字, 版本以及扫描包

* [1. 启动过程](#bootstrap)
* [2. AppDescriptor](#app_desc)
	* [2.1 .version 文件](#dot_version)
	* [2.2 名字](#setup_appname)
		* [2.2.1 短名字](#short_id)
	* [2.3 版本](#version)
	* [2.4 扫描包](#scan_package)

## <a name="bootstrap"></a>1. 启动过程

`Act.start()` 是 ActFramework 应用启动的入口函数, 调用该方法之后会触发一系列操作:

1. 通过调用栈获得调用类名字, 比如 `com.mycom.myproj.AppEntry`
2. 获得该类的 `AppDescriptor`
3. 从 `AppDescriptor` 获得包名并将其设定在 `scan_package` 系统属性 (System.properties) 上面备用
4. 通过以下规则推断应用启动模式:
	- 4.1 如果发现 `app.mode` 系统设定, 则使用该设定
	- 4.2 如果发现应用从 jar 文件启动, 则直接设定 `app.mode` 为 `prod`
	- 4.3 如果发现应用从 classes 目录下启动则依据 `profile` 设定推断:
		* 4.3.1 如果发现 `profile` 系统设定为 `prod`, 则设定模式为 `prod`
		* 4.3.2 如果发现 `profile` 系统设定为 `dev`, 则设定模式为 `dev`
		* 4.3.3 所以其他情况均推断 `app.mode` 为 `dev`
5. 在控制台上打印 Banner
6. 初始化各种基础服务,包括
	* 性能统计
	* 插件管理器
	* 数据源管理器
	* 字节码增强器管理器
	* 视图 (View) 管理器
	* 自动加载各种插件 (Plugin)
	* 网络层
	* 应用程序管理器
7. 启动应用程序
	- 7.1 扫描应用程序文件结构并获得 `App` 实例
	- 7.2 构建应用, 包括准备 target 目录并拷贝资源文件
		- 仅对 dev 模式启用
	- 7.3 调用 `App.refresh`, 这里是应用的自举过程,包括一下步骤:
		* 清除上次应用启动状态 - 仅对 dev 模式有效
		* 初始化 SingletonManager
		* 初始化 EventBus
		* 加载 AppConfig
		* 加载 JobManager
		* 初始化 Router
		* 加载系统内置处理器路由
		* 初始化 CLI 服务
		* 初始化 WebSocket 连接管理器
		* 初始化 DbService 管理器
			- 这里会启动各个数据源的初始化线程
		* 初始化并加载字节码扫描器管理器
		* 初始化 AppClassLoader
		* 初始化 Cache
		* 扫描字节码
		* Hook 到 Act 的视图管理器
		* 加载依赖注入容器
		* 初始化 SessionManager
		* 当所有的数据源启动完成之后发出应用启动过程完成的事件
8. 获得应用的 http 端口并启动网络层
9. 获得进程 pid 并写入 pid 文件
	
## <a name="app_desc"></a>2. AppDescrptior

在上面的启动过程中我们提到了一个概念 `AppDescriptor`, ActFramework 使用 `AppDescriptor` 描述应用的以下属性:

1. 名字
2. 版本
3. 扫描包

### <a name="dot_version"></a>2.1 .version 文件

在继续讲述这三个方面之前需要提到一个特殊文件: `.version`. 如果使用 ActFramework 提供的 maven archetype, 会在项目的 `src/main/resources/<pkg_path>/` 下面生成一个名为 `.version` 的文件. 加入应用的 package 为 `com.mycom.myproj`, 该文件的路径为 `src/main/resources/com/mycom/myproj/.version`, 其内容为:

```
artifact=${project.artifactId}
version=${project.version}
build=${buildNumber}
```

其中三个变量会在 maven 构建的时候被替换为实际值:

* `project.artifactId` 替换为 pom.xml 文件中的 artifactId
* `project.version` 替换为 pom.xml 文件中定义的 version
* `buildNumber` 替换为 pom.xml 中 maven-buildnumber-plugin 生成的 buildnumnber
	1. 如果项目托管在 git 下面, 则使用 git commit 的 hash 作为 buildnumber, 否则
	2. 使用 build 时候的日期时间作为 buildnumber

这个 .version 文件帮助 ActFramework 获得默认的名字和应用的版本号. 关于该文件的详细描述, 参见 [osgl-version 项目](https://github.com/osglworks/java-version)

### <a name="setup_appname"></a>2.1 名字

ActFramework 为每个应用提供默认名字, 也可以由应用在调用 `Act.start()` 的时候指定名字:

```java
Act.start("My Awesome App");
```

如果没有指定名字, Act 通过下面的操作推断名字:

1. 检查是否有 .version 文件, 如果存在则使用其中的 artifact 定义作为应用名字. 否则
2. 通过启动类的包名和类名生成名字

#### <a name="short_id"></a>2.1.1 短名字 (short id)

当确认应用名字之后 ActFramework 会按照一下规则依据名字生成 shortId:

1. 如果名字为空或者 `MyApp`, 则使用 `act` 作为 shortId
2. 将名字以空格符分割, 并按照分割后数组长度不同采用不同算法生成 shortId:
3. 如果分割后只有一个字串 (即名字中没有空格), 则截取名字前三个字符作为 shortId
4. 如果分割后有两个字串, 则每个字串截取 2 个字符并以 `-` 连接
5. 如果超过两个, 则取前三个字串,每个字串截取 1 个字符并以 `-` 连接

### <a name="version"></a>2.2 版本

如果 .version 文件存在, 则使用其中定义的 version 和 buildnumber 作为版本. 详情参见 [osgl-version 项目](https://github.com/osglworks/java-version)

如果 .version 文件不存在, 则使用 `unknown` 作为版本号

### <a name="scan_package"></a>2.3 扫描包

可以在 `Act.start()` 方法中传入扫描包, 例如:

```java
Act.start("my awesome app", "com.myproj;com.myproj2;...");
```

上面的代码中传入 `Act.start` 方法的第二参数 `"com.myproj;com.myproj2;..."` 即为扫描包. 

如果没有指定扫描包, ActFramework 通过调用类的名字来获得扫描包. 假设类名为 `com.myproj.AppEntry` 扫描包为 `com.myproj`
