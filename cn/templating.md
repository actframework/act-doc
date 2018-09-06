# 第七章 模板

目前ActFramework支持的模板引擎:

* **[Rythm](http://rythmengine.org)** (框架内置) - 引擎ID: rythm
* [Beetl](http://www.ibeetl.com) - 引擎ID: beetl
* [FreeMarker](http://freemarker.apache.org) - 引擎ID: freemarker
* [Mustache](https://github.com/spullara/mustache.java) - 引擎ID: mustache
* [Thymeleaf](http://www.thymeleaf.org/) - 引擎ID: thymeleaf
* [Velocity](http://velocity.apache.org) - 引擎ID: velocity

**注意** 如果要使用非Rythm模板引擎需要在`pom.xml`文件中分别加入响应的依赖:

Beelt依赖:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-beetl</artifactId>
    <version>1.0.0</version>
</dependency>
```

Freemarker依赖:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-freemarker</artifactId>
    <version>1.0.1</version>
</dependency>
```

Mustache依赖:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-mustache</artifactId>
    <version>1.0.0</version>
</dependency>
```

Thymeleaf依赖:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-thymeleaf</artifactId>
    <version>1.0.0</version>
</dependency>
```


Velocity依赖:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-velocity</artifactId>
    <version>1.0.0</version>
</dependency>
```

## <a name="location"></a>7.1 模板文件的位置

ActFramework依照以下管理访问模板文件:

```
/src/main/resources/{template-plugin-id}/{controller-class}/{action-method}.{fmt-suffix}
```

假设你的控制器类是

```java
package com.mycom.myprj;

public class MyController {
    
    @GetAction("/")
    public void home() {
    }
    
    @GetAction("/foo")
    public Foo getFoo() {
        return Foo.instance();
    }
}
``` 

对应与`home()`和`getFoo()`响应方法的两个模板文件分别为:

1. `/src/main/resources/rythm/com/mycom/myprj/MyController/home.html`
1. `/src/main/resources/rythm/com/mycom/myprj/MyController/getFoo.html`

如果你的应用需要对发送到`/foo`的请求支持`application/json`格式, 你可以创建json格式模板文件如下: 

```
/src/main/resources/rythm/com/mycom/myprj/MyController/getFoo.json
``` 

## <a name="argument"></a> 7.2 模板参数传递

ActFramework使用ASM对响应方法做了增强，因此你不必像在Spring MVC应用中那样显示指定参数

一个SpringMVC的响应方法：

```java
public String foo(String a, String b, int c, ModelMap modelMap) {
    modelMap.put("a", a);
    modelMap.put("b", b);
    modelMap.put("c", c);
    modelMap.put("abc", a + b + c);
    return "/path/to/the/template";
}
```

用ActFramework重写上面的方法:

```java
public Result foo(String a, String b, int c) {
    String abc = a + b + c;
    return render(a, b, c, abc);
}
```

在Rythm引擎中申明参数:

```
@args String a, String b, int c
<pre>
a = @a
b = @b
c = @c
</pre>
```

**注意** 其他模板不需要参数声明

### <a name="passing_return_value"></a> 7.2.1 向模板传递返回值

如果你的控制器需要向模板传递函数返回值

```java
public Foo getFoo() {
    return dao.findOne();
}
```

在模板中通过`result`名字来引用返回值:

```
@args Foo result
Foo is @foo
```

## <a name="reference"></a> 7.3 参考

* [Beetl官网](http://www.ibeetl.com)
* [Freemarker官网](http://freemarker.incubator.apache.org/)
* [Velocity官网](http://velocity.apache.org)
* [Mustache官网](https://github.com/spullara/mustache.java)
* [Rythm官网](http://rythmengine.org)
* [Thymeleaf官网](http://www.thymeleaf.org/)
* [Velocity官网](http://velocity.apache.org)


## <a name="demo_project"></a> 7.4 演示项目

你可以在[github](https://github.com/actframework/act-demo-apps/tree/master/views)或者[码云](https://git.oschina.net/actframework/demo-apps/tree/master/views?dir=1&filepath=views)上访问模板引擎演示项目 

该演示项目展示了:

* 如何在项目中集成多个模板引擎
* 在开发模式下各个模板引擎对错误显示的支持


[返回目录](index.md)
