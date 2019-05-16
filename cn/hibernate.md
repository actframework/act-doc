# 使用JPA-Hibernate访问SQL Database 
version 1.5.7
## 安装

在你的`pom.xml`文件中加上以下依赖：

```xml
<dependency>
    <groupId>org.actframework</groupId>
    <artifactId>act-hibernate</artifactId>
    <version>1.5.7</version>
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
你还需要引入一个数据库连接池，根据自身条件在以下任选其一：

```xml
<dependency>
      <groupId>com.zaxxer</groupId>
      <artifactId>HikariCP-java7</artifactId>
      <version>2.4.13</version>
</dependency>

<dependency>
      <groupId>com.zaxxer</groupId>
      <artifactId>HikariCP</artifactId>
      <version>2.7.9</version>
</dependency>

<dependency>
      <groupId>com.alibaba</groupId>
      <artifactId>druid</artifactId>
      <version>1.1.16</version>
</dependency>
```

## 配置

```
# If you have only one DBPlugin in your class path, then
# you do not need to specify the db.impl configuration
db.impl=act.db.hibernate.HibernatePlugin
# database driver default to org.h2.Driver
db.driver=...
# database Url default to jdbc:h2:mem:tests
db.url=...
# username default is empty
db.username=...
# password default is empty
db.password=...
```

## 域模型

下面创建一个简单的域模型，该模型有三个字段：

1. `id`
1. `password`
1. `phone`
1. `email`

```java
package com.mycom.myprj;

import act.util.SimpleBean;
import javax.persistence.*;

@Entity
@Table(name = "user")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String password;
    @Column
    private String email;
    @Column
    private String phone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
```

## 数据库访问层

Dao 只需要很简单的 继承自 act.db.jpa.JPADao 就行了。

```java
package com.mycom.myprj;

import act.db.jpa.JPADao;
import com.mycom.myprj.User;

public class UserDao extends JPADao<Integer,User> {

}
```
