# 域模型和数据库访问

ActFramework不限制应用程序使用数据库访问机制，同时也提供了推荐的数据访问框架:

1. 管理数据访问的配置
1. 一个简单易用的数据访问对象接口
1. 多数据库访问支持

ActFramework目前支持使用[Morphia](http://mongodb.github.io/morphia/)访问MongoDB,  以及[EBean](http://ebean-orm.github.io/)访问SQL数据库.

**注意** JPA现在Act数据访问框架下暂不支持。当然如果你自己管理配置和初始化，依然可以自由使用基于JPA的方案，如Hibernate等

参考:

1. [使用Morphia访问MongoDB数据库](morphia.md)
1. [使用EBean访问SQL数据库](ebean.md)
1. [开发多数据源应用](multi_db.md)
 
[回到目录](index.md)