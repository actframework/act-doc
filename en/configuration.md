# Configuration

**Note** this chapter is about general processing of configuration in ActFramework. For built-in configuration items, please refer to [configuration reference](reference/configuration.md).

ActFramework provides sophisticated support to make application developers easily specifify and consume configurations.

* [Define configuration](#define)
  - [secret in configuration key name](#key_name)
    + [value type indicator](#key_name-type_indicator)
    + [key alias](#key_name-key_aliases)
    + [enabled and disabled](#key_name-enabled_disabled)
  - [profile based configuration](#profile)
* [Consume configuration](#consume)
  - [pull configuration from `AppConfig`](#pull-configuration-from-appconfig)
  - [inject configuration](#inject-configuration-value)
  - [inject into static fields](#inject-into-static-fields-with-autoconf)
  - [inject complex types](#inject-complex-type)
    + [map](#inject-map)
    + [list](#inject-list)
    + [service implementation](#inject-implementation)

## <a name="define"></a>Define configuration

ActFramework read any file with `.properties` extension under `resources/conf` directory when an application start up. Here is an example of application configurations:

```
resources
  ├── conf
  │   ├── app.properties
  │   ├── db.properties
  │   ├── social.properties
  │   ├── storage.properties
  │   └── cron.properties
  ...
```

Inside each properties file, applicaiton developers can define relevant configuration items e.g.

```
jwt=true

session.ttl=60*30

cors=true
cors.origin=*
cors.headers=Content-Type, X-HTTP-Method-Override, X-Act-Session, X-Requested-With, Location
cors.option.check=false

cron.withdraw-job.db-load=0 30 13 * * ?
```

**Note** the file name and number doesn't matter. App developer can choose any number of properties files with whatever name to organize their application configuration.

### <a name="key_name"></a>Secrets in configuration key name

#### <a name="key_name-type_indicator"></a>1. value type indicator

Developer can use suffixes to specify the configuration item type:

* `.bool`, `.boolean`, `.enabled` or `.disabled` indicates a `boolean` type configuration. E.g. `secure.enabled`
* `.impl` indicates configuration is an instance to be loaded from class name. E.g. `xyz-service.impl`
* `.dir`, `.home`, `.path` indicates a `URI` type configuration. E.g. `lookup.path`
* `.long` indicates a `long` type configuration
* `.int`, `.ttl`, `.len`, `.count`, `.times`, `.size`, `.port` indicates an `int` type configuration
* `.float` indicates a `float` type configuration
* `.double` indicates a `double` type configuration

#### <a name="key_name-key_aliases"></a>2. configuration key aliases

**Note** this only applies to actframework build-in configurations (not including plugin configurations)

For build-in configuration developer can ommit **certain** type suffixes like `.int` etc and `act.` prefix. E.g. the following configuration items are exactly the same:

```
act.metric.enabled=false
metric.enabled=false
metric=false
act.metric=false
```

**Note** not all [type indicator suffix](#key_name-type_indicator) can be omitted, specifically the following suffixes shall not be omitted from key name:

* `.dir`
* `.home`
* `.path`
* `.ttl`
* `.port`
* `.len`
* `.count`
* `.times`
* `.size`

Thus, for example `act.session.ttl=60*5` cannot be simplied as `session=60*5`, however `session.ttl=60*6` is okay.

#### <a name="key_name-enabled_disabled"></a>3. a little bit intelligence about `.enabled` and `.disabled`

for any configuration named by `.enabled` it can be specified by `.disabled` with reversed value. For example the following configurations have the same means:

```
act.api_doc.enabled=true
act.api_doc.disabled=false
act.api_doc=true
```

## <a name="profile"></a>Profile based configuration

With decent projects we often need to deal with different configurations in different environments, for example, the jdbc URL is different when running app in a local development environment from in a production environment. ActFramework provides a tool called "profile based configuration" to organize configuration for different runtime environment. For example, we can create three profiles for an application: 

* sit - for System Integration Test
* uat - for User Acceptance Test
* prod - for production

Now we can line our configuration folder as shown below:

```
resources
  ├── conf
  │   ├── prod
  │   │   ├── app.properties
  │   │   └── db.properties
  │   ├── sit
  │   │   ├── app.properties
  │   │   └── db.properties
  │   ├── uat
  │   │   ├── app.properties
  │   │   └── db.properties
  │   ├── app.properties
  │   ├── db.properties
  │   └── cron.properties
  ...
```

In the above example, it defines a set of common properties `app.properties`, `db.properties`, `cron.properties` in `resources/conf` folder which will be loaded in all profiles. And it defines environment specific properties for three different profiles respectively, say `prod`, `sit`, `uat`. These properties will be loaded only in a runtime launched by specific profile and if there are entries with key name already defined in common properties, those entries will be overwritten by profile based definition.

To start the app in a specific profile, launch the Java process with JVM parameter `-Dprofile=<profile-name>`. For exmple, if it need to run the app in `uat` profile, the app needs to be started with `-Dprofile=uat` parameter.


## <a name="consume"></a>Consume configuration

**Note** this is about how to consume configuration defined in application code. Framework or plugins has their internal logic to process configurations.

ActFramework provides varieties of way for application to consume it's configuration settings. Suppose we have the following configuration properties defined:

```
myconf.foo.bar=60*30
```

### <a name="consume_pull"></a>Pull configuration from AppConfig

All configurations loaded by actframework is accessible through `AppConfig` instance. Thus application can pull the configuration value from `AppConfig` instance:

```java
@UrlContext("/conf")
public class ConfTest {
    @GetAction("pull")
    public int pull(AppConfig conf, StringValueResolverManager resolver) {
        Object o = conf.get("myconf.foo.bar");
        return resolver.resolve(o.toString(), int.class);
    }
}
```

As shown above, the `pull` request handler method get `AppConfig` and `StringValueResolverManager` injected as parameter, and then it pulls configuration `myconf.foo.bar` from `conf` and uses resolver manager to resolve the configured value `60*30` into `int` type value `1800` and return it.

### <a name="consume_inject"></a>Inject configuration value

Unlike [pull from AppConfig](#consume_pull), this approach inject the configuration value directly into field or parameter:

```java
@UrlContext("/conf")
public class ConfTest {

    @Configuration("myconf.foo.bar")
    private int fooBar;

    @GetAction("inject")
    public int inject() {
        return this.fooBar;
    }

    @GetAction("inject_param")
    public int injectParam(@Configuration("myconf.foo.bar") int fooBar) {
        return fooBar;
    }

}
```

### <a name="consume_autoconf"></a>Inject into static fields with `AutoConf`

This approach inject configured value into static fields:

```
@UrlContext("/conf")
@AutoConfig("myconf")
public class ConfTest {

    private static final Const<Integer> FOO_BAR = $.constant();

    @GetAction("auto_conf")
    public int autoConf() {
        return FOO_BAR.get();
    }

}
```

There are few things about this approach worth attentions:

1. The class must be annotated with `AutoConfig` annotation with a parameter indicate the namespace of the configuration item. In our example it is `myconf` which is the namespace of `myconf.foo.bar`. If the namespace is not specified, it default to `app`.

2. No need to put any annotation on the fields to be injected, in our case, the `FOO_BAR`.

3. The name of the field must be in underscore separated uppercase and corresponding to the configuration name without namespace. In our case the field name `FOO_BAR` corresponds to configuration `myconf.foo.bar` without namespace `myconf`

4. The type of the configuration could be direct type or a `Const` type which is preferred as we can declare the field as `final`, and later on use `FOO_BAR.get()` the retrieve the configured value.

### <a name="consume_complex_type"></a>Inject complex type

It is possible to inject complex type from configuration including Map, List and implementations.

### <a name="consume_map"></a>Inject Map

Suppose we have the following configurations:

```
myconf.foo.bar.one=1
myconf.foo.bar.two=2
```

We can inject the Map as:

```java
@GetAction("map")
@ResponseContentType(H.MediaType.JSON)
public Object barMap(@Configuration("myconf.foo.bar") Map<String, Integer> barMap) {
    return barMap;
}
```

Calling on the endpoint will get:

```json
{
  "one": 1,
  "two": 2,
}
```

We can also inject the Map as:

```java
@GetAction("map2")
@ResponseContentType(H.MediaType.JSON)
public Object barMap(@Configuration("myconf.foo") Map<String, Integer> fooMap) {
    return fooMap;
}
```

Calling on the endpoint will get:

```json
{
  "bar.one": 1,
  "bar.two": 2,
}
```

### <a name="consume_list"></a>Inject List

Given the following configuration:

```
myconf.list.demo=1,2,3
```

We can inject list or array of integer as:

```java
@GetAction("listDemo")
@ResponseContentType(H.MediaType.JSON)
public Object listDemo(@Configuration("myconf.list.demo") int[] list) {
    return list;
}
```

### <a name="consume_impl"></a>Inject implementation

Let's say there is an interface defined:

```java
public interface GreetingService {
    String greet();

    default String getName() {
        return greet() + " service";
    }
}
```

And there are several implementations of the `GreetingService` defined:

```java
public class HelloService implements GreetingService {
    @Override
    public String greet() {
        return "Hello";
    }
}
```

And

```java
public class NiHaoService implements GreetingService {
    @Override
    public String greet() {
        return "NiHao";
    }
}
```

Now the developer can configure the greeting service in different scenarios:

```
greet.scenario1=demo.HelloService
greet.senario2=demo.NiHaoService
```

To consume the configuration, it can declare the implementation as:

```java
@UrlContext("/conf")
public class ConfTest {

    @Configuration("greet.default")
    private GreetingService defaultService;

    @GetAction("greet")
    public String greetDefault() {
        return defaultService.greet();
    }

}
```

We can also inject a Map of implementations like:


```java
@UrlContext("/conf")
public class ConfTest {

    @Configuration("greet")
    private Map<String, GreetingService> greetingServiceMap;

    @GetAction("greet/all")
    public Object allGreetings() {
        return greetingServiceMap;
    }
}
```

Hitting `/conf/greet/all` will result in

```JSON
{
  "default": {
    "name": "Hello service"
  },
  "scenario2": {
    "name": "NiHao service"
  },
  "scenario1": {
    "name": "Hello service"
  }
}
```

Or inject a list of implementations:

properties:

```
greets=demo.helloworld.HelloService,demo.helloworld.NiHaoService
```

Java code:

```java
@UrlContext("/conf")
@ResponseContentType(H.MediaType.JSON)
public class ConfTest {
    
    @Configuration("greets")
    private List<GreetingService> greetingServices;

    @GetAction("greet/list")
    public Object greetingList() {
        return greetingServices;
    }

}
```

Going to `/conf/greet/list` will get something like:

```JSON
[
  {
    "name": "Hello service"
  },
  {
    "name": "NiHao service"
  }
]
```
