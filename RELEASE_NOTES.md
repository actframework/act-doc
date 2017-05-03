# Release Notes

#### act-1.3.0

02/May/2017 - [What' New](http://actframework.org/doc/releases/r1.3.0.md)

* [#128 Create a mechanism to cache the GET request result](https://github.com/actframework/actframework/issues/128)
* [#154 Support multiple `handler.missing_authentication` configurations](https://github.com/actframework/actframework/issues/154)
* [#163 Introduce `@TemplateContext` annotation](https://github.com/actframework/actframework/issues/163)
* [#164 Split `@Controller` annotation into `@UrlContext` and `@Port` annotation](https://github.com/actframework/actframework/issues/164)
* [#167 `@Global` doesn't work when put behind the interceptor annotation](https://github.com/actframework/actframework/issues/167)
* [#168 Make all scanner favor the setting of `@Env` annotations](https://github.com/actframework/actframework/issues/168)
* [#169 Regex in route not working](https://github.com/actframework/actframework/issues/169)
* [#170 Make it easy to create global template variable](https://github.com/actframework/actframework/issues/170)
* [#171 Add helper javascript library that extends jQuery](https://github.com/actframework/actframework/issues/171)
* [#174 Support profile specific route configuration](https://github.com/actframework/actframework/issues/174)
* [#175 Create better error message when there are error enhancing classes](https://github.com/actframework/actframework/issues/175)
* [#177 Better error reporting when multiple controller action/interceptor methods have the same name](https://github.com/actframework/actframework/issues/177)
* [#178 When handler returns a primitive type the result is not JSON result when `Accept` header require JSON](https://github.com/actframework/actframework/issues/178)
* [#179 Provide an annotation to mark a field or parameter as template variable](https://github.com/actframework/actframework/issues/179)
* [#180 Setting character encoding in response doesn't effect correctly](https://github.com/actframework/actframework/issues/180)
* [#181 Make redirect favor Controller URL context](https://github.com/actframework/actframework/issues/181)
* [#182 Make app able to run `prod` mode from within IDE](https://github.com/actframework/actframework/issues/182)


#### act-1.2.0

24/Apr/2017 - [What's New](http://actframework.org/doc/releases/r1.2.0.md)

* [#161 Add an annotation that indicate an injected field is stateless](https://github.com/actframework/actframework/issues/161)
* [#160 Make `ActionContext` an injectable field in `Controller.Util`](https://github.com/actframework/actframework/issues/160)
* [#159 generated pid file not get deleted when app process is killed](https://github.com/actframework/actframework/issues/159)
* [#157 SEO support on routing](https://github.com/actframework/actframework/issues/157)
* [#156 Compile error is not displayed at dev mode](https://github.com/actframework/actframework/issues/156)
* [#153 When `@NotNull` used along with `@DbBind` it shall return 404 if binding failed](https://github.com/actframework/actframework/issues/153)_
* [#152 Allow annotation based interceptor class to be registered as global interceptor](https://github.com/actframework/actframework/issues/152)
* [#136 Allow `@With` annotation to be used on specific handler method](https://github.com/actframework/actframework/issues/136)
* [#124 Improve error reporting on "Unknown accept content type"](https://github.com/actframework/actframework/issues/124)

#### act-storage-0.10.0

24/Apr/2017

* [#11 Enable user defined `KeyGenerator`](https://github.com/osglworks/java-storage/issues/11)


#### act-1.1.2

17/Apr/2017

* [#151 Update version of osgl and other dependencies](https://github.com/actframework/actframework/issues/151)
* [#150 Deadlock while app boot up](https://github.com/actframework/actframework/issues/150)

#### act-storage-0.9.0

17/Apr/2017

* [#10 NPE triggered when loading storage object without attr file](https://github.com/osglworks/java-storage/issues/10)
* [#9 Allow plugin key name provider and key generator](https://github.com/osglworks/java-storage/issues/9)
* [#8 Allow store sobject with suffix attached to the key](https://github.com/osglworks/java-storage/issues/8)

#### act-aaa-1.1.0

17/Apr/2017

* [#1 Rework on AAA facade, added a lot of convenient APIs](https://github.com/osglworks/java-aaa/issues/1)
* [#2 AAA and AAAPersistenceService now has APIs to return all roles, permissions and privileges](https://github.com/osglworks/java-aaa/issues/2)

#### act-ebean-1.1.5

* [#16 NPE when no third party datasource configured](https://github.com/actframework/act-ebean/issues/16)

#### act-ebean2-1.0.4

* [#6 NPE when no third party datasource configured](https://github.com/actframework/act-ebean2/issues/6)

#### act-sql-common-1.0.2

* [#3 Allow specific implementation to initialize in different logic when dataSourceProvider available or not](https://github.com/actframework/act-sql-common/issues/3)

#### act-ebean-1.1.4

* [#14 Ebean Agent loaded twice if there are two ebean db services](https://github.com/actframework/act-ebean/issues/14)
* [#15 The datasource created in sql-common not used when creating ebean server](https://github.com/actframework/act-ebean/issues/15)

#### act-ebean2-1.0.3

* [#3 It doesn't start with MySQL jdbc driver 5.x](https://github.com/actframework/act-ebean2/issues/3)
* [#4 Ebean Agent loaded twice if there are two ebean2 db services](https://github.com/actframework/act-ebean2/issues/4)
* [#5 The datasource created in sql-common not used when creating ebean server](https://github.com/actframework/act-ebean2/issues/5)


#### act-sql-common-1.0.1

* [#2 The default jdbc driver doesn't work with mysql jdbc driver 5.x](https://github.com/actframework/act-sql-common/issues/2)
* [#1 When it uses h2 with db on filesystem, it shall ignore the `ddl.create` if that file exists](https://github.com/actframework/act-sql-common/issues/1)

#### act-1.1.1

* [#148 Support get process ID on non-unix environment](https://github.com/actframework/actframework/issues/148)
* [#147 Unnecessary synchronization ReflectedHandlerInvoker.checkTemplate](https://github.com/actframework/actframework/issues/147)
* [#146 When db plugin is configured, it uses empty string as service ID](https://github.com/actframework/actframework/issues/146)

#### act-1.1.0

* [#142 Always generate pid file when app start in prod mode](https://github.com/actframework/actframework/issues/142)
* [#141 Support context URL path](https://github.com/actframework/actframework/issues/141)
* [#140 Cannot use multiple Job annotations on one job method](https://github.com/actframework/actframework/issues/140)
* [#139 allow SimpleEventHandler to be used to handle event happening before app started](https://github.com/actframework/actframework/issues/139)
* [#138 Update FastJson to 1.2.31](https://github.com/actframework/actframework/issues/138)
* [#137 DbService update](https://github.com/actframework/actframework/issues/137)

#### act-sql-common-1.0.0

* [An new ActFramework module for all DB plugins that needs to play with SQL database](https://github.com/actframework/act-sql-common)

#### act-beetlsql

* [BeetlSQL plugin for ActFramework](https://github.com/actframework/act-beetlsql)

#### act-ebean2-1.0.1

* [New ebean plugin that uses the latest ebean library](https://github.com/actframework/act-ebean2) - Require JDK 8+

#### act-ebean-1.1.2

* [#12 Migrate ebean plugin to new DB plugin architecture - extends from act-sql-common-1.0.0](https://github.com/actframework/act-ebean/issues/12)

#### act-1.0.7

* [#70 Make it able to configure the number of network io threads and work threads](https://github.com/actframework/actframework/issues/70)
* [#120 configuration `render.json.output_charset.enabled` default value shall be `false`](https://github.com/actframework/actframework/issues/120)
* [#127 qrcode method problem](https://github.com/actframework/actframework/issues/127)
* [#130 Response outputstream not closed](https://github.com/actframework/actframework/issues/130)
* [#131 `ZXingResult` call `applyAfterCommitHandler` twice](https://github.com/actframework/actframework/issues/131)
* [#132 "type not recognized: MODEL_TYPE" Error when using a DaoBase subclass as Controller](https://github.com/actframework/actframework/issues/132)
* [#133 It uses undertow deprecated API to construct HttpOpenListener](https://github.com/actframework/actframework/issues/133)
* [#134 Fine tune undertow configurations](https://github.com/actframework/actframework/issues/134)

#### act-ebean-1.1.0

* [#9 Support plugin different datasource solution](https://github.com/actframework/act-ebean/issues/9)
* [#8 change mysql jdbc driver class name](https://github.com/actframework/act-ebean/issues/8)
* [#6 Support Druid database connection pool](https://github.com/actframework/act-ebean/issues/6)


#### act-1.0.6

* [#121 #115 caused issue that failed to add route mapping in certain case](https://github.com/actframework/actframework/issues/121)

#### act-1.0.5

* [#118 Version range doesn't work as expected](https://github.com/actframework/actframework/issues/118)

#### act-1.0.4

* [#109 It shall display the exception stack trace tab on template exception page](https://github.com/actframework/actframework/issues/109)
* [#110 Using simplified action path in @fullUrl and @url doesn't work in an free template](https://github.com/actframework/actframework/issues/110)
* [#111 Routing failure on `/{path1}/{path2}/{path3}/{id}.html` style URL path](https://github.com/actframework/actframework/issues/111)
* [#112 Missing embedded object content when PropertySpec is specified](https://github.com/actframework/actframework/issues/112)
* [#115 Router: support inner variables inside URL path](https://github.com/actframework/actframework/issues/115)

#### act-ebean-1.0.5

* [#5 Make it easy to do low level JDBC logic](https://github.com/actframework/act-ebean/issues/5)
* [#4 Use HikariCP as datasource connection pool](https://github.com/actframework/act-ebean/issues/4)

#### rythmengine-1.2.0

* [#362 Remove `__sep` and `__util` iterable variables](https://github.com/rythmengine/rythmengine/issues/362)
* [#361 Replace `Stack` with `Deque` in `TemplateBase`](https://github.com/rythmengine/rythmengine/issues/361)

#### osgl-http-1.0.2

* [#1 H.Rquest.fullUrl() shall not output `80` when sending to port `80`](https://github.com/osglworks/java-http/issues/1)

#### act-1.0.3

* [#68  Error enhancing render arguments when break the statement into multiple lines](https://github.com/actframework/actframework/issues/68)
* [#84 @fullUrl and @url tag doesn't work when there is no GET request mapping to the action handler method](https://github.com/actframework/actframework/issues/84)
* [#89 session.ttl setting prevent app from start up](https://github.com/actframework/actframework/issues/89)
* [#94 Invalid encoded characters in Error page](https://github.com/actframework/actframework/issues/94)
* [#97 Act controller not return correct @version "v" for save method when MorphiaDao return the value bug](https://github.com/actframework/actframework/issues/97)
* [#99  Update FastJson version to 1.2.29](https://github.com/actframework/actframework/issues/99)
* [#100 when the browser get a json request, Chinese characters are not displayed properly](https://github.com/actframework/actframework/issues/100)
* [#101 IE doesn't support "application/json" content type](https://github.com/actframework/actframework/issues/101)
* [#104 Incorrectly configured routes should not crash hot-reload](https://github.com/actframework/actframework/issues/104)
* [#106 Reloading View manager might break the hot reload process](https://github.com/actframework/actframework/issues/106)
* [#107 Simplify the use of reverse routing API](https://github.com/actframework/actframework/issues/107)
* [#108 Simplify the use of `@url` and `@fullUrl` tag](https://github.com/actframework/actframework/issues/108)

#### act-morphia-1.0.2

* [#5 Act controller not return correct @version "v" for save method when MorphiaDao return the value](https://github.com/actframework/act-morphia/issues/5)

#### act-freemarker-1.0.1

* [#1 Allow user to use `.ftl` or configured suffix in template name](https://github.com/actframework/act-freemarker/issues/1)

#### act-1.0.2

* Fix [# 88 Controller context break with intermediate non-controller class in the hierarchies](https://github.com/actframework/actframework/issues/88)
* Fix [#87 DependencyInjectionListener shall register with sub classes of the target class also](https://github.com/actframework/actframework/issues/87)
* Fix [#86 It shall allow `null` value for enum type parameter when do the request parameter binding](https://github.com/actframework/actframework/issues/86)
* Fix [#83 Hot reload will not process changes to messages resource bundles](https://github.com/actframework/actframework/issues/83)

#### act-morphia-1.0.1

* Fix [#4 MorphiaInjectionListener not effect on User defined Dao](https://github.com/actframework/act-morphia/issues/4)

#### act-ebean-1.0.1

* Fix [#1 EbeanDao.drop() method cause JdbcSQLException](https://github.com/actframework/act-ebean/issues/1)
* Fix [#3 EbeanInjectionListener not effect on User defined Dao](https://github.com/actframework/act-ebean/issues/3)

## act-aaa-1.0.1

* Fix [#1 It shall not throw out NPE when AAA service cannot find the user in the system](https://github.com/actframework/act-aaa-plugin/issues/1)

#### act-1.0.1

* Fix [#81 Duplicate route mapping breaks the hot reloading and application state](https://github.com/actframework/actframework/issues/81)
* Fix [#79 static action handler method cause NPE](https://github.com/actframework/actframework/issues/79)

#### act-1.0.0

The the first formal release
