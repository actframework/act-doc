# How to create Singleton in ActFramework

Creating singleton class in Java is a [complicated topic](http://www.javaworld.com/article/2073352/core-java/simply-singleton.html) and deserve [long thread of discussion](http://stackoverflow.com/questions/70689/what-is-an-efficient-way-to-implement-a-singleton-pattern-in-java). 

## Standard Java approach

Fortunately since Java5 you got a very neat way to create singleton:

```java
public enum MySingleton {
    INSTANCE;
    // your class implementation starts here
    public String foo() {
        return "foo"
    }
}
```

To reference your singleton class simply refer to the `INSTANCE` field in the enum:

```java
MySingleton.INSTANCE.foo();
```

However if your singleton class needs to extend other class then you can't use the `enum` approach, and you probably need to go back to the classic approach as:

```java
public class MySingleton extends MyBaseClass {
    public static final MySingleton INSTANCE = new MySingleton(); 
}
``` 

or if you want to keep the INSTANCE private:

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

or if you prefer to lazy load:

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

**Tips** Make sure you have the `volatile` keyword in the `INSTANCE` declaration, otherwise you will get into the [trouble](http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html)

## ActFramework specific approach

ActFramework provides you another approach to create singleton class: just add `javax.inject.Singleton` annotation to your class:

```java
@javax.inject.Singleton
public class MySingleton extends MyBaseClass {
    public String foo() {
        return "foo"
    }
}
```

And to use your singleton class:

```java
String s = act.app.App.instance().singleton(MySingleton.class).foo();
```

Or if your application does not have super class to extend, you can extends your class to `act.util.SingletonBase`:

```java
public class MySingleton extends act.util.SingletonBase {
    public String foo() {
        return "foo"
    }
}
```

And to use your class:

```java
MySingleton singleton = MySingleton.instance();
String s = singleton.foo();

// you can also use the App.singleton(Class) way to fetch your singleton:
MySingleton singleton2 = act.app.App.instance().singleton(MySingleton.class);

// or you can just ask App to give you an new instance:
MySingleton singleton3 = act.app.App.instance().newInstance(MySingleton.class);
```

ActFramework makes sure `singleton`, `singleton2` and `singleton3` is the same instance. You can also inject your singleton class into another class:

```java
public class MySingletonConsumer {
    @Inject 
    private MySingleton mySingleton;
}
``` 

or into your [action handler](../controller.md#term)

```java
@GetAction("/foo")
public String sayFoo(@act.di.Context MySingleton mySingleton) {
    return mySingleton.foo();
}
```

## Summary

* The easiest and most clean way to create singleton is to declare your singleton as `enum`
* ActFramework understand `javax.inject.Singleton` annotation and collect your singleton instances in preload registry so that you can refer to your singleton instance through `act.app.App.instance().singleton(Class)` call
* ActFramework can inject your singleton instance to certain context
* ActFramework provides `SingletonBase` class and if your singleton class extends `SingletonBase` you can easily access your singleton instance via `MySingleton.instance()` call

