# Dependency Injection in Actframework  II - Inject object type

This recipe talks about Inject object type in ActFramework

## 1. Built-in bindings

There are many services and components in Actframework can be injected directly, including:

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

### 1.1 App services

* `DbServiceManager`
* `MailerService`
* `Router`
* `CliDispatcher`
* `AppJobManager`

## 2. Dao

At the moment ActFramework support `EbeanDao` and `MorphiaDao` for SQL database MongoDB access respectively

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

If application has defined its own Dao, it can be injected also

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



## 3. Class that can be constructed (without external parameter)

Any class with public default constructor or constructor with `@Inject` annotation can be injected

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

The `Foo` and `Bar` depicted above can be injected

```java
public class XxxController {
    @Inject Foo foo;
    @Inject Bar bar;

    ...
}
```

**Note** The constructable class cannot be injected into method parameter list directly

```java
public class XxxController {

    // foo and bar won't be able to injected throght DI
    // instead they will be deserialized from form parameters
    @PostAction("/xxx")
    public void xxxAction(Foo foo, Bar bar) {
    }
}
```

However it can request framework to inject constructable object throug `@Provided` annotation

```java
public class YyyController {

    // this time foo and bar will be injected through DI
    @PostAction("/yyy")
    public void xxxAction(@Provided Foo foo, @Provided Bar bar) {
    }
}
```

## 4. Application defined binding

If application defined an interface or abstract class and the provider binding, the interface/class can be injected:

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

## Links

* [ActFramework document](/doc/index.md)
