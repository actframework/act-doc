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

### 解决办法

可能是你的网络的问题. 也许切换到阿里云的maven服务器可以解决这个问题. 另外你可能需要删除本地maven repository缓存的相关的库,再重新运行`mvn clean compile`一次, 就上面的错误警告来讲,你需要删除和undertow还有jboss xnio相关的本地缓存:

```
rm -rf ~/.m2/repository/io/undertow/ && rm -rf ~/.m2/repository/org/jboss/xnio
```

## 我在控制台上看到这种出错信息`java.security.InvalidKeyException: Illegal key size`, 是怎么回事?

```
13:01:06.851 [XNIO-1 task-17] ERROR a.a.u.AppCrypto - Cannot encrypt/decrypt! Please download Java Crypto Extension pack from Oracle: http://www.oracle.com/technetwork/java/javase/tech/index-jsp-136007.html
13:01:06.851 [XNIO-1 task-17] ERROR a.h.b.c.RequestHandlerProxy - Error handling request
org.osgl.exception.UnexpectedException: java.security.InvalidKeyException: Illegal key size
	at org.osgl.util.E.unexpected(E.java:100)
	at org.osgl.util.Crypto.encryptAES(Crypto.java:183)
	at act.app.util.AppCrypto.encrypt(AppCrypto.java:71)
	at act.app.App.encrypt(App.java:659)
	at act.util.SessionManager$CookieResolver.dissolveIntoCookieContent(SessionManager.java:321)
	at act.util.SessionManager$CookieResolver.dissolveSession(SessionManager.java:217)
	at act.util.SessionManager.dissolveSession(SessionManager.java:78)
	at act.app.ActionContext.dissolveSession(ActionContext.java:849)
	at act.app.ActionContext.dissolve(ActionContext.java:697)
	at act.handler.builtin.controller.RequestHandlerProxy.onResult(RequestHandlerProxy.java:241)
	at act.handler.builtin.controller.RequestHandlerProxy.handle(RequestHandlerProxy.java:177)
	at act.handler.DelegateRequestHandler.handle(DelegateRequestHandler.java:27)
	at act.route.Router$ContextualHandler.handle(Router.java:935)
	at act.xio.NetworkHandler$1.run(NetworkHandler.java:78)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)
Caused by: java.security.InvalidKeyException: Illegal key size
	at javax.crypto.Cipher.checkCryptoPerm(Cipher.java:1034)
	at javax.crypto.Cipher.implInit(Cipher.java:800)
	at javax.crypto.Cipher.chooseProvider(Cipher.java:859)
	at javax.crypto.Cipher.init(Cipher.java:1370)
	at javax.crypto.Cipher.init(Cipher.java:1301)
	at org.osgl.util.Crypto.encryptAES(Crypto.java:174)
	... 15 common frames omitted
```

### 解决办法

你需要按照 JCE (Java Cryptography Extension). 搜索 `jce java` 然后找到并点击前往 oracle 网站的链接. 根据说明下载安装该软件

