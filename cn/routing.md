# 路由

ActFramework应用程序可以使用三种不同的方式来创建路由:

1. 在响应方法上标记相关注解
1. 通过`resources/routes.conf`路由表文件
1. 通过配置API

## 通过注解创建路由

下面的注解可以指定路由:

1. `org.osgl.mvc.annotation.Action`
1. `org.osgl.mvc.annotation.GetAction`
1. `org.osgl.mvc.annotation.PostAction`
1. `org.osgl.mvc.annotation.PutAction`
1. `org.osgl.mvc.annotation.DeleteAction`

示例代码：

```java
@GetAction("/profile/{id}")
public Profile getProfile(String id) {
    return dao.findById(id);
}

@PostAction("/profile")
public void createProfile (Profile profile) {
    dao.save(profile);
}

@PutAction("/profile/{id}/address")
public void updateAddress(String id, Address address) {
    Profile profile = dao.findById(id);
    notFoundIfNull(profile);
    profile.setAddress(address);
    profile.update(profile);
}

@DeleteAction("/profile/{id}")
public void deleteProfile(String id) {
    dao.deleteById(id);
}
```

**小贴士**: 当某个响应方法处理多种不同的HTTP方法请求时可以使用`@Action`注解：

```java
@Action("/", methods = {H.Method.GET, H.Method.POST})
public void home() {}
```

**小贴士**: 你可以通过注解将不同的请求路径映射到同一个响应方法上:

```java
@GetAction({"/profile/{id}", "/profile"})
public Profile getProfile(String id) {
    return dao.findById(id);
}
```

依据上例的配置`getProfile`可以处理下面两种请求:

1. `/profile/<profile_id>`
2. `/profile?id=<profile_id>`

## 路由表文件：`routes`

如果你更喜欢`PlayFramework`形式的路由表, 你可以在`/src/main/resources`目录下创建一个`routes`文件. 和上面注解路由相对应的`routes`文件内容如下所示(假设控制器的类名为`com.mycom.myprj.MyController`):

```
GET /profile/{id} com.mycom.myprj.MyController.getProfile
POST /profile com.mycom.myprj.MyController.createProfile
PUT /profile/{id}/address com.mycom.myprj.MyController.updateAddress
DELETE /profile/{id}
```

规则：路由表条目由下面三个部分组成：

```
(GET|POST|DELETE|PUT|*) <path> <handler>
----------------------- 
   HTTP请求方法
                        ------
                        请求路径
                               -----------
                               响应器规范
```

### 响应器指令

通常来讲响应器规范部分由控制器类名加上响应方法方法名组成. 不过你也可以使用响应器指令来定义不同的响应器：

```
GET /tmp externalfile:/tmp
GET /public file:/public
GET /3215430325 echo:some-code
GET /google redirect:http://google.com
```

ActFramework内置四种响应器指令

1. `echo`: `echo:`后面的字串会被发送回请求端.
1. `file`: 发送应用程序根目录一下的静态文件
1. `externalfile`: 发送任何指定的静态文件
1. `redirect`: 发送重定向响应

**注意**, `routes`文件中的条目可以覆盖由注解指定的路由

## 通过配置API构建路由

TBD

[返回目录](index.md)
