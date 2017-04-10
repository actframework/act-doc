# Release Notes

#### act-1.1.0

* [#142 Always generate pid file when app start in prod mode](https://github.com/actframework/actframework/issues/142)
* [#141 Support context URL path](https://github.com/actframework/actframework/issues/141)
* [#140 Cannot use multiple Job annotations on one job method](https://github.com/actframework/actframework/issues/140)
* [#139 allow SimpleEventHandler to be used to handle event happening before app started](https://github.com/actframework/actframework/issues/139)
* [#138 Update FastJson to 1.2.31](https://github.com/actframework/actframework/issues/138)
* [#137 DbService update](https://github.com/actframework/actframework/issues/137)

#### act-sql-common-1.0.0

* [An new ActFramework module for all DB plugins that needs to play with SQL database](https://github.com/actframework/act-sql-common)

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
