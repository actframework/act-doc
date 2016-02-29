# 事件

ActFramework提供简单易用的事件绑定和分派机制

## 简单事件框架

简单事件让开发人员直接使用字串来定义事件。而事件响应方法则是一个有`act.event.On`注解标注的普通Java方法。分派简单事件可以传入任何参数，这些参数都将传入事件响应方法。

### 申明事件响应方法

```java
public class Foo {
    @On(value = "customer-created", async = true)
    public void sendWelcomeEmail(Contact newCustomer) {
        ...
    }
}
```

### 触发事件

```java
@Controller("/customer")
public class CustomerController {
    
    @PostAction("/")
    public void createCustomer(Customer customer, @Context EventBus eventBus) {
        customerDao.save(customer);
        eventBus.trigger("customer-created", customer);
    }
}
```

## 类型安全事件框架

类型安全事件框架实现更加传统的事件绑定和分派机制


### 申明事件响应方法

```java
import act.event.ActEvent;
import act.event.ActEventListenerBase;
import act.util.Async;

@Async
public class CustomerCreated extends ActEventListenerBase<ActEvent<Customer>> {
    @Override
    public void on(ActEvent<Customer> event) throws Exception {
        Customer customer = event.source();
        // send welcome email to customer
    }
}
```

### 触发事件


```java
@Controller("/customer")
public class CustomerController {
    
    @PostAction("/")
    public void createCustomer(Customer customer, @Context EventBus eventBus) {
        customerDao.save(customer);
        eventBus.trigger(new ActEvent<Customer>(customer));
    }
}
```

## 两种事件框架的比较

<table>
<thead>
<tr>
<th></th><th>Pros</th><th>Cons</th>
</tr>
</thead>
<tbody>
<tr>
<td>简单事件框架</td>
<td>
简单，轻量，更易表达
</td>
<td>
没有类型安全<br/>基于反射的事件方法调用
</td>
</tr>
<tr>
<td>类型安全事件框架</td>
<td>
类型安全; 运行时效率更高
</td>
<td>
申明事件响应器以及触发事件的代码较为冗长
</td>
</tr>
</tbody>
</table>

[返回目录](index.md)