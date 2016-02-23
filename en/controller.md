# Controller and Action handler

ActFramework provides maximum flexibility in creating controller and action handlers.

## <a name="term"></a>Terms

1. **Controller**. Controller refers to a class that contains one or more action handlers. 
    
    Note in ActFramework it does NOT require controller class to extend a specific class, neither does it require controller class to be annotated with a certain annotation  

1. **Action handler**. An action handler is a method that provides logic to handler an incoming request. In other words, action handler is a method that has been configured as a route destination.

    An action handler could be either static method or non-static method

## <a name="a-simple-controller"></a>A simple controller

A very simple controller with one action handler could be as simple as

```java
package com.mycom.myprj.controller;

public class MyController {
    public void home() {}
}
``` 

The `home()` method is an action handler if there are an entry in the `route` table like:

```
GET / com.mycom.myprj.controller.MyController.home
```

You can also use annotation based routing which is usually easier way to go:

```
@GetAction("/")
public void home() {}
```

If you haven't define any template, the handler will be a dumb handler which returns `200 Okay` response with no body content

If you have a template file created in proper location, ActFramework will render that file and put the render result into the response body

**Tips** Although it does not require a controller to extend any class, it is good to have your controller class to extend `act.controller.Controller.Util` to get a set of handy utilities that helps to return responses. If your controller already extends other classes, you can use `static import` to achieve the same effect as demonstrated below:

1. Extend `act.controller.Controller.Util`:

    ```java
    import act.Controller;
    public class MyController extends Controller.Util {
        ...
    } 
    ``` 

1. import static:

    ```java
    import static act.Controller.Util.*;
    public class MyController extends Controller.Util {
        ...
    } 
    ```

**Note** In the following section of this page, it assumes the controller code has extended the `Controller.Util` class or has the static import statement as shown above.

 
## <a name="parameter"></a>Getting parameters

ActFramework automatically popluate your action handler parameters from

1. URL path variables
1. Query parameters
1. Form post parameters

```
@PutAction("/customer/{customerId}/order/{orderId}")
public void updateOrderAmount(String customerId, String orderId, int amount) {
    ...
}
```

In the above example, the `customerId` and `orderId` is the URL path variable and `amount` is either the query param specified in the URL or the form data depending on the PUT request encoding.

### <a name="binding"></a>Binding to complex form data

ActFramework support binding complex form data to a domain model class. Suppose you have the following model class:

```java
public class Order {
    private String id;
    private String customerId;
    private List<Item> items;
    
    public String getId() {
        return id;
    } 
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public List<Item> getItems() {
        return items;
    }
    
    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    public static class Item {
        private String description;
        private int amount;
        
        public String getDescription() {
            return description;
        }
        
        public void setDecsription(String desc) {
            this.description = desc;
        }
        
        public int getAmount() {
            return amount;
        }
        
        public void setAmount(int amount) {
            this.amount = amount;
        }
    }
}
```

Your html order creation form:

```html
<form action="/customer/@customer.getId()/order" method="POST">
<div class="line-item">
    <span class="desc"><input name="order[items][][description]"></span>
    <span class="amount"><input name="order[items][][amount]"></span>
</div>
<div class="line-item">
    <span class="desc"><input name="order[items][][description]"></span>
    <span class="amount"><input name="order[items][][amount]"></span>
</div>
<div class="line-item">
    <span class="desc"><input name="order[items][][description]"></span>
    <span class="amount"><input name="order[items][][amount]"></span>
</div>
...
</form>
```

You can have an action to handle creating new `Order`:

```java
@PostAction("/customer/{customerId}/order")
public void createOrder(String customerId, Order order) {
    order.setCustomerId(customerId);
    dao.save(order);
}
```

### <a name="json-param"></a>Binding to JSON body

The above `createOrder` method is also able to bind the JSON body: 

```JSON
{
    "items": [
        {
            "description": "item 1",
            "amount": 10000
        },
        {
            "description": "item 2",
            "amount": 12300
        },
        ...
    ]
}
```

**Note** ActFramework does NOT support binding to XML data at the current stage 

## <a name="response"></a>Specify responses

With ActFramework you have multiple ways of specifying response to be sent back, all of them are easy to understand and very expressive.

### <a name="implicity-200"></a>Implicit 200 Okay

If there is no return type and thrown exception on an action handler method, ActFramework will automatically return an empty `200 Okay` response unless a template has been defined for the method. This is useful when the action handler is to servicing a RESTful POST or PUT request, e.g.

```java
@PostAction("/order")
public void createOrder(Order order) {
    orderService.save(order);
}
```

### <a name="explicity-200"></a>Explicity 200 Okay

For developer who really want to make everything be explicity, here are two ways to create a `200 Okay` response:

1. Return result

    ```java
    @PostAction("/order")
    public Result createOrder(Order order) {
        orderService.save(order);
        return ok();
        // or return new Ok();
    }
    ```

1. Throw out result

    ```java
    @PostAction("/order")
    public void createOrder(Order order) {
        orderService.save(order);
        throw ok();
        // or throw new Ok();
    }
    ```
You can even throw out the result implicitly

    ```java
    @PostAction("/order")
    public void createOrder(Order order) {
        orderService.save(order);
        ok();
    }
    ```
**Note** ActFramework will enhance your controller action method, so that if a `Result` type exception has been returned in the source code be thrown out automatically.

### <a name="return-404"></a>Return 404 Not Found

The server respond with `404 NotFound` automatically when it cannot find a handler to service an incoming request in route table. However there are cases that your business logic needs to return a 404 response, e.g. when a query to an order by order ID cannot locate the order in the database with the given order ID, here is what you can do:

```java
@GetAction("/order/{orderId}")
public Order getOrder(String orderId) {
    Order order = dao.findById(orderId);
    if (null == order) {
        throw new NotFound();
    }
}
```

A more expressive way to do that is:

```java
@GetAction("/order/{orderId}")
public Order getOrder(String orderId) {
    Order order = dao.findById(orderId);
    notFoundIfNull(order);
}
```

The utmost expressive way is:

```java
@GetAction("/order/{orderId}")
public Order getOrder(String orderId) {
    return dao.findById(orderId);
}
```

ActFramework will check if there is return type on action handler signature and it returns `null` then 404 will be send to response automatically.

### <a name="return-400"></a>Return other error request

Here is the demo code shows how to return response with different HTTP status code

```java
public void foo(int status) {
    badRequestIf(400 == status);
    unauthorizedIf(401 == status);
    forbiddenIf(403 == status);
    notFoundIf(404 == status);
    conflictIf(409 == status);
    // not anyone of the above?
    throw ActServerError.of(status);
} 
```

### <a name="exception-mapping"></a>Automatic map Java Exception to Response

Got exception not handled? ActFramework map them to response automatically!

TBD

### <a name="return-data"></a>Returning data to response

ActFramework does not require you to return a `Result` type in your action handler if there are data needs to be returned although you are free to do that. The following two action handlers have the same effect when the `accept` header is `application/json`:

```java
@GetAction("/order/{orderId}")
public Order getOrder(String orderId) {
    return dao.findById(orderId);
}
```

```java
@GetAction("/order/{orderId}")
public Result getOrder(String orderId) {
    Order order = orderService.findById(orderId);
    return renderJSON(order);
}
```

However the first style is recommended because:

1. It is simpler
1. It allow the flexibility of content-negotiation

### <a name="render-template"></a>Render template

For classic MVC application it always needs to render response via templating solution. There are three ways to render through templating.

1. Implicity template rendering

    For any action handler, if the corresponding template is defined, the template will always be called to render the response
    
    If an action handler has return value, the value will be passed to the template by variable named `result`
    
1. Explicity `renderTemplate` call

    ```java
    @GetAction("/order/editForm")
    public Result orderEditForm(String orderId) {
        Order order = orderService.findById(orderId);
        boolean hasWritePermission = ...;
        return renderTemplate(order, hasWritePermission);
    }
    ```
    The above code will call the template (location by convention) with parameter named `order` and `hasWritePermission`
    
1. Specify the template path

    ```java
    @GetAction("/order/editForm")
    public Result orderEditForm(String orderId) {
        Order order = orderService.findById(orderId);
        boolean hasWritePermission = ...;
        return renderTemplate("/myTemplateRoot/orderForm.html", order, hasWritePermission);
    }
    ```
    As shown above, when the first parameter passed to `renderTemplate` is a String literal, (not String variable), it will treated as template path, instead of render argument
    

TBD