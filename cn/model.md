# 域模型和数据库访问

ActFramework不限制应用程序使用数据库访问机制，同时也提供了推荐的数据访问框架:

1. 管理数据访问的配置
1. 一个简单易用的数据访问对象接口
1. 多数据库访问支持

ActFramework目前支持使用[Morphia](http://mongodb.github.io/morphia/)访问MongoDB,  以及[EBean](http://ebean-orm.github.io/)，JPA等方式访问SQL数据库.

**Act支持的ORM框架有：**

面向Sql数据库的

1. [Ebean](ebean.md)
1. [Hibernate](hibernate.md)
1. Eclipse-link
1. BeetlSQL

面向NoSQL数据库的

1.[Morphia](morphia.md)

**开发计划**
1. Act-Redis
1. Act-Mybatis

参考:

1. [开发多数据源应用](multi_db.md)
 

[回到目录](index.md)