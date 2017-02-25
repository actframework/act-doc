# ActFramework 依赖注入 III - 定义绑定

在[ActFramework 依赖注入 II - 注入对象类型](di-inject-type.md)中我们提到了定义绑定的一种方式: 

## 1. 使用Module

```java
// Define bindings
public class MyModule extends org.osgl.inject.Module {
    protected void configure() {
        bind(MyService.class).to(OneService.class);
        bind(MyService.class).named("two").to(TwoService.class);
    }
}
```

这篇文章继续介绍ActFramework的其他绑定方式

## 2. 自定义工厂

工厂和上面的Module是相当的, 把上面的Module用工厂的方式来写会是这样:

```java
public class MyFactory {

    @Provided
    public MyService getOneService(OneService oneService) {
        return oneService;
    }

    @Provided
    @Named("two")
    public MyService getTwoService(TwoService twoService) {
        return twoService;
    }

}
```

## 3. 自动绑定

自动绑定不需要定义Module和工厂,但是需要在Interface(被绑定类)上使用`@AutoBind`注解:

```java
// The interface
@AutoBind
public interface MyService {
    void service();
}
```

定义缺省实现

```java
// The implemention one
public class OneService implements MyService {
    public void service() {Act.LOGGER.info("ONE is servicing");}
}
```

使用`@Named`注解定义Qualified的实现

```java
// The implemention two
@Named("two")
public class TwoService implements MyService {
    public void service() {Act.LOGGER.info("TWO is servicing");}
}
```

使用依赖注入

```java
// Inject the service
public class Serviced {
    
    // this one will get bind to the default implementation: OneService
    @Inject
    private MyService one;

    // this one will get bind to TwoService
    @Inject
    @Named("two")
    private MyService two;
}
```

## 链接

* [ActFramework文档主页](../index)
