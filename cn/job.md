# 任务调度

任务调度支持是ActFramework的一项很棒的功能，你可以用一种前所未有的简洁方式来进行任务调度，只需要在没有返回类型和参数类型的方法上使用不同的注解制定任务触发条件即可。方法可以是静态的或者虚方法。

## 定期运行

```java
import act.job.Every;

public class Foo {
    @Every("1d")
    public void runEveryOneDay() {
        ...
    }
    
    @Every("3h")
    public void runEveryThreeHours() {
        ...
    }
    
    @Every("5mn")
    public void runEveryFiveMinutes() {
        ...
    }
    
    @Every("10s")
    public void runEveryTenSeconds() {
        ...
    }
    
    @Every
    public void runEverySecond() {
        ...
    }
    
}
```

## 固定间隔运行

```java
import act.job.FixedDelay;

public class Foo {
    @FixedDelay("40mn") 
    public void scheduledToRunFourtyMinsAfterLastRunFinished() {
        ...
    }
}
```

## Cron调度

```java
import act.job.Cron;

public class Foo {
    @Cron("0 0 0/12 * * ?")
    public void runEvery12Hours() {
        ...
    }
    
    @Cron("cron.password_reminder.scan")
    public void runPerConfiguredCrontab() {
        ...
    }
}
```

## 和其他任务联动执行

```java
package com.mycom.myrpj;

import act.job.*;

public class Foo {
   @Every("5s")
   public void jobA() {
       ...
   } 
   
   @AlongWith("com.mycom.myrpj.Foo.jobA")
   public void asyncInvokeAlongWithJobA() {
       ...
   }
   
   @InvokeBefore("com.mycom.myrpj.Foo.jobA")
   public void invokeBeforeJobA() {
       ...
   }
   
   @InvokeAfter("com.mycom.myrpj.Foo.jobA")
   public void invokeAfterJobA() {
       ...
   }
}
```

## 处理应用程序事件

```java
import act.job.*;
import act.app.event.*;

public class Foo {
    @OnAppStart
    public void invokeAfterApplicationStarted() {
        ...
    }
    
    @OnAppStart(async = true)
    public void asyncInvokeAfterApplicationStarted() {
        ...
    }
    
    @OnAppEvent(AppEventId.CONFIG_LOADED)
    public void invokeOnAppEvent() {
        ...
    }
    
    @OnAppEvent(value = AppEventId.CLASS_LOADED, async = true)
    public void asyncInvokeOnAppEvent() {
        ...
    }
}
```

## 通过API调度任务

```java
@GetAction
public void home(@Context AppJobManager jobManager) {
    jobManager.now(new Runnable() {
        @Override
        public void run() {
            System.out.println("home entry invoked");
        }
    });
    jobManager.delay(new Runnable() {
        @Override
        public void run() {
            System.out.println("delayed log");
        }
    }, "5s");
    String engine = "rythm";
    Controller.Util.render(engine);
}
```

[返回目录](index.md)