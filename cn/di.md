# 依赖注入

ActFramework支持基于[JSR330](https://jcp.org/en/jsr/detail?id=330)的依赖注入. 

## 申明需要注入的对象

ActFramework通过`javax.inject.Inject`注解识别需要注入的对象. 你可以通过一下三种标准方式申明需要注入的对象：

**字段注入**

```java
public class Foo {
    @Inject
    private Bar bar;
}
```

**构造器注入**

```java
public class Foo {
    private Bar bar
    
    @Inject
    public Foo(Bar bar) {
        this.bar = bar;
    }
}
```

**设置器(Setter)注入**

```java
public class Foo {
    private Bar bar
    @Inject
    public void setBar(Bar bar) {
        this.bar = bar;
    }
}
```

**小贴士** 字段注入最为简明，不过对单元测试会造成一些麻烦

## 获取有依赖申明的对象

```java
App app = App.instance();
// this ensure Bar has been injected into Foo
Foo foo = app.newInstance(Foo.class);
```

## 响应器参数的依赖注入

如果你希望框架注入对象到响应器的参数列表，请用`org.osgl.inject.annotation.Provided`注解来声明该参数:

```java
public Result handleXyzRequest(String s, int i, @Provided Bar bar) {
    ...
}
```

一旦ActFramework检测到`Provided`注解, 会使用`App::newInstance(Class)`来创建该对象，否则会使用POJO绑定来创建对象

**小贴士** 对于`act.app.ActionContext`类型不需要使用`Provided`注解，ActFramework总是注入该类型对象

```java
public void handleXyzResult(Stirng s, ActionContext context) {
    ...
}
```

## `AbstractModule`类

如果你以前使用过guice，和通常的Guice应用一样，你可以创建Module类来申明注入绑定规则:

```java
public class GreetingModule extends org.osgl.inject.Module {
    @Override
    protected void configure() {
        bind(GreetingService.class).to(GreetingServiceImpl.class);
    }
}
```

**小贴士** 在ActFramework中你不必使用Module类来创建`Injector`对象实例。框架会自动寻找所有申明的Module类并在
内部创建`Injector`实例

[返回目录](index.md)
