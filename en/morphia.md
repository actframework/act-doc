# Access MongoDB with Morphia

## Setup

In order to use Morphia you need to add the following dependencies in your `pom.xml` file:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-morphia</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

## Model

Let's create a simple Product model with two properties:

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

## DAO and CRUD

Now that the model has been defined, let's take a look at how to use built-in DAO to do CRUD operations.

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

## Search

Act provide a set of search methods in `Dao` interface:

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

## Extend `MorphiaDao`

Sometime it is good to extend the `MorphiaDao` class and create a dedicated DAO class for a certain type to build some domain logic, in which case you extend's the DAO's concept to business service.

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

## Using extended DAO class

Once you have extended the DAO, you can use it following the same way as shown above, just change the type as shown below:

```java
//private MorphiaDao<Product> dao = Product.dao();
private Product.Dao dao = Product.dao();
```

So ActFramework detects your implementation of the DAO and will use that class instead of the standard one.