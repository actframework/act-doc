# 依赖注入

ActFramework支持基于[JSR330](https://jcp.org/en/jsr/detail?id=330)的依赖注入. 

## 申明需要注入的对象

ActFramework通过`javax.inject.Inject`注解识别需要注入的对象. 你可以通过以下三种标准方式申明需要注入的对象：

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
Foo foo = app.getInstance(Foo.class);
```

## 方法参数的依赖注入

Actframework支持三种方法参数注入：

1. [响应器方法](controller.md)
2. [命令器方法](cli.md)
3. [任务方法](job.md)

当框架检测到响应函数参数列表中某个参数类型有依赖注入绑定，框架自动使用依赖注入提供该参数值

```java
// suppose XyzDao has bound provider, then framework will use the provider to 
// value for `dao` parameter
public Result handleXyzRequest(String s, int i, ActionContext context, XyzDao dao) {
    ...
}
```

## 通过Moudule类声明绑定

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
