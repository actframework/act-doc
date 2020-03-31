# 第八章 测试

ActFramework 提供基于 YAML 脚本的自动化测试工具

## <a name="introduction"></a> 8.1 引述

通常开发采用的测试方式大多基于 JUnit, 典型如 [Spring 的 Web 测试](https://spring.io/guides/gs/testing-web/) 一文中提供的例子:

<a name="s8_1_a"></a> 控制器代码:

```java
// snippet s8.1a
@Controller
public class HomeController {

    @RequestMapping("/")
    public @ResponseBody String greeting() {
        return "Hello World";
    }

}
```

<a name="s8_1_b"></a> 测试代码:


```java
// snippet s8.1b
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void greetingShouldReturnDefaultMessage() throws Exception {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/",
                String.class)).contains("Hello World");
    }
}
```

从上面的例子可以看出:

1. Spring 基于 JUnit 的测试可以完成 RESTful 服务的系统功能性测试
2. 测试定义较为复杂, 即便简单的测试校验也需定义诸多元素来完成

下面让我们看看 ActFramework 中如何实现同样的测试.

<a name="s8_1_c"></a> 控制器代码:

```java
// snippet s8.1c
public class HomeController {

    @GetAction
    public String greeting() {
        return "Hello World";
    }

}
```

<a name="s8_1_d"></a> 测试代码(定义在 `/resources/test/scenarios.yml` 文件中):

```yaml
# snippet s8.1d
Scenario:
  interactions:
    - description: test greeting service
      request:
        get: /
        accept: text/plain
      response:
        text: Hello World
```

运行测试的办法有两种:

1. `mvn clean compile act:test` - 在命令行使用 `test` profile 启动应用并自动运行所有的测试用例, 如果全部通过则返回 `0`, 否则返回非零值
2. 采用 `dev` 模式启动应用之后, 在浏览器中访问 `/~/test` 地址可以运行所有的测试用例. 这种方式的好处在于可以修改代码之后立刻看到反馈结果

## <a name="fixtures"></a> 8.2 准备/清理测试数据

除非极为简单的情况, 测试都需要准备和清理测试数据. ActFramework test 提供了多种工具方便开发人员准备测试数据

### <a name="load-fixture-yaml"></a> 8.2.1 从 Fixture YAML 文件中加载

假设应用定义了如下 Model 类:


<a name="s8_2_1a"></a> Course 类

```java
// snippet s8.2.1a
package com.myproj.models;

@Entity("course")
public class Course implements SimpleBean {

    @Id
    @GeneratedValue
    public int id;

    @NotBlank
    public String name;

}
```

<a name="s8_2_1b"></a> User 类

```java
// snippet s8.2.1b
package com.myproj.models;

@Entity("user")
public class User implements SimpleBean {

    @Id
    @GeneratedValue
    public int id;

    public String name;

    public DateTime birthday;

    public List<Integer> courseIds;

}
```

开发人员可以在 `resources/test/fixtures` 目录中创建 Fixture YAML 文件, 例如 `init-data.yml`:

<a name="s8_2_1c">

```yaml
# snippet s8.2.1c
Course(math):
  id: 1
  name: Maths
Course(history):
  id: 2
  name: History
User(green):
  id: 1
  name: Green Luo
  birthday: 1919-01-01
  courses:
    - ref:math
com.myproj.models.User(black):
  id: 2
  name: Black Smith
  birthday: 1818-02-02
  courses:
    - ref:math
    - ref:history
```

上面文件中定义了 `math`, `history` 两门 `Course` 以及 `green`, `black` 两个 `User` 数据. 注意数据类可以给出整个 package, 例如 `com.myproj.models.User`, 也可以忽略, 前提是应用定义了配置 `test.model-packages=com.myproj.models`. 类名之后括弧里面是数据名字, 例如 `Course(math)`, 定义了名字为 `math` 的 Course 数据. 给出数据名字是为了便于后面的引用.

注意到文件中使用了 `ref:` 前缀来引用数据: `ref:math` - 表示将 Entity `math` 的 ID 放在这里. 注意, `ref:` 之后的数据必须已经在前面定义了才行.

除了 `ref:`, ActFramework 还支持 `embed:` 和 `password:` 两种前缀.

* `embed:x` - 将前面定义为 `x` 的数据整个嵌入到当前位置, 这要求当前数据 Model 类支持嵌入结构
* `password:1234` - 将 `1234` 用 Password Hash 运算之后放在当前位置

#### <a name="use-fixture-yaml-in-scenario"></a> 8.2.1.1 在测试 Scenario 中使用 fixture YAML 文件

如果某个测试 Scenario 需要加载上面的 Fixture, 应该使用 `fixtures` 列表:

```yaml
Scenario(One):
  fixtures:
    - init-data.yml
  ...
```

框架在运行 `One` Scenario 的时候就会自动加载 `init-data.yml` 文件中定义的数据了, 应用可以在 `/resources/test/fixtures` 目录中定义任意数量的 fixture YAML 文件, 并在任何测试 Scenario 文件中引用

### <a name="generate-test-data"></a> 8.2.2 自动生成测试数据

YAML 在测试数据数量有限的情况下是比较合适的. 如果应用需要大数量的随机测试数据, 可以在测试场景文件中使用 `generateTestData` 工具:

```yaml
Scenario(Prepare):
  generateTestData:
    - User
```

上面的脚本告诉框架对 `User` 类自动生成 100 条随机测试数据. 应用也可以自己定义测试数据的数量:

```yaml
Scenario(Prepare):
  generateTestData:
    User: 200
```

### <a name="load-fixture-job"></a> 8.2.3 应用自定义测试数据加载逻辑

如果应用有特殊的测试数据加载需求, 可以使用 `FixtureLoader` 注解自定义数据加载方式:

```java
@FixtureLoader("load-my-test-data")
public vod loadUsers(User.Dao userDao, Course.Dao courseDao) {
    // define the logic to load test data
}
```

在测试场景文件中可以直接调用上面的逻辑:

```yaml
Scenario(One):
  fixtures:
    - load-my-test-data
```

### <a name="clear-test-data"></a> 8.2.4 清除测试数据

当测试 Scenario 不依赖于其他 Scenario 的时候 ActFramework 总是会清除掉所有的数据存储. 这一点感觉比较危险, 但因为产品模式下 ActFramework 是不会运行自动测试的, 所以不会对线上系统造成任何危害. 但如果开发调试过程中有手工生成数据就需要小心处理测试. 最好的办法是创建一个特殊的配置用来运行调试自动化测试, 该配置可以定义单独的数据库连接.

对于某些数据特别是长期不变的配置数据, 如果测试不涉及数据的增删改操作, 可以使用 `NoFixture` 注解来告诉框架不要在测试过程中对此类数据进行清理操作:

```java
@Entity(mame = "city")
@NoFixture
public class City extends SimpleBean {
  public String name;
  ...
}
```

如上例所示, `City` 类上有 `NoFixture` 注解,因此在自动测试过程中 city 数据不会被清理.

## <a name="scenario-structure"></a> 8.3 测试场景文件结构

测试场景是 ActFramework 进行自动测试的核心数据. 测试场景定义在 `.yml` 文件中, ActFramework 从以下文件加载测试场景:

1. `resources/test/scenarios.yml`
2. `resources/test/scenarios` 目录下的任何 `.yml` 文件

测试场景文件结构如下:

![image](https://user-images.githubusercontent.com/216930/45152661-099ad700-b215-11e8-95c4-318cc3e7a3df.png)

由上图可知测试场景文件中可以定义多个测试场景, 每个测试场景中又可以定义多个交互, 而每个交互中则包含请求和响应的定义.

## <a name="request-spec"></a> 8.4 定义请求

请求定义指定请求方法, URL 和参数. 例如:

<a name="s8_4a"></a>

```yaml
# snippet s8.4a
request:
  get: /foo
  params:
    bar: 123
```

上面示例定义了一个 `GET /foo?bar=123` 的请求, 其中使用了 `params` 来指定 `bar=123` 的 GET 查询参数. 上面的定义也可以简写为:

<a name="s8_4b"></a>

```yaml
# snippet s8.4b
request:
  get: /foo?bar=123
```

下面是一个 POST 请求的定义示例:

<a name="s8_4c"></a>

```yaml
# snippet s8.4c
request:
  post: /users
  params:
    user.name: Thomas
    user.email: tom@x.com
```

也可以采用 JSON 方式来定义 POST 请求的参数:

<a name="s8_4d"></a>

```yaml
# snippet s8.4d
request:
  post: /users
  json:
    user:
      name: Thomas
      email: tom@x.com
```

### <a name="request-header"></a> 8.4.1 请求头

如果需要可以在请求定义中加入请求头的定义, 例如:

<a name="s8_4_1a"></a>

```yaml
# snippet s8.4.1a
request:
  headers:
    X-Token: 123
```

## <a name="response-spec"></a> 8.5 定义响应校验

在测试场景中使用 `response` 来定义响应校验, 例如:

<a name="s8_5a"></a>

```yaml
# snippet s8.5a
response:
  json:
    name: Thomas
    email: tom@x.com
```

### <a name="response-status"></a> 8.5.1 响应状态校验

在上面的例子中并没有明确定义响应状态, 但是 ActFramework 会检查返回响应的状态是否为成功, 状态值为 `2xx` 的响应被认为是成功响应, 除此之外的响应都会导致测试失败. 如果测试期望一个失败响应状态, 则需要明确定义状态, 例如:

<a name="s8_5_1a"></a>

```yaml
# snippet s8.5.1a
response:
  status: 404
```

### <a name="response-header"></a> 8.5.2 响应头校验

如果需要对响应头进行校验可以使用 `headers`, 例如:

<a name="s8_5_2a"></a>

```yaml
# snippet s8.5.2a
response:
  headers: 
    X-Token: 123
```

### <a name="response-body"></a> 8.5.3 响应内容校验

响应内容的校验相对比较复杂, 需要就下面几个概念分开来阐述:

* 值校验器
* 响应内容类型
* 缓存响应结果

#### <a name="verifier"></a> 8.5.3.1 值校验器

ActFramework 内置了以下值校验器:

* after: 检查日期是否在给定日期参数之后
* before: 检查日期是否在给定日期参数之前
* contains: 检查字串类型值是否包含给定字串
* containsIgnoreCase: 检查字串类型值是否包含给定字串(大小写不区分)
* ends: 检查字串类型值是否以给定字串结束
* eq: 检查值是否等于给定参数
* eqIgnoreCase: 检查字串类型值是否与给定字串相等 (忽略大小写差异)
* exists: 检查是否有值
* gt: 检查值是否大于给定参数
* gte: 检查值是否大于或等于给定参数
* lt: 检查值是否小于给定参数
* lte: 检查值是否小于或等于给定参数
* neq: 检查值是否不等于给定参数
* starts: 检查字串类型值是否以给定字串开头

对于任何值可以使用多个值校验器, 只有全部校验器通过之后才认为测试通过. 例如:

<a name="s8_5_3_1a"></a>

```yaml
# snippet s8.5.3.1a
response:
  json:
    value: 
      - exists: true
      - neq: 123
```

上面的例子要求 JSON 返回值的 `value` 字段存在且不等于数字 `123`. 下面的返回是不能通过测试的:

```
{"a": 123} // 没有 `value` 值
```

```
{"value": 123} // `value`值等于 123 了
```

**注意** 关于日期类型, 测试支持的日期格式有:

* `yyyy-MM-dd hh:mm:ss`
* `yyyy-MM-dd HH:mm:ss`
* `yyyy-MM-dd`

这里的日期格式并非应用输出的日期格式, 而是在测试场景文件中指定用于验证应用输出日期值的格式

下面是日期类型的验证示例:

<a name="s8_5_3_1b"></a>

```yaml
# snippet s8.5.3.1b
response:
  text:
    - after: 1997-05-11 # the returned date should be after date 11/May/1997
    - before: 2018-05-31 # the returned date should be before date 31/May/2018
```

#### <a name="response-type"></a> 8.5.3.2 响应内容类型

[例 s8.5a](#s8_5a) 中使用了 `json` 类型响应, 这是面向服务端口应用最常见的类型. 除了 `json` 之外, ActFramework 还支持另外两种类型响应:

* text
* html

下面分别介绍这三种内容类型的校验方法

#### <a name="text-response"></a> 8.5.3.3 text 类型内容校验

`text` 类型内容通常用于简单情况. 假如应用 Controller 代码为

<a name="s8_5_3_3a"></a>

```java
// snippet s8.5.3.3a
@GetAction("/hello")
public String sayHello() {
    return "Hello World";
}
```

对 `text` 类型内容的验证脚本为:

<a name="s8_5_3_3b"></a>

```yaml
# snippet s8.5.3.3b
Scenario:
  interactions:
    - description: verify /hello
      request:
        url: /hello
        accept: text/plain
      response:
        text: Hello World
```

也可以采用校验器方式来验证 text 内容:

<a name="s8_5_3_3c"></a>

```yaml
# snippet s8.5.3.3c
Scenario:
  interactions:
    - description: verify /hello
      request:
        url: /hello
        accept: text/plain
      response:
        text: 
          - eq: Hello World
          - eqIgnoreCase: hello world
          - starts: Hello
          - ends: World
          - contains: Wor
```

**注意** 上面的请求定义部分加入了 `accept: text/plain` 修饰, 是为了强制 [sayHello()](#s8_5_3_3a) 方法返回 `text/plain` 类型的内容.

#### <a name="json-response"></a> 8.5.3.4 JSON 类型内容校验

对于同样的 [sayHello()](#s8_5_3_3a) 方法, 如果采用 `accept: application/json` 方式请求, 应该获得 JSON 类型的响应内容:

<a name="s8_5_3_4a"></a>

```JSON
// snippet s8.5.3.4a
{
  "result": "Hello World"
}
```

这个时候的校验脚本为:

<a name="s8_5_3_4b"></a>

```yaml
# snippet s8.5.3.4b
Scenario:
  interactions:
    - description: verify /hello
      request:
        url: /hello
        accept: application/json
      response:
        json:
          result: Hello World 
```

或者使用校验器:

<a name="s8_5_3_4c"></a>

```yaml
# snippet s8.5.3.4c
Scenario:
  interactions:
    - description: verify /hello
      request:
        url: /hello
        accept: text/plain
      response:
        json:
          result: 
            - eq: Hello World
            - eqIgnoreCase: hello world
            - starts: Hello
            - ends: World
            - contains: Wor
```

##### <a name="json-response"></a> 8.5.3.4.1 JSON POJO 内容校验

下面是一个稍微复杂一点的 POJO JSON 内容的例子. 假设有下面的代码:

<a name="s8_5_3_4_1a"></a>

```java
// snippet s8.5.3.4.1a
@JsonView
@GetAction("/users/{user}")
public User getUser(@DbBind User user) {
  return user;
}
```

假定请求 `GET /users/1` 返回结果应该为:

<a name="s8_5_3_4_1b"></a>

```json
// s8.5.3.4.1b
{
  "firstName": "Jack",
  "lastName": "Smith",
  "email": "jacks@x.com",
  "address": {
    "unitNo": "4",
    "streetNo": "33-36",
    "street": "King St",
    "suburb": "Aliceville",
    "postCode": 3366
  }
}
```

下面是相应的测试脚本:

<a name="s8_5_3_4_1c"></a>

```yaml
# s8.5.3.4.1c
Scenario:
  interactions:
    - description: test GET /users/1
      request:
        get: /users/1
      response:
        json:
          firstName: Jack
          lastName: Smith
          email: jacks@x.com
          address:
            unitNo: 4
            streetNo: "33-36"
            street: King St
            suburb: Aliceville
            postCode: 3366
```

##### <a name="json-response"></a> 8.5.3.4.2 JSON 数组校验

JSON 数组的验证更加复杂一些. 假设请求 `/foo/bar` 返回如下 JSON 数组

<a name="s8_5_3_4_2a"></a>

```json
// s8.5.3.4.2a
[1, 2, 3, 4, 5]
```

对应的测试脚本为:

<a name="s8_5_3_4_2b"></a>

```yaml
# s8.5.3.4.2b
Scenario:
  interactions:
    - description: test GET /foo/bar
      request:
        get: /foo/bar
      response:
        json:
          size: 5 # there shall be 5 elements in the array
          0: 1
          1: 2
          2: 3
          3: 4
          4: 5
```

如果不需要完全匹配所有数组元素, 则可以采用 `?` 或者 `<any>` 来指定匹配任意元素:

<a name="s8_5_3_4_2c"></a>

```yaml
# s8.5.3.4.2c
Scenario:
  interactions:
    - description: test GET /foo/bar
      request:
        get: /foo/bar
      response:
        json:
          size: 5     # there shall be 5 elements in the array
          ?:          # for any element in the array, it shall be
            - gte: 1  # greater than or equals to `1`
            - lt: 6   # less than `6`
```

**提示** 数组校验可以和 POJO 校验混合使用, 例如:

```yaml
# s8.5.3.4.2d
Scenario:
  interactions:
    - description: test list employees
      request:
        get: /employees?q=Tom
      response:
        json:
          size:
            - gt: 0   # there must exists element in the response
          ?:          # for any element in the array, it shall be
            fullName: # the full name must contains "tom" (case insensitive)
              - containsIgnoreCase: Tom

```

#### <a name="html-response"></a> 8.5.3.5 html 类型内容校验

对于传统的后端生成页面的情况需要校验页面元素. ActFramework 提供了类似 jQuery 查询的方法来校验页面. 假设请求 GET /page/1 返回的结果为:

<a name="s8_5_3_5a"></a>

```html
<!-- snippet s8.5.3.5a -->
<html>
<head>
</head>
<body>
<h1>Page One</h1>
<p id="content">This is page one</p>
</body>
</html>
```

对应的测试脚本为:

<a name="s8_5_3_5b"></a>

```yaml
# snippet s8.5.3.5b
Scenario
  interactions:
    - description: test GET /page/1
      request:
        get: /page/1
      response:
        html:
          h1: Page One
          p#content:
            - contains: page one
```

## <a name="correlated_interactions"></a> 8.6 关联多个交互测试

很多时候多个测试交互相互之间需要关联起来, 例如测试创建用户就需要两个交互:

1. 创建用户
2. 验证创建好的用户

假设有下面的服务端口:

<a name="s8_6a"></a>

```java
@UrlContext("users")
@JsonView
public class UserService {

  @Inject
  private User.Dao userDao;

  @PostAction
  @Transactional
  public User create(User user) {
    return userDao.save(user);
  }

  @GetAction("{user}")
  public User get(@DbBind user) {
    return user;
  }
}
```

测试用户创建的脚本为:

<a name="s8_6b"></a>

```yaml
# snippet s8.6b
Scenario(CREATE_USER):
  interactions:
    - description: create the user
      request:
        post: /users
        params:
          firstName: Jack
          lastName: Smith
          email: jacks@x.com
    - description: verify user been created
      request:
        get: /users/${last:id}
      response:
        json:
          firstName: Jack
          lastName: Smith
          email: jacks@x.com
```

上面的测试脚本中值得注意的地方:

1. `create the user` 交互没有定义响应, 但测试框架会自动检查响应的状态码 (参见 [8.5.1 节](#response-status)), 同时将响应缓存起来
2. `verify user been created` 交互的请求定义为 `get /users/${last:id}`, 其中 `${last:id}` 的意思是: 从上一个交互响应中拿到名字为 `id` 的值

### <a name="cache-response-value"></a> 8.6.1 缓存响应值

因为每次交互都会有新的响应, 因此 `last` 缓存会被下一次交互重置. 如果需要保存某一次交互的响应, 则应该使用 `cache` 来给出缓存名字:

<a name="s8_6_1a"></a>

```yaml
# snippet s8.6.1a
Scenario(CREATE_USER):
  interactions:
    - description: create the user
      request:
        post: /users
        params:
          firstName: Jack
          lastName: Smith
          email: jacks@x.com
      response:
        json:
          id: 
            - exists: true
      cache:
        newUserId: id # store `id` of the current response into cache by name `newUserId`
    - description: verify user been created
      request:
        get: /users/${newUserId}
      response:
        json:
          firstName: Jack
          lastName: Smith
          email: jacks@x.com
```

### <a name="randomize-testing-data"></a> 8.6.2 随机生成测试数据

在上面的测试脚本中我们硬编码了下面的测试数据:

* firstName: Jack
* lastName: Smith
* email: jacks@x.com

通常测试希望采用随机数据来确保不会因为硬编码而漏掉逻辑中的一些错误. ActFramework 提供了随机测试数据生成机制, 下面是的用户创建测试脚本完全去掉了硬编码:

<a name="s8_6_2a"></a>

```yaml
# snippet s8.6.2a
Scenario(CREATE_USER):
  constants: # define random generated data and associated each data with a name
    newUserFirstName: ${randomFirstName()} 
    newUserLastName: ${randomLastName()}
    newUserEmail: ${randomEmail()}
  interactions:
    - description: create the user
      request:
        post: /users
        params:
          firstName: ${newUserFirstName} # refer to random data by name `newUserFirstName`
          lastName: ${newUserLastName} # refer to random data by name `newUserLastName`
          email: ${newUserEmail} # refer to random data by name `newUserEmail`
      response:
        json:
          id: 
            - exists: true
      cache:
        newUserId: id # store `id` of the current response into cache by name `newUserId`
    - description: verify user been created
      request:
        get: /users/${newUserId}
      response:
        json:
          firstName: ${newUserFirstName}
          lastName: ${newUserLastName}
          email: ${newUserEmail}
```

在上面的例子中我们使用了一下几个随机数据生成器:

* randomFirstName - 随机生成名 (英文)
* randomLastName - 随机生成姓 (英文)
* randomEmail - 随机生成电子邮件

ActFramework 还提供了更多的随机数据生成器, 包括:

* randomStr - 随机生成字串
* randomInt - 随机生成整型数字
* randomBoolean - 随机生成布尔数据
* randomLong - 随机生成长整型数字
* randomDate - 随机生成日期型数据
* randomFullName - 随机生成姓名 (英文)
* randomPassword - 随机生成密码字串
* randomUrl - 随机生成 URL
* randomUsername - 随机生成用户名
* randomCompanyName - 随机生成公司名 (英文)
* randomHost - 随机生成主机名
* randomMobile - 随机生成手机号码 (澳洲)
* randomPhone - 随机生成座机号码 (澳洲)
* randomPostCode - 随机生成邮编 (澳洲)
* randomState - 随机生成州名 (澳洲)
* randomStreet - 随机生成街名 (英文)
* randomSuburb - 随机生成区名 (英文)

## <a name="scenario-dependency"></a> 8.7 测试场景依赖

测试场景有可能有依赖关系, 典型的例子是大部分需要用户认证的测试场景都依赖于用户登录场景, 这个时候可以使用 `depends` 来指定依赖场景:

<a name="s8_7a"></a>

```yaml
# snippet s8.7a
Scenario(Login):
  interactions:
    - description: login testing user
      request:
        post: /login
        params:
          username: test001
          password: 123456
Scenario(A):
  depends:
    - Login
  ...
Scenario(B):
  depends:
    - Login
  ...
```

## <a name="scenario-partition"></a> 8.8 测试场景分区

当测试场景依赖关系涉及到多个测试测试场景的时候有可能由于执行顺序导致依赖关系被打破, 这时候需要定义测试分区. 假设我们有以下测试场景:

1. login
2. logout
3. add-bookmark
4. add-bookmark-unauthorized
5. update-bookmark
6. update-bookmark-unauthorized

其中 `add-bookmark`, `update-bookmark` 依赖与 `login`, 而 `add-bookmark-unauthorized`, `update-bookmark-unauthorized` 则依赖于 `logout`. 假如执行顺序为以上列表自上而下, 在执行 3. `add-bookmark` 的时候就会遇到问题, 因为其依赖 `login` 已经执行过了, 但会话又被 `logout` 了,因此场景 `add-bookmark` 将不会成功, 这个时候我们需要将这些测试场景使用 `partition` 关键字来标注分区, 用 `logout` 和 `add-bookmark-unauthorized` 来举例:

```yml
Scenario(Logout):
  partition: non-authenticated #分区
  setup: true
  noIssue: true
  description: Prepare - logout the current session
  interactions:
    - description: logout the current session
      request:
        get: logout
Scenario(Add bookmark - unauthorized):
  partition: non-authenticated #分区
  urlContext: bookmarks
  interactions:
    - description: It shall respond 401 if a guest user (user that not logged in) submit request to add bookmark
      request:
        method: post
        json:
          url: https://google.com
          description: The gate of the net
      response:
        status: 401
```

在上面的测试场景定义中我们使用了 `non-authenticated` 标注 `logout` 和 `add-bookmark` 测试场景, (显而易见, `update-bookmark-unauthozied` 也应该加入 `non-authenticated` 分区). 使用分区的目的在于保证同一个分区类的测试场景运行不会被其他分区测试场景干扰. 

当测试场景没有定义分区的时候归入 `default` 分区.

## <a name="organize-scenario-files"></a> 8.9 组织测试场景文件

默认的测试场景文件为 `/resources/test/scenarios.yml`, 应用可以将所有的测试场景全部放进这个文件中. 但如果测试场景太多, 管理会比较混乱, 同时还会对版本控制带来麻烦. 这个时候可以按照应用自己的方式将测试场景放进多个 `yml` 文件中, 所有测试场景文件应用放进 `resources/test/scenarios/` 目录. 例如 [act Github Issue 测试项目](https://github.com/actframework/actframework/tree/develop/testapps/GHIssues/src/main/resources/test/scenarios) 就将测试场景按照 issue 组织在 `resources/test/scenarios` 目录中.