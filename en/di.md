# Dependency Injection

ActFramework support dependency injection based on [JSR330](https://jcp.org/en/jsr/detail?id=330)  

## Declare inject object

In ActFramework the `javax.inject.Inject` annotation is used to declare the inject object. 
You can declare the inject object follow the three standard ways

### Field injection

```java
public class Foo {
    @Inject
    private Bar bar;
}
```

### Constructor injection

```java
public class Foo {
    private Bar bar
    
    @Inject
    public Foo(Bar bar) {
        this.bar = bar;
    }
}
```

### Setter injection

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

## Create object instance that has DI

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

## Inject into controller action method

You can ask Act to inject class instance you need in an controller action method by declaring the parameter with 
`org.osgl.inject.annotation.Provided` annotation:

```java
public Result handleXyzRequest(String s, int i, @Provided Bar bar) {
    ...
}
```

When Act detect that `Bar bar` is annotated with `Provided`, it will not try to create an new Bar and 
bind it with request parameters, instead `App::newInstance(Class)` will be called to create 
the `Bar` instance and feed into the `handleXyzRequest` method

**Note** You don't need to add `@Provided` to inject `ActionContext` object, it will always get injected 
into action handler if declared in the parameter list:

```java
public void handleXyzResult(Stirng s, ActionContext context) {
    ...
}
```

## Create module class

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
