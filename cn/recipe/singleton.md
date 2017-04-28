# 如何在ActFramework中创建单例

Java中创建单例是一个[有趣的话题](http://www.javaworld.com/article/2073352/core-java/simply-singleton.html) 人们对此进行了[很多讨论](http://stackoverflow.com/questions/70689/what-is-an-efficient-way-to-implement-a-singleton-pattern-in-java). 

## 常规方法

自从有了Java 5之后创建单例就变得及其简单了：

```java
public enum MySingleton {
    INSTANCE;
    // your class implementation starts here
    public String foo() {
        return "foo"
    }
}
```

引用单例的方法:

```java
String s = MySingleton.INSTANCE.foo();
```

如果你的单例需要继承其他类，你就不能使用`enum`方式。这个时候你可以使用老方法:

```java
public class MySingleton extends MyBaseClass {
    public static final MySingleton INSTANCE = new MySingleton(); 
}
``` 

如果希望INSTANCE私有的话：

```java
public class MySingleton extends MyBaseClass {
    private static final MySingleton INSTANCE = new MySingleton();
    public static MySingleton instance() {
        return INSTANCE;
    } 
    public String foo() {
        return "foo"
    }
}
``` 

或者说你希望延迟加载的话：

```java
public class MySingleton extends MyBaseClass {
    private static volatile final MySingleton INSTANCE;
    public static MySingleton instance() {
        if (null == INSTANCE) {
            synchronized(MySingleton.class) {
                if (null == INSTANCE) {
                    INSTANCE = new MySingleton();
                }
            }
        }
        return INSTANCE;
    }
}
``` 

**提示** 你需要在`INSTANCE`申明上引入`volatile`关键字，否则就会落入[双锁检查陷阱](http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html)

## ActFramework特别支持

在ActFramework你还有一种简单的使用单例模式的办法，在类申明上加入`javax.inject.Singleton`注解:

```java
@javax.inject.Singleton
public class MySingleton extends MyBaseClass {
    public String foo() {
        return "foo"
    }
}
```

使用单例对象：

```java
String s = act.app.App.instance().singleton(MySingleton.class).foo();
```

如果你的类不需要继承其他类的话你也可以考虑直接继承`act.util.SingletonBase`：

```java
public class MySingleton extends act.util.SingletonBase {
    public String foo() {
        return "foo"
    }
}
```

这个方法和`javax.inject.Singleton`注解效果是一样的，而且引用单例对象变得更加简单：

```java
MySingleton singleton = MySingleton.instance();
String s = singleton.foo();

// you can also use the App.singleton(Class) way to fetch your singleton:
MySingleton singleton2 = act.app.App.instance().singleton(MySingleton.class);

// or you can just ask App to give you an new instance:
MySingleton singleton3 = act.app.App.instance().getInstance(MySingleton.class);
```

上面的代码中，ActFramework确保`singleton`, `singleton2` 和 `singleton3` 都指向同一个实例。你还可以把单例注入到其他类中：

```java
public class MySingletonConsumer {
    @Inject 
    private MySingleton mySingleton;
}
``` 

或者注入到你的[响应器](../controller.md#term)方法中：

```java
@GetAction("/foo")
public String sayFoo(@act.di.Context MySingleton mySingleton) {
    return mySingleton.foo();
}
```

## 总结

* 创建单例最简明的方法是把你的类申明为`enum`
* ActFramework识别`javax.inject.Singleton`注解并自动加载单例实例到内存。应用程序可以使用`act.app.App.instance().singleton(Class)`调用来获取单例
* ActFramework实现了单例注入
* ActFramework提供`SingletonBase`类。如果应用类继承了`SingletonBase`，单例可以通过`MySingleton.instance()`调用来获取

