# Dependency Injection

ActFramework support dependency injection based on [JSR330](https://jcp.org/en/jsr/detail?id=330)  

## Declare inject object

In ActFramework the `javax.inject.Inject` annotation is used to declare the inject object. 
You can declare the inject object follow the three standard ways

**Field injection**
```java
public class Foo {
    @Inject
    private Bar bar;
}
```

**Constructor injection**
```java
public class Foo {
    private Bar bar
    
    @Inject
    public Foo(Bar bar) {
        this.bar = bar;
    }
}
```

**Setter injection**
```java
public class Foo {
    private Bar bar
    @Inject
    public void setBar(Bar bar) {
        this.bar = bar;
    }
}
```

**Tips** The Field injection is clean and simple, but not unit test friendly.

## Create object instance in actframework

```java
App app = App.instance();
// this ensure Bar has been injected into Foo
Foo foo = app.newInstance(Foo.class);
```

If you do something like:

```java
Foo foo = new Foo();
```

You don't have the `Bar` injected into your `foo` instance

## Inject action method parameter

Actframework support parameter value injection in the following three cases

1. [Controller action handler](controller.md)
2. [Command handler](cli.md)
3. [Job method](job.md)

If framework detect a certain parameter type has provider registered, then it will 
inject the parameter value using the provider automatically


```java
// suppose XyzDao has bound provider, then framework will use the provider to 
// value for `dao` parameter
public Result handleXyzRequest(String s, int i, ActionContext context, XyzDao dao) {
    ...
}
```

## Declare binding rule with module class

If you have used `Guice` before, like your usual Guice application, you can create Module 
classes to define binding logic, e.g.

```java
public class GreetingModule extends org.osgl.inject.Module {
    @Override
    protected void configure() {
        bind(GreetingService.class).to(GreetingServiceImpl.class);
    }
}
```

**Tips** Unlike guice, you don't need to create your own `Injector` via calling 
`Guice.createInjector(...)`. 
Declaring your module classes, and Act will locate them to create the injector for you. 

[Back to index](index.md)
