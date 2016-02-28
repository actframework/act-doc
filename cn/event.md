# Event in ActFramework

ActFramework provides utmost expressive way to bind and dispatch events.

## Simple Event Framework

Simple event framework allows developer to use any String as the event to trigger and bind event handlers. And event handler could be simple as a public method that are annotated with `act.event.On` annotation:

### Declaring event handler

```java
public class Foo {
    @On(value = "customer-created", async = true)
    public void sendWelcomeEmail(Contact newCustomer) {
        ...
    }
}
```

### Trigger event

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

## Typesafe Event Framework

Typesafe event framework is more classic way to bind and dispatch event.


### Declaring event handler

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

### Trigger event


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

## Comparison between two event framework

<table>
<thead>
<tr>
<th></th><th>Pros</th><th>Cons</th>
</tr>
</thead>
<tbody>
<tr>
<td>Simple event framework</td>
<td>
Simple, lightweight and very expressive to declare event handler
</td>
<td>
Not type safe<br/> reflection based method call
</td>
</tr>
<tr>
<td>Typesafe event framework</td>
<td>
Typesafe; better runtime performance
</td>
<td>
Slightly verbose to declare event handler
</td>
</tr>
</tbody>
</table>

[Back to index](index.md)