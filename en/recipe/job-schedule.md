# Actframework: Job scheduling

## Annotations

ActFramework scans annotations on method to schedule the job execution. Belows are support job schedule annotions:

* `@AlongWith` - Specify this method shall be invoked along with another job asynchronously
* `@Cron` - Schedule a method invocation with unix-like cron expression
* `@Every` - Schedule a method to be invoked in a fixed rate
* `@FixedDelay` - Schedule a method to be invoked at fixed delay
* `@InvokeAfter` - Schedule a method to be invoked after another job (synchrously)
* `@InvokeBefore` - Schedule a method to be invoked before another job (synchrously)
* `@OnAppEvent` - Specify the method to be invoked on a certain `AppEvent`
* `@OnAppStart` - Invoke the method on App start
* `@OnAppStop` - Invoke the method on App stop

## Job method

Requirements to a Job method：

1. No return value. Otherwise the returned value will be ignored
2. Cannot have parameters except the the ones can be injected


Job method could be either static or virtual. If the Job method is virtual, the declaring class must not be abstract.

Job method examples:

* unix-like cron scheduling

    ```java
    /**
     * This method is scheduled to run every minute
     */
    @Cron("0 * * * * ?")
    public void backup() {
        JobLog.log("SomeService.backup");
    }
    ```

* Run on application start

    ```java
    @OnAppStart(async = true)
    public void onAppStartAsync() {
        JobLog.log("onAppStartAsync called");
    }
    ```

* A fault Job method declaration: the method list contains parameter that cannot be injected

    ```java
    @Every("3s")
    public String schedule(int n) {
        processor.process("DI in field" + n);
        return "ignored";
    }
    ```

* However if the parameters can be injected, then it should okay

    ```java
    /*
     * Here we support User.Dao and PostMan are injectable types
     */
    @Every("1d")
    public void sendPasswordExpirationReminder(User.Dao userDao, PostMan postman) {
        Iterable<User> users = userDao.passwordExpireSoon();
        for (User user: users) {
            postman.sendPasswordExpireReminderEmail(user);
        }
    }
    ```

## About runtime environment

ActFramework is designed to support scale horizontally. However if we scale our app to multiple nodes, 
the job scheduling will clash to each other as all node will run the Job scheduling.

In order to address this issue, Actframework provided a way to allow it specify the node's group. For 
example, if you want job to be run only on a certain node, start the act application using 
`-Dapp.nodeGroup=job` to mark the node as `job` group. Then use `Env.Group("job")`annotation on
the method to be scheduled so that the method will be invoked only on app that runs in `job` group

```java
/**
 * This method will get called every x, where
 * `x` is configured through `every.check_status`
 * configuration
 */
@Every(value = "every.check_status", id = "CHECK_STATUS")
@Env.Group("job")
public void checkStatus() {
    JobLog.log("SomeService.checkStatus");
}
```

If you want to understand more about job scheduling of Actframework, have a play with the job demo app:

* https://github.com/actframework/act-demo-apps/tree/master/jobs
