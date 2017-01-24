# 控制器和响应方法

ActFramework在定义控制器和响应方法上提供了足够的机制允许你以不同的方式来定义逻辑

## <a name="term"></a>概念

1. **控制器**. 控制器是指一个包括了若干请求响应器的Java类
    
    **注意** ActFramework并不要求控制器集成某个特定的类，也不要求控制器加上某个特定注解

1. **响应器** 指某个方法提供了一定的逻辑代码响应发送到特定路径的请求。简单的说如果在应用运行的时候有路由条目配置到某个方法，该方法即为响应器。

    响应器可以是静态方法也可以是虚方法

## <a name="a-simple-controller"></a>一个简单的控制器


```java
package com.mycom.myprj.controller;

public class MyController {
    public void home() {}
}
``` 

如果应用程序的路由表(/resources/routes)定义了一下条目：

```
GET / com.mycom.myprj.controller.MyController.home
```

以上代码就成了一个简单的控制器，在其中定义了一个响应器`home`

除了路由表，你也可以通过注解来加载路由：

```java
@GetAction("/")
public void home() {}
```

如果没有定义相应的模板，该响应器对发送到`/`的请求只会送回200 Okay的代码。

如果在[特定的地方](templating.md#location)定义了模板代码，ActFramework会调用模板并生成响应结果。

**小贴士** 尽管控制器不需要继承任何类，ActFramework推荐你的控制器继承`act.controller.Controll.Util`类，这样你可以在你的控制器中方便的使用各种工具方法。当你的控制器已经继承了其他类的时候，你可以使用`import static`来实现相同的功能：

1. 继承 `act.controller.Controller.Util`:

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

**注意** 本页下面的代码例子都假设控制器继承了`Controller.Util`类

 
## <a name="parameter"></a>获得请求参数

ActFramework从一下来源自动填充响应器参数：

1. URL路径参数
1. 查询参数
1. 表单参数

```java
@PutAction("/customer/{customerId}/order/{orderId}")
public void updateOrderAmount(String customerId, String orderId, int amount) {
    ...
}
```

如上例所示URL路径变量`customerId`和`orderId`被自动填充为响应器参数，参数`amount`则来自查询参数或者表单参数

### <a name="binding"></a>POJO绑定

ActFramework可以将复杂的表单变量绑定到域模型对象（POJO实例）. 假设你有如下类:

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

你的订单表单如下:

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

你可以在响应器中直接声明`Order`类型变量:

```java
@PostAction("/customer/{customerId}/order")
public void createOrder(String customerId, Order order) {
    order.setCustomerId(customerId);
    dao.save(order);
}
```

### <a name="json-param"></a>JSON内容绑定

上面的`createOrder`响应器也可以从类似下面的JSON内容绑定: 

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

**Note** ActFramework暂不支持从XML内容的绑定 

### <a name="file"></a>获取上传文件

假设你的文件上传表单如下:

```html
<form method="POST" enctype="multipart/form-data" action="/upload">
    Please specify file to upload: <input type="file" name="myfile"><br />
    <input type="submit" value="submit">
</form>
```

你可以直接在你的响应器中申明`File`类型参数：

```java
public void handleUpload(File myfile) {
    ...
}
```


## <a name="response"></a>发回响应

ActFramework提供多种不同的方法让开发人员指定响应内容，每种方式都简单易用。

### <a name="implicity-200"></a>自动返回200 Okay

当响应器方法没有返回类型，也没有抛出异常ActFramework自动发回代码为`200 Okay`的空响应。如果有[相应的模板定义](templating.md#location)，则根据模板生成返回内容。自动返回可以让一些PUT和POST的响应器非常简练：

```java
@PostAction("/order")
public void createOrder(Order order) {
    orderService.save(order);
}
```

### <a name="explicity-200"></a>程序中制定返回200 Okay

对于有轻微强迫症的猿们，一定要通过程序显式返回200 Okay才舒服，ActFramework提供两种方式：

1. 返回`org.osgl.mvc.result.Result`

    ```java
    @PostAction("/order")
    public Result createOrder(Order order) {
        orderService.save(order);
        return ok();
        // 或者 return new Ok();
    }
    ```

1. 抛出`org.osgl.mvc.result.Result`

    ```java
    @PostAction("/order")
    public void createOrder(Order order) {
        orderService.save(order);
        throw ok();
        // or throw new Ok();
    }
    ```

你甚至可以将`Result`隐式抛出:

    ```java
    @PostAction("/order")
    public void createOrder(Order order) {
        orderService.save(order);
        ok();
    }
    ```

**注意** ActFramework会对控制器的响应方法做字节码增强，当某一条语句返回`Result`类型，但没有返回上级调用，框架会自动将Result作为异常抛出，这就是上例可以简单写一句`ok()`的原因所在

### <a name="return-404"></a>返回404 Not Found

对于http服务来讲，当请求的资源无法找到的时候服务器应该返回`404 NotFound`响应。ActFramework程序可以使用如下方式返回`404`错误：

```java
@GetAction("/order/{orderId}")
public Order getOrder(String orderId) {
    Order order = dao.findById(orderId);
    if (null == order) {
        throw new NotFound();
    }
}
```

对上述代码的一种更为简洁的表述为：

```java
@GetAction("/order/{orderId}")
public Order getOrder(String orderId) {
    Order order = dao.findById(orderId);
    notFoundIfNull(order);
}
```

而极简方式则为：

```java
@GetAction("/order/{orderId}")
public Order getOrder(String orderId) {
    return dao.findById(orderId);
}
```

你没有看错，没有任何语句检查返回订单对象是否为空。ActFramework将自动检查，如果响应器返回空值，且方法申明有返回类型，则自动返回`404`错误

### <a name="return-400"></a>返回其他错误

下面的代码演示了如何返回其他错误类型：

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

### <a name="exception-mapping"></a>从Java异常自动映射为HTTP错误响应

你的代码有异常抛出嘛? ActFramework会自动将它们映射为错误响应：

1. `IllegalArgumentException` -> 400 Bad Request
1. `IndexOutOfBoundsException` -> 400 Bad Request
1. `IllegalStateException` -> 409 Conflict
1. `UnsupportedOperationException` -> 501 Not Implemented
1. Other uncaught exception -> 500 Internal Error

### <a name="return-data"></a>返回数据

ActFramework允许返回任何类型的数据，并根据上下文情况判断最终返回格式。当请求的`Accept`http头设置为`application/json`的时候下面两组代码的效果是完全相同的:

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

推荐使用第一种方式，原因在于：

1. 更加简洁
1. 当请求要求不同的返回格式的时候，ActFramework能够满足要求

### <a name="render-template"></a>使用模板

传统的MVC应用几乎都会设计模板。ActFramework支持下面三种方式来调用模板:

1. 隐式模板调用

    对于任何响应器，如果定义了相应的模板文件，则总是启用模板文件来生成响应。
    
    如果响应器返回某个对象，该对象可以在模板中使用`result`参数来引用
    
1. 显式模板调用

    ```java
    @GetAction("/order/editForm")
    public Result orderEditForm(String orderId) {
        Order order = orderService.findById(orderId);
        boolean hasWritePermission = ...;
        return render(order, hasWritePermission);
    }
    ```
    以上代码明确调用模板来生成响应结果。在调用模板的时候传进两个参数`order`和`hasWritePermission`，这两个参数可以在模板中被直接引用
    
1. 显式调用模板并制定路径

    ```java
    @GetAction("/order/editForm")
    public Result orderEditForm(String orderId) {
        Order order = orderService.findById(orderId);
        boolean hasWritePermission = ...;
        return renderTemplate("/myTemplateRoot/orderForm.html", order, hasWritePermission);
    }
    ```
    
    在上例中传递给`renderTemplate`的第一个参数是一个字串量(String literal)，而不是一个变量。在这种情况下，ActFramework将其作为模板路径处理，其他的参数则继续作为模板参数处理。
    

### <a name="render-binary"></a>发回二进制数据

1. 发回嵌入二进制流（例如图片或者嵌入式PDF）

    ```java
    @GetAction("/user/{userId}/avatar")
    public Result getAvatar(String userId) {
        User user = userDao.findById(userId);
        return binary(user.getAvatarFile());
    }
    ```
    
2. 发回下载文件

    ```java
    @GetAction("/invoice/{id}/photoCopy")
    public Result downloadInvoicePhotoCopy(String id) {
        Invoice invoice = dao.findById(id);
        return download(invoice.getPhoto());
    }
    ```

## <a name="content-negotiation"></a>内容格式

ActFramework检测请求的`Accept`头并根据其设定生成不同的响应内容

```java
@GetAction("/person/{id}")
public Person getPerson(String id) {
    return dao.findById(id);
}
```

对于上例代码，当`Accept`头设置为"application/json"的时候, 响应是JSON体:

```json
{
  "firstName": "John",
  "lastName": "Smith"
}
```

当设置为`text/html`或`text/plain`的时候, 响应将调用`Person.toString()`方法，生成下面的内容

```
John Smith
```

你甚至可以为响应器定义多个不同的后缀名的模板文件。

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

ActFramework根据`Accept`头的内容来选择适合的模板文件

## 会话和快闪对象

TBD...

## 总结

本章讲述了一下概念：

1. 控制器`Controller`和响应器`Action handler`的概念
1. 如何写一个简单的控制器
1. 如何获取请求参数以及POJO绑定
1. 如何发回不同的响应代码
1. 如何返回数据
1. 如何隐式或显式的指定响应模板
1. 如何返回二进制流或下载文档
1. `Accept`头对ActFramework行为的影响 
1. 如何使用回话和快闪对象

[返回目录](index.md)
