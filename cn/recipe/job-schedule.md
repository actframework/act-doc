# Actframework的任务调度

## 任务调度注解

在Actframework的应用当中进行任务调度的方式是使用任务调度注解标记任务方法。
ActFramework支持的任务调度注解包括：

* `@AlongWith` - 指定该方法与某个任务一同执行（异步）
* `@Cron` - 使用类unix的cron表达式来调度执行该方法
* `@Every` - 定期执行该方法
* `@FixedDelay` - 固定间隔执行该方法
* `@InvokeAfter` - 指定该方法在某个任务之后执行（同步）
* `@InvokeBefore` - 指定该方法在某个任务之前执行（同步）
* `@OnAppEvent` - 指定当某个`AppEvent`触发时执行该方法
* `@OnAppStart` - 当App启动时执行该方法
* `@OnAppStop` - 当App停止时执行该方法

## 任务方法

任务方法的要求：

1. 没有返回值，如果有返回值，返回值会被自动忽略
2. 除了能进行依赖注入的类型，不能有其他类型的参数


任务方法可以是静态的也可以是虚函数。当任务方法不是静态方法的时候，声明方法的类不能是抽象类。任务方法示例：

* 使用类unix cron表达式调度

    ```java
    /**
     * This method is scheduled to run every minute
     */
    @Cron("0 * * * * ?")
    public void backup() {
        JobLog.log("SomeService.backup");
    }
    ```

* 当应用启动完成后调度

    ```java
    @OnAppStart(async = true)
    public void onAppStartAsync() {
        JobLog.log("onAppStartAsync called");
    }
    ```

* 一个错误声明的任务方法，方法参数列表中有一个无法进行依赖注入的参数

    ```java
    @Every("3s")
    public String schedule(int n) {
        processor.process("DI in field" + n);
        return "ignored";
    }
    ```

* 如果方法声明中的参数可以被依赖注入，则方法是有效的任务方法：

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

## 关于运行环境

ActFramework是能够进行水平扩容的。假设我们有多台服务器运行同样的ActFramework应用，任务调度势必发生冲突。Act提供了一种巧妙的解决办法。在启动应用的时候可以使用`-Dapp.nodeGroup=xxx`参数来指定当前应用节点的`group`，比如`-Dapp.nodeGroup=job`, 然后在任务方法上使用`Env.Group("job")`来指定这个方法只能在指定为`job`group的应用节点上运行：

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

这是一种简单易用的处理多应用服务任务调度冲突的办法

需要进一步了解ActFramework的任务调度可以试试运行调试任务调度演示项目：

* https://github.com/actframework/act-demo-apps/tree/master/jobs
