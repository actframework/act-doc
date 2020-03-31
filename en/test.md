# Chapter 8 Testing

ActFramework provides powerful automate testing support based on test scenario in YAML format 

## <a name="introduction"></a> 8.1 Introduction

Tranditional automate testing are implemented based on JUnit, as mentioned in the SpringFramework's [Testing Web Layer](https://spring.io/guides/gs/testing-web/):

<a name="s8_1_a"></a> Controller:

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

<a name="s8_1_b"></a> Testing:


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

What we get from the above code:

1. Spring provides JUnit based test for RESTful service system test
2. It is pretty verbose to define even simpliest test case

Now let's look at how ActFraework implement the same testing.

<a name="s8_1_c"></a> Controller:

```java
// snippet s8.1c
public class HomeController {

    @GetAction
    public String greeting() {
        return "Hello World";
    }

}
```

<a name="s8_1_d"></a> Testing(defined in `/resources/test/scenarios.yml` file):

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

How to run Act test:

1. `mvn clean compile act:test` - run `test` in command line. This will use `test` profile to start the application and automatically run all test scenarios defined in the project. If all test passed the process will return `0` to operting system.
2. Start the app in `dev` mode and then navigate to `/~/test` to run all test scenarios and get the report in brower.

Normally command line should be used in a CI system, e.g. Jenkins. While developer can use the browser based testing for quick feedback.

## <a name="fixtures"></a> 8.2 Clear/Setup fixtures

It usually require fixture setup/clear for any non-trivial project. ActFramework always clear the data before running a-non-dependent scenario and provides facilities to prepare the testing data:

### <a name="load-fixture-yaml"></a> 8.2.1 Loading testing data from fixtures yaml file

Suppose application defines the following model class:


<a name="s8_2_1a"></a> Course

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

<a name="s8_2_1b"></a> User

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

Developer can create a fixture yaml file, e.g. `resources/test/fixtures/init-data.yml` (the name `init-data` could be any string, while the foler `resource/test/fixtures` is important):

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

In the above yaml file we define "math" and "history" `Course` entities, as well as "green" and "black" `User` entities. Note it can specify the full qualified class name like `com.myproj.models.User` or if app has defined `test.model-packages=com.myproj.models` then the package name can be omitted. Each entity can have a name like `math`, `history`, `green` and `black`. The name can be used for the following entity to refer to the entity that has been defined previously.

Note we use the keyword `ref:` to specify a reference to an already defined entity, e.g. `ref:math` - means the ID of entity `math` should be put in place.

Aside from `ref:` ActFramework also support `embed:` and `password:` keyword in fixture yaml file:

* `embed:x` - embed the entire entity named `x` in place, this require the current model class support embedded structure
* `password:1234` - apply password hash on `1234` string and then store the result in place

#### <a name="use-fixture-yaml-in-scenario"></a> 8.2.1.1 Refer the fixture YAML file in scenario definition

If a certain scenario needs to load fixtures, use `fixtures` list block to specify fixture files:

```yaml
Scenario(One):
  fixtures:
    - init-data.yml
  ...
```

With the above definition, framework will load all fixture data in `init-data.yml` file (located in `resources/test/fixtures` folder) before running scenario `One`.

### <a name="generate-test-data"></a> 8.2.2 Generate test data automatically

Fixture yaml file is not proper when there are big set of test data required, in which case developer can use `generateTestData` tool in Scenario specification:

```yaml
Scenario(Prepare):
  generateTestData:
    - User
```

With the above scenario definition, the framework will generate 100 user data using random algorithm. Developer can also specify the number of random testing data to be generated:

```yaml
Scenario(Prepare):
  generateTestData:
    User: 200
```

### <a name="load-fixture-job"></a> 8.2.3 Customize the testing data generation

In case app has special requirement on loading testing data, it can use `FixtureLoader` annotation to define the logic:

```java
@FixtureLoader("load-my-test-data")
public vod loadUsers(User.Dao userDao, Course.Dao courseDao) {
    // define the logic to load test data
}
```

Once the fixutre loader method has been defined, the logic name can be used in the scenario specification:

```yaml
Scenario(One):
  fixtures:
    - load-my-test-data
```

### <a name="clear-test-data"></a> 8.2.4 Clear testing data

While scenario does not depend on any other scneario, ActFramework will always clear all entity data (except the ones annotated with `NoFixture`). This sounds at bit risky. However ActFramework will never run test scenarios in prod mode, thus it won't impact online system. It does requrie the developer to be aware of this while running tests in dev mode to prevent the local database from been cleared by accident. A better solution is to create a special profile to run automate tests, and define a separate database connection in that profile. 

For certain data that is immutable in most cases, e.g. configuration/geolocation etc, in case the data itself is not going to be created/updated/deleted in all scenarios, it might be good to use `NoFixture` annotation to mark on the entity class so framework will waive data clearing on the models:

```java
@Entity(mame = "city")
@NoFixture
public class City extends SimpleBean {
  public String name;
  ...
}
```

As `@NoFixture` is annotated on the `City` class, the city data will never get cleared during testing. 


## <a name="scenario-structure"></a> 8.3 Test Scenario file structure

Test Scenarios can be defined in yaml format file(s):

1. `resources/test/scenarios.yml`
2. Any `.yml` file located in `resources/test/scenarios` dir

Below shows the structure of a test scenario file:

![image](https://user-images.githubusercontent.com/216930/45152661-099ad700-b215-11e8-95c4-318cc3e7a3df.png)

As demonstrated in the above image, it can define multiple test scenarios in a test scenario file. Inside each scenario, it can define multiple interactions, each interaction defines request and response specification

## <a name="request-spec"></a> 8.4 Request

Request specification defines HTTP method, URL and parameters:

<a name="s8_4a"></a>

```yaml
# snippet s8.4a
request:
  get: /foo
  params:
    bar: 123
```

The above case defines a `GET /foo?bar=123` request, Note we use the `params` section to specify the `bar=123` query parameter. The request can also be specified as:

<a name="s8_4b"></a>

```yaml
# snippet s8.4b
request:
  get: /foo?bar=123
```

The following example defines a post request:

<a name="s8_4c"></a>

```yaml
# snippet s8.4c
request:
  post: /users
  params:
    user.name: Thomas
    user.email: tom@x.com
```

The above code specify the post parameter as form body encode by using `params` section. It can also use JSON to specify the post parameter:

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

### <a name="request-header"></a> 8.4.1 Request header

If needed, developer can add header into request specification:

<a name="s8_4_1a"></a>

```yaml
# snippet s8.4.1a
request:
  headers:
    X-Token: 123
```

## <a name="response-spec"></a> 8.5 Response

The `response` section is used in test scenarios to define a response and verifications:

<a name="s8_5a"></a>

```yaml
# snippet s8.5a
response:
  json:
    name: Thomas
    email: tom@x.com
```

As shown in the above example, the response expect a JSON encoded body containing two fields:

1. `name` - should have the value `Thomas`
2. `email` - should have the value `tom@x.com`

### <a name="response-status"></a> 8.5.1 Verify response status code

There is no explicity response status code specified in the above example. In which case the code `2xx` is supposed to be the expected response status code. Response with any other status code is considered to be failure case. In case a response expect a failure status code, it needs to specify the status code in response specification:

<a name="s8_5_1a"></a>

```yaml
# snippet s8.5.1a
response:
  status: 404
```

The above response specification means it expect a response come with status code `404`

### <a name="response-header"></a> 8.5.2 Verify response header

Use `headers` section to specify response header verification

<a name="s8_5_2a"></a>

```yaml
# snippet s8.5.2a
response:
  headers: 
    X-Token: 123
```

The above code means it expect a header named `X-Token` in the response with value be `123`.

### <a name="response-body"></a> 8.5.3 Verify response body

Response body verification is more sophisticated than status code and header verification. We will elaborate the response body verification in the following three sections:

* Value verifier
* Response content type
* Cache response value

#### <a name="verifier"></a> 8.5.3.1 Value verifier

ActFramework built in the following value verifiers:

* after: Check if date/time is after given date time
* before: Check if date/time is before given date time
* contains: Check if a string contains given string
* containsIgnoreCase: Same as contains but do string comparison case insensitively
* ends: Check if string ends
* eq: Check if a value equas given value
* eqIgnoreCase: Check if a string typed value equals given value case insensitively
* exists: check if a value exists
* gt: Check if a value is greater than a given value
* gte: Check if a value is greater than or equal to a given value
* lt: Check if a value is less than a given value
* lte: Check if a value is less than or equal to a given value
* neq: Check if a value is not equal to a given value
* starts: Check if a string typed value starts with a given string

It can apply multiple value verifier to a value, in such case the test pass only when all value verifier passed:

<a name="s8_5_3_1a"></a>

```yaml
# snippet s8.5.3.1a
response:
  json:
    count: 
      - exists: true
      - neq: 123
```

In the above example, it require the value `count` in the JSON response exists and not equal to int value `123`

```
{"a": 123} // test fail as no value named `count` 
```

```
{"count": 123} // test fail as `count` is `123`
```

**Note** Test scenario support specify date/time verification value with any one of the following format:

* `yyyy-MM-dd hh:mm:ss`
* `yyyy-MM-dd HH:mm:ss`
* `yyyy-MM-dd`

For example:

<a name="s8_5_3_1b"></a>

```yaml
# snippet s8.5.3.1b
response:
  text:
    - after: 1997-05-11 # the returned date should be after date 11/May/1997
    - before: 2018-05-31 # the returned date should be before date 31/May/2018
```

#### <a name="response-type"></a> 8.5.3.2 Response content type

It uses `json` content type in [Example s8.5a](#s8_5a), which is the commonly used response type in RESTful service. Aside from `json`, ActFramework also support the following content types:

* text
* html

We will go through all three content type verification in Act test.

#### <a name="text-response"></a> 8.5.3.3 Verify text content

`text` content type is used in very simple case, e.g.

<a name="s8_5_3_3a"></a>

```java
// snippet s8.5.3.3a
@GetAction("/hello")
public String sayHello() {
    return "Hello World";
}
```

Below is the test scenario to verify the above endpoint that returns pure text:

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

**Note* we have specified `accept: text/plain` to force [sayHello()](#s8_5_3_3a) method returns `text/plain` content type. Otherwise ActFramework will default to `application/json` to output response.

It can also use verifiers to verify the content:

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

#### <a name="json-response"></a> 8.5.3.4 Verify JSON response

Again let's take [sayHello()](#s8_5_3_3a) request hander as the example, if request ask for `accept: application/json`, we expect a JSON type response:

<a name="s8_5_3_4a"></a>

```JSON
// snippet s8.5.3.4a
{
  "result": "Hello World"
}
```

And the test scenario specification shall be defined as:

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

or with verifiers:

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

##### <a name="json-response"></a> 8.5.3.4.1 Verify JSON POJO response

Below is a more sophisticated POJO JSON response. Suppose we have a request handler defined as:

<a name="s8_5_3_4_1a"></a>

```java
// snippet s8.5.3.4.1a
@JsonView
@GetAction("/users/{user}")
public User getUser(@DbBind User user) {
  return user;
}
```

And suppose the request to `GET /users/1` shall return:

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

Then the test scenario shall be defined as:

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

##### <a name="json-response"></a> 8.5.3.4.2 Verify JSON array

It is a little bit more tricky to verify JSON array response. Suppose request to `/foo/bar` shall return:

<a name="s8_5_3_4_2a"></a>

```json
// s8.5.3.4.2a
[1, 2, 3, 4, 5]
```

Here is the corresponding test scenario definition:

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

It can use `?` or `<any>` to verify any item in the JSON array so we do not need to know exact array index to verify the logic:

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

**Note** developer can do combined POJO and array verification, e.g.

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

#### <a name="html-response"></a> 8.5.3.5 Verify html content

For tranditional server side rendering page, it generate HTML content. ActFramework provides jQuery style query for HTML page verification. Suppose request to `GET /page/1` shall return HTML result as:

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

Here is the corresponding test scenario definition:

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

## <a name="correlated_interactions"></a> 8.6 Correlated interactions

It is not unusual that we need to verify a certain function by running through multiple interactions in a testing scenario, e.g. to verify a user creation we need at least two interactions:

1. Create user
2. Get created user and verify the data

Suppose the app has the following service endpoints:

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

The test scenario to verify user creation should be defined as:

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

A few place in the above scenario specification worth mentioning:

1. The `create the user` interaction does not have response specification, however the framework will anyway check the response status code to make sure the request is successful (refer to [8.5.1](#response-status)). The framework will also cache the response so that the data can be referred in the following interaction.
2. The `verify user been created` interaction defines the request as `get /users/${last:id}`, here `${last:id}` means: get the `id` field from the last response data

### <a name="cache-response-value"></a> 8.6.1 Cache response value

Since each interaction will have an new response, thus the `last` cache will get flushed everytime when act run an new test interaction. In case it needs to refer to the data of the response in interaction happens before last interaction, we need to `cache` the data and assign it to a given name:

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
      assign:
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

**Note** here `assign` can also be `cache`, `store` and `save`, these four keyword can be used interchangably.

### <a name="randomize-testing-data"></a> 8.6.2 Generate testing data randomly on the fly

In the above scenario definition we have hardcoded the following testing data:

* firstName: Jack
* lastName: Smith
* email: jacks@x.com

Sometimes we want to use randomly generated data to ensure we don't miss capturing logic error. ActFramework provides random data generation tool. The following user creation test scenario definition removed all hardcoded testing data:

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

In the above code we used three random testing data generators:

* randomFirstName - generate first name
* randomLastName - generate last name
* randomEmail - generate email

Aside from these three generators, ActFramework provides more testing data generator including:

* randomStr - generate random string
* randomInt - generate random int value
* randomBoolean - generate random bool value
* randomLong - generate random long value
* randomDate - generate random Date type value
* randomFullName - generate random first name
* randomPassword - generate random password
* randomUrl - generate random URL
* randomUsername - generate random username
* randomCompanyName - generate random company name
* randomHost - generate random host nane
* randomMobile - generate random mobile number (Australia)
* randomPhone - generate random land line number (Australia)
* randomPostCode - generate random post code (Australia)
* randomState - generate random state (Australia)
* randomStreet - generate random street
* randomSuburb - generate random suburb


## <a name="scenario-dependency"></a> 8.7 Test scenario dependency

It is possible that test scenarios depends on other test scenarios. A typical example is most test scenario that require user authentication relies on login scenario. ActFramework provides `depends` section to specify dependent scenarios:

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

## <a name="scenario-partition"></a> 8.8 Test scenario partitioning

Test scenario partitioning can be used to group test scenario execution to prevent test scenario dependency from being broken. Suppose we have the following test sceanrios:

1. login
2. logout
3. add-bookmark
4. add-bookmark-unauthorized
5. update-bookmark
6. update-bookmark-unauthorized

The `add-bookmark`, `update-bookmark` depend on `login` while `add-bookmark-unauthorized` and `update-bookmark-unauthorized` depend on `logout`. Even if we have defined the dependency relationship in `depends` section, it is still possible the dependency being broken. Suppose framework run the test scenarios in a top-down order, when it running `add-bookmark` it will call `login` and `login` has already been executed, so it returns immediately. However `logout` has been running after `login`, and it terminated the login session thus `add-bookmark` will always fail. In order to address the issue, ActFramework introduced `partition` keyword to group senarios into different partitions: 

```yml
Scenario(Logout):
  partition: non-authenticated
  setup: true
  noIssue: true
  description: Prepare - logout the current session
  interactions:
    - description: logout the current session
      request:
        get: logout
Scenario(Add bookmark - unauthorized):
  partition: non-authenticated
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

As shown in above test scenario definition we use `non-authenticated` to group `logout` and `add-bookmark` into the same partition (Obviously `update-bookmark-unauthorized` shall be marked with the same partition). By doing this act will not disturb the running of scenarios in one partition with scenarios in other partitions.

If a scenario does not have partition defined, then it is default to be in `default` partition.

## <a name="organize-scenario-files"></a> 8.9 Organize scenario files

Test scenarios can be defined in `/resources/test/scenarios.yml` file. However if there are too many test scearnios, it might be better to organize sceanrios into different yaml files and put them in `resources/test/scenarios/` dir. For example, [act Github Issue test project](https://github.com/actframework/actframework/tree/develop/testapps/GHIssues/src/main/resources/test/scenarios) orgnaize test sceanrios by issue and all sceanrio files are saved in `resources/test/scenarios` dir.