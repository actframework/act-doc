# 如何在Act中控制JSON返回对象的字段

本文讲述如何在Act应用中控制返回JSON对象的字段。不同的场景返回同样对象的时候可能会要求返回不同的对象字段。
最简单的例子是基本上所有返回`User`对象都要求去掉password字段。在这篇文章中我们讨论如何在Act应用中实现对JSON返回的控制。

首先创建一个`Article`类，以及对该资源的RESTful服务接口：

```java
@Entity("article")
public class Article extends MorphiaAdaptiveRecord<Article> {

    @Controller("article")
    public static class Service extends MorphiaDao<Article> {

        @GetAction
        public Iterable<Article> list() {
            return findAll();
        }

        @GetAction("{id}")
        public Article show(String id) {
            return findById(id);
        }

        @PostAction
        public Article create(Article article) {
            return save(article);
        }
        ...
    }

}
```

这里我们看到可以通过 `POST /article`向服务提交article数据。假设我提交的数据是：

```json
{
	"title": "How to control JSON view in Actframework",
	"content": "BlahBlah",
	"author": "Gelin Luo",
	"language": "Java",
	"framework": "Actframework",
	"tags" : [
		{"name": "java"},
		{"name": "mvc"},
		{"name": "json"}
	]
}
```

我可以得到类似下面的返回:

```json
{
  "id": "58a6409ab6c6fe2138b67f10",
  "_created": "17/02/2017 11:15:22 AM",
  "content": "BlahBlah",
  "v": 1,
  "language": "Java",
  "author": "Gelin Luo",
  "title": "How to control JSON view in Actframework",
  "_modified": "17/02/2017 11:15:22 AM",
  "framework": "Actframework",
  "tags": [
    {
      "name": "java"
    },
    {
      "name": "mvc"
    },
    {
      "name": "json"
    }
  ]
}
```

当我发出`GET /article`请求时，`Article.Service.list()`方法会响应并返回所有的article列表：

```json
[
  {
    "id": "58a6409ab6c6fe2138b67f10",
    "_created": "17/02/2017 11:15:22 AM",
    "content": "BlahBlah",
    "v": 1,
    "language": "Java",
    "author": "Gelin Luo",
    "title": "How to control JSON view in Actframework",
    "_modified": "17/02/2017 11:15:22 AM",
    "framework": "Actframework",
    "tags": [
      {
        "name": "java"
      },
      {
        "name": "mvc"
      },
      {
        "name": "json"
      }
    ]
  }
]
```

那如果我想控制返回列表的数据，让每项只返回`author`和`title`，我可以在`list()`方法上面添加注解`PropertySpec`：

```java
@GetAction
@act.util.PropertySpec("author,title")
public Iterable<Article> list() {
    return findAll();
}
```

然后再发出`GET /article`请求，就可以得到下面的响应了：

```json
[
  {
    "author": "Gelin Luo",
    "title": "How to control JSON view in Actframework"
  }
]
```

我可以在`Article.Service.show(String)`方法上采用类似的方法来定义需要返回的字段。有人提到过如果想让前端向后端在请求中传递需要的字段该怎么办，下面是Actframework提供的方法：

将`show(String)`方法做一点改动

从

```java
@GetAction("{id}")
public Article show(String id) {
    return findById(id);
}
```

变为

```java
@GetAction("{id}")
public Article show(String id, String fields) {
    PropertySpec.current.set(fields);
    return findById(id);
}
```

然后就可以从前端在请求中加载`fields`参数了：

```
GET /article/58a6409ab6c6fe2138b67f10?fields=-tags,-content,-_created
```

上面的请求表示从返回JSON结果中去掉`tags`, `content`,和`_created`三个字段

返回结果将会是：

```json
{
  "id": "58a6409ab6c6fe2138b67f10",
  "v": 1,
  "language": "Java",
  "author": "Gelin Luo",
  "title": "How to control JSON view in Actframework",
  "_modified": "17/02/2017 11:15:22 AM",
  "framework": "Actframework"
}
```

该博客的源码在码云上：

https://git.oschina.net/greenlaw110/blog_json_view_control 
