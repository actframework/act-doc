# FAQ

## 我启动示例应用看到"App not found"报错是怎么回事

我使用Intellij IDEA加载了示例项目然后我运行"HelloWorld"程序的时候得到下面的错误堆栈:

```
Exception in thread "main" org.osgl.exception.UnexpectedException: App not found. Please make sure your app start directory is correct
	at act.Act.start(Act.java:307)
	at act.Act.startApp(Act.java:269)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at act.boot.app.RunApp.start(RunApp.java:64)
	at act.Act.start(Act.java:610)
	at demo.helloworld.HelloWorldApp.main(HelloWorldApp.java:24)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:147)
```

### 解决办法

你需要在IDEA的Run配置里面正确地设置"工作目录(working directory)":

![image](https://cloud.githubusercontent.com/assets/216930/23855130/a2136556-0848-11e7-8184-2433004b123b.png)

## 我不能构建示例程序, 下载依赖的时候提示出错

我在控制台上发现类似下面的警告信息

```
[WARN] The POM for io.undertow:undertow-core:jar:1.4.11.Final is invalid, transitive dependencies (if any) will not be available
```

可能是你的网络的问题. 也许切换到阿里云的maven服务器可以解决这个问题. 另外你可能需要删除本地maven repository缓存的相关的库,再重新运行`mvn clean compile`一次, 就上面的错误警告来讲,你需要删除和undertow还有jboss xnio相关的本地缓存:

```
rm -rf ~/.m2/repository/io/undertow/ && rm -rf ~/.m2/repository/org/jboss/xnio
```
