# Multi DB Application

ActFramework provides support to multiple data sources in your application.

## Configuration

```
db.instances=db1,db2

# db1 configurations
db.db1.impl=act.db.morphia.MorphiaPlugin
db.db1.uri=mongodb://localhost/test

db.db2.impl=act.db.ebean.EbeanPlugin
db.db2.driver=org.h2.Driver
db.db2.url=jdbc:h2:mem:test
```

As per above configuration, two database sources will be configured:

1. db1, also the default datasource, is a mongodb connection to `localhost/test`
1. db2, is a ebean connection using h2 jdbc driver connecting to in memory databse named `test`

## Specify database in Model class

The `Blog` model will use the default db, which is `db1`

```java
package com.mycom.myprj;

import org.mongodb.morphia.annotations.Entity;
import act.db.morphia.MorphiaModel;

@Entity("blog")
public class Blog extends MorphiaModel<Blog> {
    ...
}
```
The `Account` model will use the `db2` data source:

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

[Back to index](index.md)