# Jobs and Scheduler

Job and scheduler is one of the best feature of ActFramework. It makes job scheduling really a handy piece of work. What you need to do is just create a public method without return result and parameter. The method could be either static or virtual.

## Run logic regularly

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

## Run logic at fixed delay

```java
import act.job.FixedDelay;

public class Foo {
    @FixedDelay("40mn") 
    public void scheduledToRunFourtyMinsAfterLastRunFinished() {
        ...
    }
}
```

## Cron job

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

## Invoke along with other job

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

## Listening to application events

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

## Adhoc job schedule

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

[Back to index](index.md)