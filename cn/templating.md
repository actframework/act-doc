# Templating

ActFramework can plugin different templating solutions. At the moment [Rythm](http://rythmengine.org) is fully supported. Freemarker and Velocity is limited supported.

** Note to use freemarker and velcoity templating you need to add the dependencies into your pom.xml file respectively:

Freemarker templating dependency:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-freemarker</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

Velocity templating dependency:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-velocity</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

## <a name="location"></a>template file location

The convention of locating a template file is:

```
/src/main/resources/{template-plugin-id}/{controller-class}/{action-method}.{fmt-suffix}
```

For example, if you have a controller class defined as:

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

You can create two template files corresponding to the `home()` and `getFoo()` action methods:

1. `/src/main/resources/rythm/com/mycom/myprj/MyController/home.html`
1. `/src/main/resources/rythm/com/mycom/myprj/MyController/getFoo.html`

When the request to `/foo` endpoints accept `application/json` content, then you can create a json template at 

```
/src/main/resources/rythm/com/mycom/myprj/MyController/getFoo.json
``` 

## Passing parameter to template

ActFramework use ASM to enhance your controller method so that you don't need to manually put template arguments into a Map type context, like what you did in Spring MVC:

```java
public String foo(String a, String b, int c, ModelMap modelMap) {
    modelMap.put("a", a);
    modelMap.put("b", b);
    modelMap.put("c", c);
    modelMap.put("abc", a + b + c);
    return "/path/to/the/template";
}
```

In Act, your code could much more clean:

```java
public Result foo(String a, String b, int c) {
    String abc = a + b + c;
    return render(a, b, c, abc);
}
```

And in your rythm template you declare your template argument and use them as follows:

```
@args String a, String b, int c
<pre>
a = @a
b = @b
c = @c
</pre>
```

## Passing return result to template

If your action method just need to pass one parameter into the template, you can choose to return it:

```java
public Foo getFoo() {
    return dao.findOne();
}
```

In your template you use name `result` to refer to the return instance:

```
@args Foo result
Foo is @foo
```

## Reference

* Please visit [Rythm official website](http://rythmengine.org) to get detail information on how to use Rythm
* Please visit [Velocity official website](http://velocity.apache.org) to get detail information on how to use Velocity
* Please visit [Freemarker official website](http://freemarker.incubator.apache.org/) to get detail information on how to use Freemarker

[Back to index](index.md)