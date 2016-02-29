# 使用Morphia访问MongoDB数据库

## 安装

要在ActFramework应用中使用Morphia，请在`pom.xml`文件中添加一下依赖:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-morphia</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

## 配置

简单的配置:

```
db.uri=mongodb://localhost/mydb
```

**小贴士** 你甚至不需要任何配置. ActFramework会自动连接到本地MongoDB服务器的test数据库

稍微复杂一点的配置:

```
db.url=mongodb://<username>:<password>@<host1>:<port1>,<host2>:<port2>,...,hostN:portN/dbname?replicaSet=...&connectTimeoutMS=...
```


## 域模型

下面创建一个简单的域模型，该模型有两个字段：

1. `name`
1. `price`

```java
package com.mycom.myprj;

import org.mongodb.morphia.annotations.Entity;
import act.db.morphia.MorphiaModel;

@Entity("prod")
public class Product extends MorphiaModel<Product> {
    private String name;
    private int price;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getPrice() {
        return price;
    }
    
    public void setPrice(int price) {
        this.price = price;
    }
}
```

## 数据访问对象和CRUD

一下代码演示如何使用MorphiaDao来进行CRUD操作:

```java
package com.mycom.myprj;

import act.controller.Controller;
import act.db.morphia.MorphiaDao;
import org.bson.types.ObjectId;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.mvc.annotation.PostAction;
import org.osgl.mvc.annotation.PutAction;


@Controller("/prod")
public class ProductController extends Controller.Util {

    private MorphiaDao<Product> dao = Product.dao();

    @GetAction
    public Iterable<Product> list() {
        return dao.findAll();
    }

    @GetAction("/{id}")
    public Product show(String id) {
        return dao.findById(new ObjectId(id));
    }

    @PostAction
    public void create(Product product) {
        dao.save(product);
    }

    @PutAction("/{id}/name")
    public void update(String id, String name) {
        Product product = dao.findById(new ObjectId(id));
        notFoundIfNull(product);
        product.setName(name);
        dao.save(product);
    }

    @DeleteAction
    public void delete(String id) {
        dao.deleteById(new ObjectId(id));
    }
}
```

## 查询操作

```java
// find by name
Iterable<Product> products = dao.findBy("name", name);

// find all that price is less than 10000
Iterable<Product> products = dao.findBy("price <", 100000);

// find by name and price
Iterable<Product> products = dao.findBy("name, price <", name, 100000);

// find by name using regular expression
Iterable<Product> products = dao.findBy("name", Pattern.compile("laptop"));
```

## 使用扩展的DAO类

你可以根据需要扩展`MorphiaDao`类，并加入业务逻辑

```java
@Entity("prod")
public class Product extends MorphiaModel<Product> {
    private String name;
    private int price;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getPrice() {
        return price;
    }
    
    public void setPrice(int price) {
        this.price = price;
    }
    
    public static class Dao extends MorphiaDao<Product> {
        
        public static final int LOW_PRICE = 10000;
        public static final int HIGH_PRICE = 999900;
        
        public Dao() {
            super(Product.class);
        }
        
        public Iterable findLowPriceProducts() {
            return findBy("price <", LOW_PRICE);
        }
        
        public Iterable findHighPriceProducts() {
            return findBy("price >", HIGH_PRICE);
        }
    }
}

```

## 使用扩展的DAO类

假如你定义了扩展的DAO，你可以使用同样的接口来获取其实例:

```java
//private MorphiaDao<Product> dao = Product.dao();
private Product.Dao dao = Product.dao();
```

[返回目录](index.md)