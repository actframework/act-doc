# Controller and Action handler

ActFramework provides maximum flexibility while creating controller and action handlers.

## <a name="term"></a>Concept

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

The `home()` method is an action handler if there is an entry in the `/resources/routes.conf` file like:

```
GET / com.mycom.myprj.controller.MyController.home
```

You can also use annotation based routing which is usually easier way to go:

```
@GetAction("/")
public void home() {}
```

If you haven't defined any template, the handler will be a dumb handler which returns `200 Okay` response with no body content

If you have a template file created in [proper location](templating.md#location), ActFramework will render that file and put the render result into the response body

**Tips** Although it does not require a controller to extend any class, it is good to have your controller class extend `act.controller.Controller.Util` to get a set of handy utilities that helps to return responses. If your controller already extends other classes, you can use `static import` to achieve the same effect as demonstrated below:

1. Extend `act.controller.Controller.Util`:

    ```java
    import act.Controller;
    public class MyController extends Controller.Util {
        ...
    } 
    ``` 

1. Import static:

    ```java
    import static act.Controller.Util.*;
    public class MyController extends Controller.Util {
        ...
    } 
    ```

**Note** In the following section of this page, it is assumed that the controller code has extended the `Controller.Util` class or has the static import statement as shown above.

 
## <a name="parameter"></a>Getting parameters

ActFramework automatically populates your action handler parameters from

1. URL path variables
1. Query parameters
1. Form post parameters

```java
@PutAction("/customer/{customerId}/order/{orderId}")
public void updateOrderAmount(String customerId, String orderId, int amount) {
    ...
}
```

In the above example, the `customerId` and `orderId` is the URL path variable and `amount` is either the query parameter specified in the URL or the form data depending on the PUT request encoding.

### <a name="binding"></a>Binding to POJO

ActFramework support binding of complex form data to a domain model class (a POJO). Assume you have the following model class:

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

### <a name="json-param"></a>Binding from JSON content

The above `createOrder` method is also able to bind to the JSON body: 

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

### <a name="file"></a>Binding to file

Assume you have the file upload form in your html page:

```html
<form method="POST" enctype="multipart/form-data" action="/upload">
    Please specify file to upload: <input type="file" name="myfile"><br />
    <input type="submit" value="submit">
</form>
```

You can declare the file in your action handler method as:

```java
public void handleUpload(File myfile) {
    ...
}
```


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

### <a name="explicity-200"></a>Explicit 200 Okay

For developer who really want to make everything be explicit, here are two ways to create a `200 Okay` response:

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
**Note** ActFramework will enhance your controller action method, so that if a `Result` type exception has been returned in the source code it is to be thrown out automatically.

### <a name="return-404"></a>Return 404 Not Found

The server responds with `404 NotFound` when it cannot find a handler to service to an incoming request in route table. However there are cases that your business logic needs to return a 404 response, e.g. when a query to an order by order ID cannot locate the order in the database with the given order ID, here is what you can do:

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

ActFramework will check if there is a return type on action handler signature and if it returns `null` then 404 will be send as response.

### <a name="return-400"></a>Return other error request

Here is the demo code shows how to return a response with different HTTP status codes

```java
public void foo(int status) {
    badRequestIf(400 == status);
    unauthorizedIf(401 == status);
    forbiddenIf(403 == status);
    notFoundIf(404 == status);
    conflictIf(409 == status);
    // none of the above?
    throw ActServerError.of(status);
} 
```

### <a name="exception-mapping"></a>Automatic map Java Exception to Response

Got exception not handled? ActFramework map them to response automatically!

1. `IllegalArgumentException` -> 400 Bad Request
1. `IndexOutOfBoundsException` -> 400 Bad Request
1. `IllegalStateException` -> 409 Conflict
1. `UnsupportedOperationException` -> 501 Not Implemented
1. Other uncaught exception -> 500 Internal Error

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
1. It allows the flexibility of content-negotiation

### <a name="render-template"></a>Render template

For classic MVC applications it is always needed to render response via templating. There are three ways to render through templating.

1. Implicit template rendering

    For any action handler, if the corresponding template is defined, the template will always be called to render the response
    
    If an action handler has return value, the value will be passed to the template by variable named `result`
    
1. Explicit `renderTemplate` call

    ```java
    @GetAction("/order/editForm")
    public Result orderEditForm(String orderId) {
        Order order = orderService.findById(orderId);
        boolean hasWritePermission = ...;
        return renderTemplate(order, hasWritePermission);
    }
    ```
    The above code will call the template (location by convention) with the parameters named `order` and `hasWritePermission`
    
1. Specify the template path

    ```java
    @GetAction("/order/editForm")
    public Result orderEditForm(String orderId) {
        Order order = orderService.findById(orderId);
        boolean hasWritePermission = ...;
        return renderTemplate("/myTemplateRoot/orderForm.html", order, hasWritePermission);
    }
    ```
    As shown above, when the first parameter passed to `renderTemplate` is a String literal, not String variable, it will be treated as template path, instead of render argument
    

### <a name="render-binary"></a>Render binary data

1. Render binary as stream embedded in browser (e.g. a PDF or image):

    ```java
    @GetAction("/user/{userId}/avatar")
    public Result getAvatar(String userId) {
        User user = userDao.findById(userId);
        return binary(user.getAvatarFile());
    }
    ```
    
2. Render binary as a download file

    ```java
    @GetAction("/invoice/{id}/photoCopy")
    public Result downloadInvoicePhotoCopy(String id) {
        Invoice invoice = dao.findById(id);
        return download(invoice.getPhoto());
    }
    ```

## <a name="content-negotiation"></a>Content awareness

ActFramework detects the request's `accept` header and renders content accordingly

```java
@GetAction("/person/{id}")
public Person getPerson(String id) {
    return dao.findById(id);
}
```

With the action handler code shown above, if the request's `Accept` header is "application/json", the response will be something like:

```
{
  "firstName": "John",
  "lastName": "Smith"
}
```

While if the header is `text/html` or `text/plain`, the response will be the plain String:

```
John Smith
```

You can define template files with different suffixes if you need to tweak the default render result:

`getPerson.html`

```
@args Person result
<div>
  <span class="label">First name</span><span>@result.getFirstName()</span>
</div>
<div>
  <span class="label">Last name</span><span>@result.getLastName()</span>
</div>
```

`getPerson.json`

```
@args Person result
{
    "firstName": "@result.getFirstName()",
    "lastName: "@result.getLastName()"
}
```

ActFramework will pickup the proper template file based on the `Accept` header

## Session and Flash

If you have to keep data across multiple HTTP requests, you can save them in the Session or the Flash scope. Data stored in the Session are available during the whole user session, and data stored in the Flash scope are available to the next request only.

It’s important to understand that Session and Flash data are not stored in the server but are added to each subsequent HTTP request, using the Cookie mechanism. So the data size is very limited (up to 4 KB) and you can only store String values.

Of course, cookies are signed with a secret key so the client can’t modify the cookie data (or it will be invalidated). The ActFramework session is not aimed to be used as a cache. If you need to cache some data related to a specific session, you can use the `Session.cache()` API to keep them related to a specific user session.

Example:

```java
@GetAction
public void index(H.Session session, Message.Dao dao) {
    List<String> messages = session.cached("messages");
    if (null == messages) {
        // Cache miss
        messages = dao.findByUser(me);
        session.cacheFor30Min("messages", messages);
    }
    render(messages);
}
```

The session expires when you close your web browser, unless you have enabled [session.persistent](configuration#session_persistent)

The cache has different semantics to the classic Servlet HTTP session object. You can’t assume that these objects will be always in the cache. So it forces you to handle the cache miss cases, and keeps your application fully stateless.

## Wrap up

In the section we have explained/demonstrated:

1. The concept of `Controller` and `Action handler` in ActFramework
1. How to write a simple controller
1. How to handle request parameters including binding request parameters to POJO instance
1. How to respond request with different status code
1. How to return data
1. How to find/specify template to render the response
1. How to get or download binary data
1. How `Accept` header impact ActFramework's behavior 
1. How to use `Session` and `Flash` Object

[Back to index](index.md)
