# Actframework依赖注入 II - 注入对象类型

本篇讲述Actframework依赖注入的对象类型

## 1. 框架内置绑定

在ActFramework中有大量的服务和组件都可以直接使用依赖注入，其中包括

* `ActionContext` - Encapsulate all data/info relevant to an HTTP request context
* `H.Session` - HTTP request session. Also available via `actionContext.session()`
* `H.Flash` - HTTP request flash. Also available via `actionContext.flash()`
* `H.Request` - HTTP request. Also available via `actionContext.req()`
* `H.Response` - HTTP response. Also available via `actionContext.resp()`
* `CliContext` - Encapsulate all data/facilities relevant to a CLI session
* `CliSession` (Since act-0.6.0) - CLI session
* `MailerContext` - Mailer method context
* `ActContext` - A generic `ActContext` depends on the current computation environment, could be either `ActionContext`, `CliContext` or `MailerContext` or `null`
* `Logger` - The `Act.LOGGER` instance
* `UserAgent` - The user agent if in a request handling context
* `AppConfig` - The application configuration object
* `AppCrypto` - The application crypto object
* `CacheService` - The `App.cache()` cache service
* `EventBus` - The application's event bus
* `Locale` - Could be `ActContext.locale()` or `AppConfig.locale()` if there is no context

### 1.1 应用服务组件

* `DbServiceManager`
* `MailerService`
* `Router`
* `CliDispatcher`
* `AppJobManager`

## 2. Dao

目前支持`EbeanDao`和`MorphiaDao`两种分别用于访问SQL和MongoDB数据库

```java
// Demonstrate inject to field
@Controller("user")
public class UserService {

    @javax.inject.Inject
    private MorphiaDao<User> userDao;

    @PostAction
    public void create(User user) {
        userDao.save(user);
    }

}
```

```java
// Demonstrate inject to parameter
@Controller("user")
public class UserService {

    @PostAction
    public void create(User user, MorphiaDao<User> userDao) {
        userDao.save(user);
    }

}
```

如果应用有自定义的Dao,可以直接注入:

```java
// The Model
@Entity("user")
public class User extends MorphiaModel<User> {
    
    public String email;
    ...

    public static class Dao extends MorphiaDao<User> {
        public User findByEmail(String email) {
            return findOneBy("email", email);
        }
    }
}
```

```java
// The controller
@Controller("user")
public class UserService {

    @javax.inject.Inject
    private User.Dao userDao;

    @GetAction("{email}")
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

}
```



## 3. 可构造对象

任何拥有public缺省构造函数或者带有`@Inject`构造函数的类均可被注入, 例如:

```java
// A class with public default constructor
public class Foo {
    public Foo() {...}
}
```

```java
// A class with Inject constructor
public class Bar {
    @javax.inject.Inject
    public Bar(Foo foo) {...}
}
```

上面的`Foo`和`Bar`都可以用于依赖注入:

```java
public class XxxController {
    @Inject Foo foo;
    @Inject Bar bar;

    ...
}
```

**注意** 可构造对象不能直接用于参数注入

```java
public class XxxController {

    // foo and bar won't be able to injected throght DI
    // instead they will be deserialized from form parameters
    @PostAction("/xxx")
    public void xxxAction(Foo foo, Bar bar) {
    }
}
```

但是可以通过`@Provided`注解来指定使用依赖注入

```java
public class YyyController {

    // this time foo and bar will be injected through DI
    @PostAction("/yyy")
    public void xxxAction(@Provided Foo foo, @Provided Bar bar) {
    }
}
```

## 4. 应用自定义的绑定

假设应用自己定义了接口或抽象类, 并且定义了绑定, 可以直接使用依赖注入

```java
// The interface
public interface MyService {
    void service();
}
```

```java
// The implemention one
public class OneService implements MyService {
    public void service() {Act.LOGGER.info("ONE is servicing");}
}
```

```java
// The implemention two
public class TwoService implements MyService {
    public void service() {Act.LOGGER.info("TWO is servicing");}
}
```

```java
// Define bindings
public class MyModule extends org.osgl.inject.Module {
    protected void configure() {
        bind(MyService.class).to(OneService.class);
        bind(MyService.class).named("two").to(TwoService.class);
    }
}
```

```java
// Inject the service
public class Serviced {
    @Inject
    private MyService one;

    @Inject
    @Named("two")
    private MyService two;
}
```

## 链接

* [ActFramework文档主页](/doc/index.md)
