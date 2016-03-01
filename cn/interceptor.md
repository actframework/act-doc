# 拦截器

ActFramework应用程序可以使用两种方式创建拦截器:

1. 基于注解的方式 (类似PlayFramework)
1. 通过继承写拦截器类

## 基于注解的拦截器

该设计完全基于PlayFramework 1.x的方式

### Before

在某方法上使用`@Before`注解告诉ActFramework在执行该类中的所有请求响应器之前先调用该方法

下面我们使用拦截器来创建一个安全检查：

```java
public class Admin extends Controller.Util {
 
    @Before
    public void checkAuthentification() {
        if(session.get("user") == null) {
            redirect("/login");
        }
    }
 
    public void index() {
        Iterable<User> users = userDao.findAll();
        render(users);
    }
    
    …
}
```

如果不需要拦截器拦截发送给某些响应器的请求，可以使用`except`参数来指定豁免清单:

```java
public class Admin extends Controller.Util {
 
    @Before(except="login")
    static void checkAuthentification() {
        if(session.get("user") == null) {
            redirect("/login");
        }
    }
 
    public void index() {
        Iterable<User> users = userDao.findAll();
        render(users);
    }
 
    …
}
```

另一方面，你也可以使用`only`参数来指定该拦截器唯一作用于的响应器:

```java
public class Admin extends Controller.Util {
 
    @Before(only={"login","logout"})
    public void doSomething() {  
        …  
    }
    …
}
```

除了`@Before`, `except`和`only`参数也可以在`@After`, `@Catch`和`@Finally`注解中使用.

### @After

标注有`@After`的方法在执行本类中所有的响应器之后被调用.

```java
public class Admin extends Controller.Util {
 
    @After
    public void log() {
        Logger.info("Action executed ...");
    }
 
    public void index() {
        Iterable<User> users = userDao.findAll();
        render(users);
    }
 
    …
}
```

### @Catch

如果某方法标注有`@Catch`，该方法在当前类的响应器抛出异常后被调用. 被抛出的异常作为参数传递给`@Catch`方法.

```java
public class Admin extends Controller.Util {
	
    @Catch(IllegalStateException.class)
    public void logIllegalState(Throwable throwable) {
        Logger.error("Illegal state %s…", throwable);
    }
    
    public void index() {
        List<User> users = userDao.findAllAsList();
        if (users.size() == 0) {
            throw new IllegalStateException("Invalid database - 0 users");
        }
        render(users);
    }
}
```

和通常的Java异常处理类似，你可以在`@Catch`拦截器方法中申明父类异常来捕获子类异常

```java
public class Admin extends Controller.Util {
 
    @Catch(value = Throwable.class, priority = 1)
    public void logThrowable(Throwable throwable) {
        // Custom error logging…
        Logger.error("EXCEPTION %s", throwable);
    }
 
    @Catch(value = IllegalStateException.class, priority = 2)
    public void logIllegalState(Throwable throwable) {
        Logger.error("Illegal state %s…", throwable);
    }
 
    public void index() {
        List<User> users = userDao.findAllAsList();
        if(users.size() == 0) {
            throw new IllegalStateException("Invalid database - 0 users");
        }
        render(users);
    }
}
```

### @Finally

标注有`@Finally`注解的方法在当前控制器的响应方法执行完成之后被调用，即便响应方法抛出异常，该拦截器也会被调用

```java
public class Admin extends Controller.Util {
 
    @Finally
    static void log() {
        Logger.info("Response contains : " + response.out);
    }
 
    public static void index() {
        List<User> users = userDao.findAllAsList();
        render(users);
    }
    …
}
```

### 类继承对拦截器的影响

如果控制器继承了某个基类，在基类中定义的拦截器也适用于子类控制器

### 使用@With注解来重用拦截器定义

如果你的控制器已经继承了某个基类，而你需要重用定义在另一个类的拦截器，可以通过`@With`注解来实现:


某个定义了拦截器的类:

```java
public class Secure extends Controller.Util {
    
    @Before
    static void checkAuthenticated() {
        if(!session.containsKey("user")) {
            unAuthorized();
        }
    }
}    
```

控制器类:

```java
@With(Secure.class)
public class Admin extends MyOtherBaseClass {
    
    …
}
```

## 实现拦截器接口

基于注解的拦截器非常轻量，不过只适用于定义或这引用了拦截器的控制器。如果需要实现全局拦截，可以通过继承XxxInterceptor来实现

```java
import act.app.ActionContext;
import act.handler.builtin.controller.BeforeInterceptor;
import act.plugin.Plugin;
import org.osgl.http.H;
import org.osgl.mvc.result.Result;

import javax.inject.Singleton;

@Singleton
public class MockRequestContentAcceptor extends BeforeInterceptor {

    public MockRequestContentAcceptor() {
        super(1);
        Plugin.InfoRepo.register(this);
    }

    @Override
    public Result handle(ActionContext actionContext) throws Exception {
        String s = actionContext.paramVal("fmt");
        if ("json".equalsIgnoreCase(s)) {
            actionContext.accept(H.Format.JSON);
        } else if ("csv".equalsIgnoreCase(s)) {
            actionContext.accept(H.Format.CSV);
        } else if ("xml".equalsIgnoreCase(s)) {
            actionContext.accept(H.Format.XML);
        }
        return null;
    }
}

```

上面的代码拦截所有的请求，检查是否有`fmt`参数，如果发现`fmt`参数则设置相应的`Accept`头。

类似的拦截器接口还有：

1. `act.handler.builtin.controller.AfterInterceptor`
1. `act.handler.builtin.controller.ExceptionInterceptor`
1. `act.handler.builtin.controller.FinallyInterceptor`

[返回目录](index.md)