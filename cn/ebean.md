# 使用Ebean访问SQL Database

## 安装

在你的`pom.xml`文件中加上一下依赖：

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-ebean</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

根据你的数据库类型，你也需要加入相应的JDBC访问包的依赖。比如：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.178</version>
</dependency>
```

## 配置

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

## 域模型

下面创建一个简单的域模型，该模型有三个字段：

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

**注意** 和Morphia访问层不同，Ebean访问层目前暂时不提供类似`MorphiaModel`的父类.

## 数据访问对象和CRUD

一下代码演示如何使用EbeanDao来进行CRUD操作:

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

## 查询操作

```java
// find by last name
Iterable<Contact> contacts = dao.findBy("firstName", firstName);

// find by both first and last name
Iterable<Product> contacts = dao.findBy("firstName,lastName", firstName, lastName);

// find by firstName using regular expression
Iterable<Product> contacts = dao.findBy("firstName", Pattern.compile(firstName));
```

## 扩展`EbeanDao`

TBD

## 使用扩展的DAO类

假如你定义了扩展的DAO，你可以直接使用依赖注入来获取其实例:

```java
//private EbeanDao<Contact> dao = $.cast(app.dbServiceManager().dao(Contact.class));
private Contact.Dao dao = $.cast(app.dbServiceManager().dao(Contact.class));
```

[返回目录](index.md)