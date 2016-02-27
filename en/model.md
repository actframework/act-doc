# Model and Database Access

The application developer can choose whatever model and database access technology they want to use in ActFramework. However Act provides a recommended set of Model/DAO mechanism that help developer with:

1. DB configuration
1. A simple and easy to use DAO interface
1. Multi DB access support

At the moment ACT support accessing MongoDB with [Morphia](http://mongodb.github.io/morphia/) and SQL database with [EBean](http://ebean-orm.github.io/).

**Note** JPA is not supported within ActFramework Model framework at the moment. However you are free to use JPA/Hibernate give you manage all the configurations and initializations

Refer to:

1. [Access MongoDB with Morphia](morphia.md)
1. [Access SQL database with EBean](ebean.md)
 
[Back to index](index.md)