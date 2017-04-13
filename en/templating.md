# Templating 

At the moment ActFramework support the following template engines:

* **[Rythm](http://rythmengine.org)** (built-in) - engine ID: rythm
* [Beetl](http://www.ibeetl.com) - engine ID: beetl
* [FreeMarker](http://freemarker.apache.org) - engine ID: freemarker
* [Mustache](https://github.com/spullara/mustache.java) - engine ID: mustache
* [Thymeleaf](http://www.thymeleaf.org/) - engine ID: thymeleaf
* [Velocity](http://velocity.apache.org) - engine ID: velocity

**Note** If app needs to use template engine other than the built-in rythm, it has to add the corresponding dependency into `pom.xml` file:

For Beelt:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-beetl</artifactId>
    <version>1.0.0</version>
</dependency>
```

For Freemarker:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-freemarker</artifactId>
    <version>1.0.1</version>
</dependency>
```

For Mustache:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-mustache</artifactId>
    <version>1.0.0</version>
</dependency>
```

For Thymeleaf:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-thymeleaf</artifactId>
    <version>1.0.0</version>
</dependency>
```


For Velocity:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-velocity</artifactId>
    <version>1.0.0</version>
</dependency>
```

## <a name="location"></a>Location of template file

ActFramework use the following pattern to look for template file:

```
/src/main/resources/{template-plugin-id}/{controller-class}/{action-method}.{fmt-suffix}
```

Suppose your controller class is

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

The template file correspondign to `home()` and `getFoo()` methods are:

1. `/src/main/resources/rythm/com/mycom/myprj/MyController/home.html`
1. `/src/main/resources/rythm/com/mycom/myprj/MyController/getFoo.html`

If the app needs to send request to `/foo` using `application/json` content type, then you can
create JSON template file like:

```
/src/main/resources/rythm/com/mycom/myprj/MyController/getFoo.json
``` 

## Passing render arguments to template

ActFramework use ASM to enhance the action handler method, thus you don't need to explicitly specify render args 
as what you did in SpringMVC:


```java
public String foo(String a, String b, int c, ModelMap modelMap) {
    modelMap.put("a", a);
    modelMap.put("b", b);
    modelMap.put("c", c);
    modelMap.put("abc", a + b + c);
    return "/path/to/the/template";
}
```

The same method written in ActFramework app:

```java
public Result foo(String a, String b, int c) {
    String abc = a + b + c;
    return render(a, b, c, abc);
}
```

You must declare template arguments in Rythm template:

```
@args String a, String b, int c
<pre>
a = @a
b = @b
c = @c
</pre>
```

**Note** If you are using other template engine, then you don't need to declare template arguments

## Passing return value to template

If the action handler return an object like

```java
public Foo getFoo() {
    return dao.findOne();
}
```

You can reference the returned value using argument name `result`:

```
@args Foo result
Foo is @foo
```

## Reference

* [Beetl](http://www.ibeetl.com)
* [Freemarker](http://freemarker.incubator.apache.org/)
* [Velocity](http://velocity.apache.org)
* [Mustache](https://github.com/spullara/mustache.java)
* [Rythm](http://rythmengine.org)
* [Thymeleaf](http://www.thymeleaf.org/)
* [Velocity](http://velocity.apache.org)

### Demo project

You can checkout the view demo project at [github](https://github.com/actframework/act-demo-apps/tree/master/views) or [码云](https://git.oschina.net/actframework/demo-apps/tree/master/views?dir=1&filepath=views)

The demo project shows:

* Integrate multiple template engines in your project
* How different template engines present error info in dev mode

[Back](index.md)
