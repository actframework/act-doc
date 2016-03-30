# 依赖注入

ActFramework支持基于[JSR330](https://jcp.org/en/jsr/detail?id=330)的依赖注入. 

目前有两种实现：

1. [基于Guice的实现](https://github.com/actframework/act-guice-plugin)。要使用guice实现，在`pom.xml`文件中加入以下依赖：

    ```
    <dependency>
        <groupId>org.actframework</groupId>
        <artifactId>act-guice</artifactId>
        <version>0.1.1-SNAPSHOT</version>
    </dependency>
    ```

2. [基于Feather的实现](https://github.com/actframework/act-feather)。要使用feather实现，在`pom.xml`文件中加入：

    ```
    <dependency>
        <groupId>org.actframework</groupId>
        <artifactId>act-feather</artifactId>
        <version>0.1.1-SNAPSHOT</version>
    </dependency>
    ```


**注意** 你不能在项目中同时使用guice和feather. 你只能任选一个加入到你的项目中

## 申明需要注入的对象

ActFramework通过`javax.inject.Inject`注解识别需要注入的对象. 你可以通过一下三种标准方式申明需要注入的对象：

### 字段注入

```java
public class Foo {
    @Inject
    private Bar bar;
}
```

### 构造器注入

```java
public class Foo {
    private Bar bar
    
    @Inject
    public Foo(Bar bar) {
        this.bar = bar;
    }
}
```

### 设置器(Setter)注入

```java
public class Foo {
    private Bar bar
    @Inject
    public void setBar(Bar bar) {
        this.bar = bar;
    }
}
```

**注意** Feather实现目前不支持设置器注入！

**小贴士** 字段注入最为简明，不过对单元测试会造成一些麻烦

## 获取有依赖申明的对象

在ActFramework里你不需要使用`Guice`的`Injector`来获取对象实例。请使用`act.app.App::newInstance(Class)`方法来创建对象实例:

```java
App app = App.instance();
// this ensure Bar has been injected into Foo
Foo foo = app.newInstance(Foo.class);
```

## 响应器参数的依赖注入

如果你希望框架注入对象到响应器的参数列表，请用`act.di.Context`注解来声明该参数:

```java
public Result handleXyzRequest(String s, int i, @Context Bar bar) {
    ...
}
```

一旦ActFramework检测到`Context`注解, 会使用`App::newInstance(Class)`来创建该对象，否则会使用POJO绑定来创建对象

**小贴士** 对于`act.app.ActionContext`类型不需要使用`Context`注解，ActFramework总是注入该类型对象

```java
public void handleXyzResult(Stirng s, ActionContext context) {
    ...
}
```

## `AbstractModule`类

如果使用guice实现，和通常的Guice应用一样，你可以创建Module类来申明注入绑定规则:

```java
public class GreetingModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GreetingService.class).to(GreetingServiceImpl.class);
    }
}
```

**小贴士** 在ActFramework中你不必使用Module类来创建`Injector`对象实例。框架会自动寻找所有申明的Module类并在内部创建`Injector`实例

[返回目录](index.md)
