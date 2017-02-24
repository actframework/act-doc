# ActFramework中存储与验证用户密码的机制与应用

@oschina的[这篇博客](http://www.oschina.net/news/49852/salted-password-hash)详细讲述了保护密码的机制. 作为应用程序开发者理解这些原理是非常重要的, 但是没有理由在每个项目中依据文中所述去实现自己的保护机制, 框架应该在这方面做出足够的支持.

ActFramework提供简单有效的API来帮助用户处理安全性问题, 其中包括了密码保护与验证. 下面的代码演示如何在应用中使用框架提供的机制:

## 代码演示

```java
public class User {
    private String email;
    // 保存password hash而不是明文
    private String passhash;
    
    /**
     * 使用`act.Act.crypto().passwordHash(String)`来生成password hash
     * @param password the password text
     */
    public void setPassword(String password) {
        this.passhash = Act.crypto().passwordHash(password);
    }

    ...

    public static class Dao extends EbeanDao<User> {
        ...
        /**
         * 验证用户的方法: 使用email搜索用户, 然后对password做匹配
         * @param email an email
         * @param password a password
         * @return a user if the email and password match, else null
         */
        public final User authenticate(String email, String password) {
            User user = findOneBy("email", email);
            if (null == user) {
                return null;
            }

            return  Act.crypto().verifyPassword(password, this.passhash) ? user : null;
        }
    }
}
```

## 算法

ActFramework采用[公认最好](http://security.stackexchange.com/questions/4781/do-any-security-experts-recommend-bcrypt-for-password-storage)的[bcrypt](https://en.wikipedia.org/wiki/Bcrypt)算法处理密码保存与验证

## 问题

### 1. 盐在哪里?

[Bcrypt采用随机生成盐并且将盐和hash存放在一起](http://stackoverflow.com/questions/6832445/how-can-bcrypt-have-built-in-salts)

### 2. authenticate方法为什么不生成hash然后再从数据库中寻找用户

上面的`public final User authenticate(String email, String password)`这样写不是更简单吗:

```java
public final User authenticate(String email, String password) {
    String hash = Act.crypto().passwordHash(password);
    return findOneBy("email, passhash", email.toLowerCase(), hash);
}
``` 

答案是不行. 因为Bcrypt每次都随机生成salt和hash值,所以即便用户使用相同的密码,两次调用`Act.crypto().passwordHash(password)`生成的值都是不一样的. 必须用`email`将`User`从数据库里面取出之后再使用`Act.crypto().verifyPassword(String, String)` API来比较

### 3. 有没有时间攻击防范

[JFinal](https://www.oschina.net/p/jfinal?fromerr=R7a8Jq4T)最新版提供了[slowEquals方法](https://www.oschina.net/question/1040143_2218633)用于防范[这篇博客](http://www.oschina.net/news/49852/salted-password-hash)中讲述的时间攻击问题. ActFramework有这方面的防范措施吗?

答案是必须的, 在`Act.crypto().verifyPassword(String)`API里面调用Bcrypt的[匹配函数](https://github.com/jeremyh/jBCrypt/blob/master/src/main/java/org/mindrot/BCrypt.java#L774), 用的就是JFinal实现的[slowEquals逻辑](https://github.com/jfinal/jfinal/blob/master/src/main/java/com/jfinal/kit/HashKit.java#L90). 值得一提的是和JFinal的实现相比, Bcrypt做了一点优化, 如果字符串长度不匹配的话, 直接短路返回`false`, 而不会继续slow equals处理.

## 链接

* [文档首页](../index.md)
* [ActFramework官网](http://actframework.org)
* [ActFramework[@开源中国](https://my.oschina.net/u/103410)](https://www.oschina.net/p/actframework)
* [ActFramework[@码云](https://my.oschina.net/buthink)](https://git.oschina.net/actframework/actframework)
