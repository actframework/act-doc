# Configuration

**Note** this chapter is about general processing of configuration in ActFramework. For built-in configuration items, please refer to [configuration reference](../configuration.md).

ActFramework provides sophisticated support to make application developers easily specifify and consume configurations.

* [Define configuration](#define)
  - secret in configuration key name
  - [profile based configuration](#profile)
* Consume configuration
  - Inject configuration value with `@AutoConfig` annotation
  - Inject configuration value with `@Configuration` annotation

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

#### 1. value type indicator




## <a name="profile"></a>Profile based configuration

With decent projects we often need to deal with different configuration in different environment, for example, the jdbc URL is different when running app in a local development environment from in a production environment. ActFramework provides a tool called "profile based configuration" to organize configuration for different runtime environment. For example, we can create three profiles for an application: 

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

In the above example, 
