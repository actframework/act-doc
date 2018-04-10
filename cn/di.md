# 第三章 依赖注入

ActFramework支持基于[JSR330](https://jcp.org/en/jsr/detail?id=330)的依赖注入. 

* 1 [申明需要注入的对象](#declare_inject_target)
	* 1.1 [字段(field)注入](#field_injection)
	* 1.2 [构造器(constructor)注入](#constructor_injection)
	* 1.3 [设置器(setter)注入](#setter_injection)
	* 1.4 [方法参数注入](#param_injection)
* 2 [手动获取对象实例](#get_instance)
* 3 [通过 Module 类声明绑定](#module)


## <a name="declare_inject_target"></a>1. 申明需要注入的对象

ActFramework通过`javax.inject.Inject`注解识别需要注入的对象. 你可以通过以下三种标准方式申明需要注入的对象：

### <a name="field_injection"></a>1.1 字段注入

```java
public class Foo {
    @Inject
    private Bar bar;
}
```

**小贴士** 字段注入最为简明，不过对单元测试会造成一些麻烦

### <a name="constructor_injection"></a>1.2 构造器注入

```java
public class Foo {
    private Bar bar
    
    @Inject
    public Foo(Bar bar) {
        this.bar = bar;
    }
}
```

### <a name="setter_injection"></a>1.3 设置器(Setter)注入

```java
public class Foo {
    private Bar bar
    @Inject
    public void setBar(Bar bar) {
        this.bar = bar;
    }
}
```

## <a name="param_injection"></a>1.4 方法参数注入

方法参数注入和前面三种注入不一样, 是 ActFramework 特有的功能. Actframework支持三种方法参数注入：

1. [请求处理方法](controller.md)
2. [命令处理方法](cli.md)
3. [任务方法](job.md)

当框架检测到响应函数参数列表中某个参数类型有依赖注入绑定，框架自动使用依赖注入提供该参数值

```java
@GetAction("xyz")
public Result handleXyzRequest(String s, int i, ActionContext context, XyzDao dao) {
    ...
}
```

在上面的方法参数中, `ActionContext` 和 `XyzDao` 两个类都是有依赖注入绑定的, 因此 `context` 和 `dao` 两个参数会被依赖注入, 而 `s` 和 `i` 则因为 `String` 和 `int` 类没有依赖注入绑定而从请求参数中解析

## <a name="get_instance"></a>2. 手动获取对象实例

应用可以使用 `Act.getInstance` 静态方法来获取对象实例:

```java
// this ensure Bar has been injected into Foo
Foo foo = Act.getInstance(Foo.class);
```

## <a name="module"></a>3. 通过 Module 类声明绑定

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



\newpage
