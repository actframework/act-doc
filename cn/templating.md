# 模板

ActFramework支持不同的模板引擎. [Rythm](http://rythmengine.org)是缺省引擎，目前支持最好. 另外对Freemarker和Velocity提供了有限的支持.

**注意** 如果要使用freemarker或velcoity需要在`pom.xml`文件中分别加上下面的依赖:

Freemarker依赖:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-freemarker</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

Velocity依赖:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-velocity</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

## <a name="location"></a>模板文件的位置

ActFramework依照一下管理访问模板文件:

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

对应与`home()`和`getFoo()`响应方法的连个模板文件分别为:

1. `/src/main/resources/rythm/com/mycom/myprj/MyController/home.html`
1. `/src/main/resources/rythm/com/mycom/myprj/MyController/getFoo.html`

如果你的应用需要对发送到`/foo`的请求支持`application/json`格式, 你可以创建json格式模板文件如下: 

```
/src/main/resources/rythm/com/mycom/myprj/MyController/getFoo.json
``` 

## 模板参数传递

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

## 想模板传递返回值

如果你的控制器需要向模板传递返回值

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

## 参考

* [Rythm官网](http://rythmengine.org)
* [Velocity官网](http://velocity.apache.org)
* [Freemarker官网](http://freemarker.incubator.apache.org/)

[返回目录](index.md)