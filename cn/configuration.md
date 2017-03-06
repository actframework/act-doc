<h1 data-book="configuration">配置手册</h1>

详细定义ActFramework使用到的各种配置

#### [basic_authentication]basic_authentication.enabled

别名:

* **basic_authentication**
* **act.basic_authentication**
* **act.basic_authentication.enabled**

开关[Basic Authentication](https://en.wikipedia.org/wiki/Basic_access_authentication).

默认值: `false`

**注意** ActFramework本身不会使用这个参数. 但像[act-aaa-plugin](https://github.com/actframework/act-aaa-plugin)这样的安全插件会使用这个配置项

#### [cache_impl]cache.impl

别名

* **cache**
* **act.cache**
* **act.cache.impl**

指定缓存服务的具体实现类. 指定的实现类必须实现`org.osgl.cache.CacheServiceProvider`接口

默认值: `Auto`, 即: `org.osgl.cache.CacheServiceProvider.Impl.Auto`. 该实现会顺序依据以下条件选择具体的实现

1. 如果`MemcachedServiceProvider`存在则用之, 否则
2. 如果`EhCacheServiceProvider`存在则用之, 否则
3. 加载`SimpleCacheServiceProvider`实例. 该实现依赖于ConcurrentMap

#### [cache_name]cache.name

别名

* **act.cache.name**

指定框架使用的缓存名字

默认值: `_act_app_`

#### [cache_name_session]cache.name.session

别名

* **act.cache.name.session**

指定会话缓存名字

默认值: [cache.name](#cache_name)的设置

#### [cli]cli

别名

* **act.cli**
* **act.cli.enabled**
* **cli.enabled**

开关CLI支持. 当CLI特性被允许是管理员可以通过telnet到[CLI端口](#cli_port)来访问CLI命令集

默认值: `true`

#### [cli_page_size_json]cli.page.size.json

别名

* **act.cli.page.size.json**

指定一个CLI命令JSON输出格式每页最大记录数

默认值: 10

#### [cli_page_size_table]cli.page.size.table

别名

* **act.cli.page.size.table**

指定CLI命令表输出格式每页最大记录数

默认值: 22


#### [cli_port]cli.port

别名

* **act.cli.port**

指定CLI端口

默认值: `5461`

#### [cli_session_expiration]cli.session.expiration

别名

* **act.cli.session.expiration**

指定CLI会话过期等待时间

默认值: `300`, 即: 5分钟

### [cli_session_max]cli.session.max

别名

* **act.cli.session.max**

指定能同时进行的CLI会话数量

默认值: `3`

#### [cli_over_http]cli_over_http

别名

* **cli_over_http.enabled**
* **act.cli_over_http**
* **act.cli_over_http.enabled**

开关CLI over http功能

默认值: `false`

当该功能被允许时, 管理员可以通过HTTP[服务端口](#cli_over_http.port)使用CLI命令

#### [cli_over_http_authority_impl]cli_over_http.authority.impl

别名

* **cli_over_http.authority**
* **act.cli_over_http.authority**
* **act.cli_over_http.authority.impl**

配置CLI over http的授权机制. 指定值必须是`act.cli.CliOverHttpAuthority`接口的某个实现类的名字

默认值: `CliOverHttpAuthority.AllowAll` - 运行所有人在指定端口使用CLI命令.

#### [cli_over_http_port]cli_over_http.port

别名

* **act.cli_over_http.port**

指定CLI Over HTTP服务端口

默认值: `5462`

#### [cli_over_http_title]cli_over_http.title

别名

* **act.cli_over_http.title**

指定CLI Over HTTP的页面标题

默认值: `Cli Over Http`

#### [cli_over_http_syscmd]cli_over_http.syscmd

别名

* **cli_over_http.syscmd.enabled**
* **act.cli_over_http.syscmd**
* **act.cli_over_http.syscmd.enabled**

允许/禁止通过CLI Over Http访问系统命令

默认值: `true`

#### [cookie_domain_provider]cookie.domain_provider

别名

* **cookie.domain_provider.impl**
* **act.cookie.domain_provider**
* **act.cookie.domain_provider.impl**

指定一个返回cookie域名的Provider实现类. 如果未指定该项, 系统默认返回[host](#host)配置

有效设置:

1. 一个`javax.inject.Provider`实现的类名, 给实现必须返回`String`类型

2. `dynamic`或`flexible`或`contextual`, 这三个设置均表示cookie域名是当前HTTP请求的域名

默认值: `null`


#### [cookie_prefix]cookie.prefix

别名

* **act.cookie.prefix**

Specifies the prefix to be prepended to name of the cookies used in ActFramework, e.g. session, flash, xsrf etc. Let's say the default cookie name is ｀act_session｀, and user specifies the prefix ｀my_app｀then the session cookie name will be ｀my_app_session｀

Note this setting also impact the ｀AppConfig#flashCookieName()｀

默认值: calculated based on the following logic:

1. find the app's name, if not found, then use `act` as app name
2. split the app name by spaces
3. check the length of splited string array
3.1 if there is only one string in the array, then return the first 3 chars of the string, or the string if string len is leass than 3
3.2 if there are two strings in the array, then pick up the first 2 chars of each string and concatenate by dash `-`
3.3 pick up the first char of the first 3 strings in the array

E.g.

When app name is `HelloWorld`, the cookie prefix is `hel-`
When app name is `Hello World`, the cookie prefix is `he-wo-`
When app name is `Hello My World`, the cookie prefix is `hmw-`

#### [cors]cors

别名

* **cors.enabled**
* **act.cors**
* **act.cors.enabled**

打开/关闭对[CORS](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing)的支持

默认值: `false`

当`cors`打开是, ActFramework将向HTTP响应添加CORS相关的头

#### [cors_option_check]cors.option.check

别名

* **cors.option.check.enabled**
* **act.cors.option.check**
* **act.cors.option.check.enabled**

默认值: `true`

当该项配置打开是, ActFramework响应HTTP Option请求并在回应中添加下面的HTTP头:

* access-control-allow-headers
* access-control-expose-headers
* access-control-max-age

**注意**HTTP头`access-control-allow-origin`在任何HTTP请求的响应中都会添加

#### [cors_origin]cors.origin

别名

* **act.cors.origin**

默认值: `*`

This configuration specifies the default `Access-Control-Allow-Origin` header value

#### [cors_headers]cors.headers

别名

* **act.cors.headers**

默认值: `Content-Type, X-HTTP-Method-Override`

This configuration specifies the 默认值 for `Access-Control-Allow-Headers` and `Access-Control-Expose-Headers` headers

#### [cors_headers_expose]cors.headers.expose

别名

* **act.cors.headers.expose**

默认值: `null`

This configuration specifies the 默认值 for `Access-Control-Expose-Headers` header value. If not provided then system will use the value provided by [cors.headers](#cors_headers)

#### [cors_headers_allowed]cors.headers.allowed

别名

* **act.cors.headers.allowed**

默认值: `null`

This configuration specifies the 默认值 for `Access-Control-Allow-Headers` header value. If not provided then system will use the value provided by [cors.headers](#cors_headers)

#### [cors_max_age]cors.max_age

别名

* **act.cors.max_age**

默认值: 30*60 (seconds)

This configuration specifies the 默认值 for `Access-Control-Max-Age` header when [cors](#cors) is enabled

#### [content_suffix_aware]content_suffix.aware

别名

* **content_suffix.aware.enabled**
* **act.content_suffix.aware**
* **act.content_suffix.aware.enabled**

Once enabled then the framework automatically recognize request with content suffix, e.g. `/customer/123/json` or `/customer/123.json` will match the route `/customer/123` and set the request `Accept` header value to `application/json`

**注意** 后缀和有效URL路径之间用`/`分隔

默认值: `false`

#### [csrf]csrf

别名

* **csrf.enabled**
* **act.csrf**
* **act.csrf.enabled**

Turn on/off global [CSRF](https://www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF)_Prevention_Cheat_Sheet) protect

默认值: `false`

Once this configuration is turned on the framework will check all POST/PUT/DELETE request for CSRF token. If it doesn't match then the request will get rejected with 403 Forbidden response

#### [csrf_cookie_name]csrf.cookie_name

Aliases

* **act.csrf.cookie_name**

Specify the name of the cookie used to convey the csrf token generated on the server for the first request coming from a client

Default value: `XSRF-TOKEN`, the name used by AngularJs

#### [csrf_param_name]csrf.param_name

别名

* **act.csrf.param_name**

Set the request parameter name for CSRF token

默认值: `__csrf__`

#### [csrf_header_name]csrf.header_name

别名

* **act.csrf.param_name**

Set the response header name for CSRF token generated from server

默认值: `XSRF-TOKEN`

#### [csrf_protector]csrf.protector

别名

* **csrf.protector.impl**
* **act.csrf.protector**
* **act.csrf.protector.impl**

Set the implementation of `act.security.CSRFProtector`. The value of this configuration could be either a name of the class that implements `act.security.CSRFProtector` interface or the name of the enum defined in `act.security.CSRFProtector.Predefined`.

默认值: `HMAC`

Other options in `act.security.CSRFProtector.Predefined`: `RANDOM`

For differences between `HMAC` and `RANDOM` please checkout http://security.stackexchange.com/questions/52224/csrf-random-value-or-hmac

#### [dsp_token]dsp.token

Aliases

* **act.dsp.token**

Specify the name of "double submission protect token"

Default value: `act_dsp_token`

#### [db_seq_gen]db.seq_gen

别名

* **db.seq_gen.impl**
* **act.db.seq_gen**
* **act.db.seq_gen.impl**

Specify database sequence generator. Which must be class name of the  implementation of `act.db.util._SequenceNumberGenerator`. If not specified then it will return the first `act.db.util._SequenceNumberGenerator` implementation scanned by ActFramework.

#### [encoding]encoding

别名

* **act.encoding**

Specify application default encoding. 默认值 is `UTF-8`. It is highly recommended not to change the default setting.

#### [enum_resolving_case_sensitive]enum.resolving.case_sensitive

别名

* **act.enum.resolving.case_sensitive**

Specifies whether it allow enum resolving for request parameters to ignore case

Default value: `false` meaning enum resolving is case insensitive

#### [fmt_date]fmt.date

别名

* **act.fmt.date**

Specifies the default date format used to parse/output date string. 

默认值: the pattern of `java.text.DateFormat.getDateInstance()`

#### [fmt_date_time]fmt.date_time

别名

* **act.fmt.date_time**

Specifies the default date and time format used to parse/output date time string

默认值: the pattern of `java.text.DateFormat.getDateTimeInstance()`

#### [fmt_time]fmt.time

别名

* **act.fmt.time**

Specifies the default time format used to parse/output time string

默认值: the pattern of `java.text.DateFormat.getTimeInstance()`

#### [handler_csrf_check_failure]handler.csrf_check_failure

Aliases

* **handler.csrf_check_failure.impl**
* **act.handler.csrf_check_failure**
* **act.handler.csrf_check_failure.impl**

Specifies implementation of `act.util.MissingAuthenticationHandler` interface by class name. The implementation is called when [CSRF token](csrf) cannot be verified.

Default value: the setting of [handler.missing_authentication](#handler_missing_authentication)

#### [handler_csrf_check_failure_ajax]handler.csrf_check_failure.ajax

Aliases

* **handler.csrf_check_failure.ajax.impl**
* **act.handler.csrf_check_failure.ajax**
* **act.handler.csrf_check_failure.ajax.impl**

Specifies implementation of `act.util.MissingAuthenticationHandler` interface by class name. The implementation is called when [CSRF token](csrf) cannot be verified on an ajax request

Default value: the setting of [handler.csrf_check_failure](handler_csrf_check_failure)

#### [handler_missing_authentication]handler.missing_authentication

别名

* **handler.missing_authentication.impl**
* **act.handler.missing_authentication**
* **act.handler.missing_authentication.impl**

Specifies implementation of `act.util.MissingAuthenticationHandler` interface by class name. The implementation is called when [CSRF token](csrf) cannot be verified.

默认值: `act.util.RedirectToLoginUrl` which redirect the user to [login URL](url_login)

Other options: `act.util.ReturnUnauthorized` which respond with `401 Unauthorised`

#### [handler_missing_authentication_ajax]handler.missing_authentication.ajax

别名

* **handler.missing_authentication.ajax.impl**
* **act.handler.missing_authentication.ajax**
* **act.handler.missing_authentication.ajax.impl**

Specifies implementation of `act.util.MissingAuthenticationHandler` interface by class name. The implementation is called when [CSRF token](csrf) cannot be verified on ajax request

默认值: the setting of [handler.missing_authentication](handler_missing_authentication_ajax)

#### [handler_unknown_http_method]handler.unknown_http_method

Aliases

* **handler.unknown_http_method.impl**
* **act.handler.unknown_http_method**
* **act.handler.unknown_http_method.impl**

Specifies a class/instance that implements `act.handler.UnknownHttpMethodProcessor` that process the HTTP methods that are not recognized by `act.route.Router`, e.g. "OPTION", "HEAD" etc

#### [host]host

别名

* **act.host**

Specifies the hostname the application listen to

默认值: `localhost`

#### [http_external_server]http.external_server

别名

* **http.external_server.enabled**
* **act.http.external_server**
* **act.http.external_server.enabled**

Specify if the app is running behind a front end http server, e.g. nginx

默认值: `true` when running in `PROD` mode; `false` when running in `DEV` mode

Note act does not listen to external port directly. The recommended pattern is to have a front end HTTP server (e.g. nginx) to handle the external request and forward to act

#### [http_params_max]http.params.max

别名

* **act.http.params.max**

Specifies the maximum number of http parameters. This can be used to prevent the hash collision DOS attack. If this configuration is set to any value larger than 0, ActFramework will check the request parameter number, if the number is larger than the setting, then a `413 Request Entity Too Large` response is returned immediately

默认值: `128`

#### [http_port]http.port

别名

* **act.http.port**

Specifies the default http port the application listen to.

默认值: `5460`

#### [http.port.external]http.port.external

别名

* **act.http.port.external**

Specifies the external port which is used to construct the full URL

默认值: `80`

#### [http.port.external.secure]http.port.external.secure

Specifies the external secure port which is used to construct the full URL when app is running on secure channel

Default value: 443

#### [http_secure_enabled]http.secure.enabled

别名 

* **http.secure**
* **act.http.secure**
* **act.http.secure.enabled**

Specifies whether the default http port is listening on secure channel or not.

默认值: `false` when app is running in `DEV` mode, `true` if app is running in `RPOD` mode

#### [i18n]i18n

别名

* **i18n.enabled**
* **act.i18n**
* **act.i18n.enabled**

Turn on/off i18n support in ActFramework application

默认值: `false`

#### [i18n_locale_param_name]i18n.locale.param_name

别名

* **act.i18n.locale.param_name**

Specify the param name to set client locale in http request

默认值: `act_locale`

#### [i18n_locale_cookie_name]i18n.locale.cookie_name

别名

* **act.i18n.locale.cookie_name**

Specify the name for the locale cookie

默认值: `act_locale`

#### [idgen_node_id_provider]idgen.node_id.provider

别名

* **idgen.node_id.provider.impl**
* **act.idgen.node_id.provider**
* **act.idgen.node_id.provider.impl**

Specify the `act.util.IdGenerator.NodeIdProvider` implementation by class name. The node id provider is responsible to generate the node id for a CUID (Cluster Unique Identifer). When not specified, then Act will use the `IdGenerator.NodeIdProvider.IpProvider` that return the node id calculated from the node's ip address based on [effective ip bytes](#idgen_node_id_effective_ip_bytes_size) configuration

默认值: `act.util.IdGenerator.NodeIdProvider.IpProvider`

#### [idgen_node_id_effective_ip_bytes_size]idgen.node_id.effective_ip_bytes.size

别名

* **idgen.node_id.effective_ip_bytes**
* **act.idgen.node_id.effective_ip_bytes**
* **act.idgen.node_id.effective_ip_bytes.size**

Specifies how many bytes in the ip address will be used to calculate node ID. Usually in a cluster environment, the ip address will be different at only (last) one byte or (last) two bytes, in which case it could set this configuration to `1` or `2`. When the configuration is set to `4` then it means all 4 IP bytes will be used to calculate the node ID.

Note the bigger this number is, the longer the CUID will be. However it should be enough to distinct the application nodes inside a cluster.

默认值: `4`

#### [idgen_start_id_provider]idgen.start_id.provider

别名

* **idgen.start_id.provider.impl**
* **act.idgen.start_id.provider**
* **act.idgen.start_id.provider.impl**

Specifies the `act.util.IdGenerator.StartIdProvider` implementation by class name. This provider generate the start ID part of a CUID.

默认值: `act.util.IdGenerator.StartIdProvider.DefaultStartIdProvider`

The default provider will get the ID from [predefined file](#idgen_start_id_file), or if file IO is not allowed, it will use the timestamp.

#### [idgen_start_id_file]idgen.start_id.file

别名

* **act.idgen.start_id.file**

Specifies the start id persistent file for start ID counter.

默认值: `.act.id-app`

#### [idgen_seq_id_provider]idgen.seq_id.provider

别名

* **idgen.seq_id.provider.impl**
* **act.idgen.seq_id.provider**
* **act.idgen.seq_id.provider.impl**

Specifies the impelementation of `act.util.IdGenerator.SequenceProvider` by class name, which will be used to generate the sequence part of CUID.

默认值: `act.util.IdGenerator.SequenceProvider.AtomicLongSeq`

#### [idgen_encoder]idgen.encoder

别名

* **idgen.encoder.impl**
* **act.idgen.encoder**
* **act.idgen.encoder.impl**

Specifies an implementation of `act.util.IdGenerator.LongEncoder` interface by class name. The instance will be used to encode long value (the three parts of CUID generated) into a String.

Available options:

* `act.util.IdGenerator.UnsafeLongEncoder` - maximum compression ratio, might generate URL unsafe characters
* `act.util.IdGenerator.SafeLongEncoder` - relevant good compression ratio without URL unsafe characters

默认值: `act.util.IdGenerator.SafeLongEncoder`

#### [job_pool_size]job.pool.size

别名

* **job.pool**
* **act.job.pool**
* **act.job.pool.size**

Specifies the maximum number of threads can exists in the application's job manager's thread pool

默认值: `10`

#### [locale]locale

别名

* **act.locale**

Specifies the application default locale.

默认值: `java.util.Locale#getDefault`

#### [metric]metric

Aliases

* **metric.enabled**
* **act.metric**
* **act.metric.enabled**

Turn on/off metric in Act application

Default value: `true`

#### [modules]modules

别名

* **act.modules**

Declare additional app base (for maven modules)

默认值: `null`

#### [namedPorts]namedPorts

别名

* **act.namedPorts**

specifies a list of port names this application listen to. These are additional ports other than the default [http.port](#http_port)

The list is specified as

```
act.namedPorts=admin:8888;ipc:8899
```

默认值: `null`

Note, the default port that specified in [http.port](#http_port) configuration and shall not be specified in this namedPorts configuration

#### [ping_path]ping.path

别名

* **act.ping.path**

Specify the ping path. If this setting is specified, then when session resolving, system will check if the current URL matches the setting. If matched then session cookie expiration time will not be changed. Otherwise the expiration time will refresh

默认值: `null`

#### [profile]profile

别名

* **act.profile**

Specifies the profile to load configuration If this setting is specified, and there is a folder named as the `profile` setting sit under `/resource/conf` folder, then the properties files will be loaded from that folder.

默认值: the value of the {@link Act#mode()}

Note, unlike other configuration items which is usually specified in the configuration file. `profile` setting is load by `System#getProperty(String)`, thus it is usually specified with JVM argument `Dprofile=<profile>`

#### [resolver_error_template_path_impl]resolver.error_template_path.impl

别名

* **resolver.error_template_path**
* **act.resolver.error_template_path**
* **act.resolver.error_template_path.impl**

Specifies error page (template) path resolver implementation by class name

默认值: `act.util.ErrorTemplatePathResolver.DefaultErrorTemplatePathResolver`

#### [resolver_template_path_impl]resolver.template_path.impl

别名

* **resolver.template_path**
* **act.resolver.template_path**
* **resolver.template_path.impl**

specifies the class that is type of `act.view.TemplatePathResolver`. Application developer could use this configuration to add some flexibility to template path resolving logic, e.g. different home for different locale or different home for different device type etc.

默认值: `act.view.TemplatePathResolver`

#### [resource_preload_size_limit]resource.preload.size.limit

Aliases

* **act.resource.preload.size.limit**

Specifies the maximum number of bytes of a resource that can be preload into memory. Specifies `0` or negative number to disable resource preload feature

Default value: `1024 * 10`, i.e. 10KB

#### [scan_package]scan_package

别名

* **act.scan_package**

Specify the app package in which all classes is subject to bytecode processing, e.g enhancement and injection. This setting should be specified when application loaded. Otherwise Act will try to process all classes found in application's lib and classes folder, which might cause performance issue on startup 

#### [secret]secret

别名

* **act.secret**

Specifies the secret key the application used to do general encrypt/decrypt/sign etc

默认值: `myawesomeapp`

Note, make sure you set this value on PROD mode

#### [server_header]server.header

Aliases

* **act.server.header**

Specifies the server header to be output to the response

Default value: `act`

#### [session_ttl]session.ttl

别名

* **act.session.ttl**

specifies the session duration in seconds. If user failed to interact with server for amount of time that exceeds the setting then the session will be destroyed

默认值: `60 * 30` i.e half an hour

#### [session_persistent]session.persistent

别名

* **session.persistent.enabled**
* **act.session.persistent**
* **act.session.persistent.enabled**

Specify whether the system should treat session cookie as [persistent cookie](http://en.wikipedia.org/wiki/HTTP_cookie#Persistent_cookie). If this setting is enabled, then the user's session will not be destroyed after browser closed. 

默认值: `false`

#### [session_encrypt]session.encrypt

别名

* **session.encrypt.enabled**
* **act.session.encrypt**
* **act.session.encrypt.enabled**

{@code session.encrypted.enabled} specify whether the system should encrypt the key/value pairs in the session cookie. Enable session encryption will greatly improve the security but with the cost of additional CPU usage and a little bit longer time on request processing. 

默认值: `false`

#### [session_key_username]session.key.username

别名

* **act.session.key.username**

Specifies the session key for username of the login user. Authentication plugin shall use the session key configured to access the username.

默认值: `username`

#### [session_mapper_impl]session.mapper.impl

别名

* **session.mapper**
* **act.session.mapper**
* **act.session.mapper.impl**

Specify the implementation of `act.util.SessionMapper` by class name. A session mapper can be used to serialize session/flash to response or on the flippering side, deserialize session/flash info from request.

Default value: `act.util.SessionMapper.DefaultSessionMapper`, use cookie to serialize/deserizalize session

#### [session_secure]session.secure

别名

* **session.secure.enabled**
* **act.session.secure**
* **act.session.secure.enabled**

specifies whether the session cookie should be set as secure. Enable secure session will cause session cookie only effective in https connection. Literally this will enforce the web site to run default by https.

默认值: the setting of [http.secure](http_secure_enabled)

**注意** when Act server is running in DEV mode session http only will be disabled without regarding to the `session.secure.enabled` setting

#### [source_version]source.version

别名

* **act.source.version**

Specifies the Java source version. This configuration has impact only when app is running in DEV mode

默认值: `1.7`

Note ActFramework support Java 1.7+. Make sure you do NOT put in `1.6` or below here.

#### [source_version]source.version

别名

* **act.source.version**

Specifies the Java source version. This configuration has impact only when app is running in DEV mode

默认值: `1.7`

Note ActFramework support Java 1.7+. Make sure you do NOT put in `1.6` or below here.

#### [target_version]target.version

别名

* **act.target.version**

Specifies the Java target version. This configuration has impact only when app is running in DEV mode

默认值: `1.7`

Note ActFramework support Java 1.7+. Make sure you do NOT put in `1.6` or below here.

#### [template_home]template.home

别名

* **act.template.home**

Specifies where the view templates resides. If not specified then will use the view engine name (in lowercase) as the template home.

**注意** it is highly recommended NOT to set this configuration item

#### [url_login]url.login

别名

* **act.url.login**

Specifies the login URL which is used by `act.util.RedirectToLoginUrl`, the default implementation of `MissingAuthenticationHandler`, see [handler.missing_authentication.impl]
(handler_missing_authentication_impl)

默认值: `/login`

#### [url_login_ajax]url.login.ajax

别名

* **act.url.login.ajax**

Specifies the login URL which is used by `act.util.RedirectToLoginUrl`, the default implementation of `MissingAuthenticationHandler` when answering ajax request. See [handler.missing_authentication.ajax.impl](handler_missing_authentication_ajax_impl)

#### [view_default]view.default

别名

* **act.view.default**

Specifies the default view engine name. If there are multiple views registered and default view are available, then it will be used at priority when loading the templates

默认值: `rythm` see [Rythm Engine](http://rythmengine.org)

Other options:

* `freemarker` - need [act-freemarker](https://github.com/actframework/act-freemarker) plugin
* `velocity` - need [act-velocity](https://github.com/actframework/act-velocity) plugin
* `mustache` - need [act-mustache](https://github.com/actframework/act-mustache) plugin
* `thymeleaf` - need [act-thymeleaf](https://github.com/actframework/act-thymeleaf) plugin
* `beetl` - need [act-beetl](https://github.com/actframework/act-beetl) plugin
