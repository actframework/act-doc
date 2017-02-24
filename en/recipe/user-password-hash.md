# The mechanism of store and persist user password


It details the principle and practice to protect user's password in [this article](https://crackstation.net/hashing-security.htm). It is very important for application developer to understand these principle and practices. However it doesn't make sense for every developer to implement the mechanism in every project. Instead framework should provide enough support for that.

In this recipe we will describe how to use ActFramework's built-in support on secure user's password. 

## Demo code

```java
public class User {
    private String email;
    // Store password hash instead of plain text
    private String passhash;
    
    /**
     * Use ACT's crypto utility to generate password hash
     * @param password the password text
     */
    public void setPassword(String password) {
        this.passhash = Act.crypto().passwordHash(password);
    }

    ...

    public static class Dao extends EbeanDao<User> {
        ...
        /**
         * Very password: find out user with emai, and then verify the password 
         * against the stored password hash
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

## Algorithm

ActFramework utilize the [well known](http://security.stackexchange.com/questions/4781/do-any-security-experts-recommend-bcrypt-for-password-storage) [bcrypt](https://en.wikipedia.org/wiki/Bcrypt) to calculate/verify password hash.

## Questions

### 1. Where is my salt?

[Bcrypt generate salt randomly and store the salt with calculated hash together](http://stackoverflow.com/questions/6832445/how-can-bcrypt-have-built-in-salts)

### 2. Can I use generated hash value to look up user in database?

Isn't much simpler if we update the `authenticate` method show above with the following implementation?

```java
public final User authenticate(String email, String password) {
    String hash = Act.crypto().passwordHash(password);
    return findOneBy("email, passhash", email.toLowerCase(), hash);
}
``` 

The answer is no, because Bcrypt generate random salt of different hash every time even if you pass the same password. It has to use `email` to fetch the user and verify the password and hash explicitly using `Act.crypto().verifyPassword(String, String)` API.

### 3. Is there any prevention for [remot timing attack](https://crypto.stanford.edu/~dabo/papers/ssl-timing.pdf)


Yes, Bcrypt's [verification function](https://github.com/jeremyh/jBCrypt/blob/master/src/main/java/org/mindrot/BCrypt.java#L774) applied slow equal logic to prevent remote timing attack.

## Links

* [Document home](../index.md)
* [ActFramework](http://actframework.org)
* [ActFramework@oschina](https://my.oschina.net/u/103410)](https://www.oschina.net/p/actframework)
* [ActFramework@github](https://github.com/actframework/actframework)
