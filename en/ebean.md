# Access SQL Database with Ebean

## Setup

In order to use Ebean you need to add the following dependencies in your `pom.xml` file:

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-ebean</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

You will also need to import your JDBC or database packages something like

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.178</version>
</dependency>
```

## Configuration

```
# If you have only one DBPlugin in your class path, then
# you do not need to specify the db.impl configuration
db.impl=act.db.ebean.EbeanPlugin
# database driver default to org.h2.Driver
db.driver=...
# database Url default to jdbc:h2:mem:tests
db.url=...
# username default is empty
db.username=...
# password default is empty
db.password=...
# If specified then app scan package will be used instead
db.db2.agentPackage=act.doc.sample.**
```

## Model

Let's create a simple Contact model with three properties:

1. `firstName`
1. `lastName`
1. `address`

```java
package com.mycom.myprj;

import act.db.DB;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity(name = "ctct")
public class Contact {
    @Id
    private Long id;
    private String fn;
    private String ln;
    private String addr;

    public long getId() {
        return null == id ? -1 : id;
    }

    public String getFirstName() {
        return fn;
    }

    public void setFirstName(String fn) {
        this.fn = fn;
    }

    public String getLastName() {
        return ln;
    }

    public void setLastName(String ln) {
        this.ln = ln;
    }

    public String getAddress() {
        return addr;
    }

    public void setAddress(String addr) {
        this.addr = addr;
    }
}
```

**Note** unlike Morphia which allows you to extend your Model class to `MorphiaModel`, Ebean plugin does not support that at the moment.

## DAO and CRUD

Now that the model has been defined, let's take a look at how to use built-in DAO to do CRUD operations.

```java
package com.mycom.myprj;

import act.app.App;
import act.controller.Controller;
import act.db.ebean.EbeanDao;
import org.osgl.$;
import org.osgl.mvc.annotation.DeleteAction;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.mvc.annotation.PostAction;
import org.osgl.mvc.annotation.PutAction;

import javax.inject.Inject;

@Controller("/ctct")
public class ContactController extends Controller.Util {
    
    private EbeanDao<Long, Contact> dao;

    @Inject
    public ContactController(EbeanDao<Long, Contact> dao) {
        this.dao = dao;
    }

    @GetAction
    public Iterable<Contact> list() {
        return dao.findAll();
    }

    @PostAction
    public void create(Contact ctct) {
        dao.save(ctct);
    }

    @GetAction("/{id}")
    public Contact show(long id) {
        return dao.findById(id);
    }

    @PutAction("/{id}/addr")
    public void updateAddress(long id, String value) {
        Contact ctct = dao.findById(id);
        notFoundIfNull(ctct);
        ctct.setAddress(value);
        dao.save(ctct);
    }

    @DeleteAction
    public void delete(long id) {
        dao.deleteById(id);
    }

}
```

**Note** the different between Morphia plugin and Ebean plugin on how to get the Dao instance.

## Search

Act provide a set of search methods in `Dao` interface:

```java
// find by last name
Iterable<Contact> contacts = dao.findBy("firstName", firstName);

// find by both first and last name
Iterable<Product> contacts = dao.findBy("firstName,lastName", firstName, lastName);

// find by firstName using regular expression
Iterable<Product> contacts = dao.findBy("firstName", Pattern.compile(firstName));
```

## Extend `EbeanDao`
TBD

## Using extended DAO class

Once you have extended the DAO, you can use it following the same way as shown above, just change the type as shown below:

```java
//private EbeanDao<Contact> dao = $.cast(app.dbServiceManager().dao(Contact.class));
private Contact.Dao dao = $.cast(app.dbServiceManager().dao(Contact.class));
```

So ActFramework detects your implementation of the DAO and will use that class instead of the standard one.

[Back to index](index.md)