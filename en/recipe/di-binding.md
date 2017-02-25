# ActFramework Dependency Injection III - Define Bindings

We have mentioned one binding defining mechanism in the [ActFramework Dependency Injection II - Inject object type](di-inject-type.md)

## 1. Through Module class

```java
// Define bindings
public class MyModule extends org.osgl.inject.Module {
    protected void configure() {
        bind(MyService.class).to(OneService.class);
        bind(MyService.class).named("two").to(TwoService.class);
    }
}
```

This article will introduce other binding mechanism in ActFramework.

## 2. Define Factory class

Factory is an alternative of Module class. We can rewrite the above Module class using Factory in the following way:

```java
public class MyFactory {

    @Provides
    public MyService getOneService(OneService oneService) {
        return oneService;
    }

    @Provides
    @Named("two")
    public MyService getTwoService(TwoService twoService) {
        return twoService;
    }

}
```

## 3. Auto binding

The thrid way is "Auto binding" which does not require explicitly defining Module or Factory. 
However it must add `@AutoBind` annotation to the interface or class that needs to be bound to 
other implementations

```java
// The interface
@AutoBind
public interface MyService {
    void service();
}
```

Define default implementation:

```java
// The implemention one
public class OneService implements MyService {
    public void service() {Act.LOGGER.info("ONE is servicing");}
}
```

Use `@Named` annotation to define Qualified implementation

```java
// The implemention two
@Named("two")
public class TwoService implements MyService {
    public void service() {Act.LOGGER.info("TWO is servicing");}
}
```

Use the dependency injection

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

## Links

* [ActFramework Document home](../index)
