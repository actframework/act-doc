# Dependency Injection

ActFramework support Dependency Injection with Google Guice. You need to add the following dependency in your `pom.xml` file to use it:

```
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-guice</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## Declare inject object

In ActFramework the `javax.inject.Inject` annotation is used to declare the inject object. You can declare the inject object follow the three standard ways

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

Always use `act.app.App.newInstance(Class)` method to create the instance you need, it will inject the dependency declared in your class

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

You can ask Act to inject class instance you need in an controller action method by declaring the parameter with `act.di.Context` annotation:

```
public Result handleXyzRequest(String s, int i, @Context Bar bar) {
    ...
}
```

When Act detect that `Bar bar` is annotated with `Context`, it will not try to create an new Bar and bind it with request parameters, instead `App.newInstance()` will be called to create the `Bar` instance and feed into the `handleXyzRequest` method

## Create module class

Like your usual Guice application, you are free to create any number of Module class e.g.

```java
public class GreetingModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GreetingService.class).to(GreetingServiceImpl.class);
    }
}
```

**Tips** You don't need to create your own `Injector` via calling `Guice.createInjector(...)`. Declaring your module classes, and Act will locate them to create the injector for you. 