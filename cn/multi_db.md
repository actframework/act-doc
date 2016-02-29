# 多数据源应用

ActFramework支持在应用中使用多个数据源

## 配置

```
db.instances=db1,db2

# db1 configurations
db.db1.impl=act.db.morphia.MorphiaPlugin
db.db1.uri=mongodb://localhost/test

db.db2.impl=act.db.ebean.EbeanPlugin
db.db2.driver=org.h2.Driver
db.db2.url=jdbc:h2:mem:test
```

上面的配置指定了连个数据源

1. db1, 第一个也是默认数据源, 是一个mongodb连接，访问`localhost/test`
1. db2, 是一个ebean连接，使用h2 jdbc驱动连接到名字为`test`的内存数据库

## 在域模型类中指定数据源

下面的`Blog`模型没有特定指定数据源，因此会使用默认的数据源, 在以上配置中是`db1`

```java
package com.mycom.myprj;

import org.mongodb.morphia.annotations.Entity;
import act.db.morphia.MorphiaModel;

@Entity("blog")
public class Blog extends MorphiaModel<Blog> {
    ...
}
```
下面的`Account`模型通过`@DB`z注解指定了使用`db2`数据源:

```java
import act.db.DB;

import javax.persistence.Entity;
import javax.persistence.Id;

@DB("db2")
@Entity(name = "acc")
public class Account {
    ...
}
```

[返回目录](index.md)