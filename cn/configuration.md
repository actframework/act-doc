<h1 data-book="configuration">配置</h1>

本章节文档包含 ActFramework 各配置项的详细描述

#### [basic_authentication]basic_authentication.enabled

别名

* **basic_authentication**
* **act.basic_authentication**
* **act.basic_authentication.enabled**

在 ActFramework 应用中开启或关闭 [Basic Authentication](https://en.wikipedia.org/wiki/Basic_access_authentication) 功能.

默认值: `false`

**注意** 在 ActFramework 核心模块中并不包含该配置的具体逻辑. 由安全插件，如 `act-aaa-plugin` 使用此配置项的值.

#### [cache_impl]cache.impl

别名

* **cache**
* **act.cache**
* **act.cache.impl**

指定缓存服务的实现. 配置必须是名称为 `org.osgl.cache.CacheServiceProvider` 的实现类.

默认值: `Auto`, i.e. `org.osgl.cache.CacheServiceProvider.Impl.Auto`. 该实现将根据缓存的提供者使用以下顺序自动选择:

1. 检查是否 `MemcachedServiceProvider` 能够被实例化, 如果不能则继续
2. 检查是否 `EhCacheServiceProvider` 能够被实例化, 如果不能则继续
3. 载入 `SimpleCacheServiceProvider` 实例，这将使用内存来实现缓存服务

#### [cache_name]cache.name

别名

* **act.cache.name**

指定默认用于应用程序的缓存名称.

默认值: `_act_app_`

#### [cache_name_session]cache.name.session

别名

* **act.cache.name.session**

指定用于的应用程序的缓存会话的名称.

默认值: 配置项 [cache.name](#cache_name) 的值

#### [cli_page_size_json]cli.page.size.json

别名

* **act.cli.page.size.json**

指定 CLI 命令行模式下 JSON 格式一页能显示的最大数据条目.

默认值: 10

#### [cli_page_size_table]cli.page.size.table

别名

* **act.cli.page.size.table**

指定 CLI 命令行模式下 Table 布局一页能显示的最大数据条目.

默认值: 22


#### [cli_port]cli.port

别名

* **act.cli.port**

设置 CLI telnet 端口.

默认值: `5461`

#### [cli_session_expiration]cli.session.expiration

别名

* **act.cli.session.expiration**

指定 CLI 会话的在最后一次用户交互后的保持时间，单位: 秒.

默认值: `300`, i.e. 5 minutes

### [cli_session_max]cli.session.max

别名

* **act.cli.session.max**

指定 CLI 会话线程可以同时存在的最大数量.

默认值: `3`

#### [cli_over_http_enabled]cli_over_http.enabled

别名

* **cli_over_http**
* **act.cli_over_http**
* **act.cli_over_http.enabled**

启用或关闭 CLI 的 HTTP 支持.

默认值: `false`

一旦启用 CLI 的 HTTP 支持, 它将允许管理员通过 HTTP 的 [configured port](#cli_over_http.port) 端口来执行 CLI 命令.

#### [cli_over_http_authority_impl]cli_over_http.authority.impl

别名

* **cli_over_http.authority**
* **act.cli_over_http.authority**
* **act.cli_over_http.authority.impl**

为 CL I配置通过 http 访问的授权提供者. 指定的值应该是一个类名为 `act.cli.CliOverHttpAuthority` 的实现.

默认值: `CliOverHttpAuthority.AllowAll` 默认允许发送任何请求.

#### [cli_over_http_port]cli_over_http.port

别名

* **act.cli_over_http.port**

设置 CLI 通过 HTTP 服务的访问的端口.

默认值: `5462`

#### [cli_over_http_title]cli_over_http.title

别名

* **act.cli_over_http.title**

指定要显示在 CLI 访问的 Http 页面的标题.

默认值: `Cli Over Http`

#### [cli_over_http_syscmd_enabled]cli_over_http.syscmd.enabled

别名

* **cli_over_http.syscmd**
* **act.cli_over_http.syscmd**
* **act.cli_over_http.syscmd.enabled**

开启或关闭通过 HTTP 的 CLI 访问系统命令.

默认值: `true`

#### [cookie_domain_provider]cookie.domain_provider

别名

* **act.cookie.domain_provider**

指定返回的域名的提供者. 当没有指定时, 它将总是返回配置在 [host](#host) 的值.

合法的配置参数:

1. `javax.inject.Provider` 类的实现类的类名，将返回一个 `String` 类型的值.

2. `dynamic` 或 `flexible` 或 `contextual`, 均表示域名将从当前域请求中取值.

默认值: `null`

#### [cors]cors.enabled

别名

* **cors**
* **act.cors**
* **act.cors.enabled**
开启或关闭 ActFramework 应用的 [CORS](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing) 支持.

默认值: `false`

一旦 `cors`被开启, ActFramework 将默认在响应中自动添加下面列表指定的 HTTP 头. 当此配置开启, ActFramework 同时也将在请求中创建 HTTP OPTION.

#### [cors_option_check]cors.option.check.enabled

别名

* **cors.option.check**
* **act.cors.option.check**
* **act.cors.option.check.enabled**

默认值: `true`

启用此配置时, ActFramework 将仅向 HTTP OPTION 请求添加以下 CORS 相关标头:

* access-control-allow-headers
* access-control-expose-headers
* access-control-max-age

当启用 [cors](#cors) 配置时, 无论 HTTP 请求的方法，总是会添加 `access-control-allow-origin` 到 HTTP 头.

#### [cors_origin]cors.origin

别名

* **act.cors.origin**

默认值: `*`

此配置指定默认的 `Access-Control-Allow-Origin` 标头值.

#### [cors_headers]cors.headers

别名

* **act.cors.headers**

默认值: `Content-Type, X-HTTP-Method-Override`

此配置指定 `Access-Control-Allow-Headers` 和 `Access-Control-Expose-Headers` 标头的默认值.

#### [cors_headers_expose]cors.headers.expose

别名

* **act.cors.headers.expose**

默认值: `null`

此配置指定 `Access-Control-Expose-Headers` 标头的默认值。 如果没有提供，那么系统将使用 [cors.headers](#cors_headers) 提供的值.

#### [cors_headers_allowed]cors.headers.allowed

别名

* **act.cors.headers.allowed**

默认值: `null`

此配置指定 `Access-Control-Allow-Headers` 标头的默认值。 如果没有提供，那么系统将使用 [cors.headers](#cors_headers) 提供的值.

#### [cors_max_age]cors.max_age

别名

* **act.cors.max_age**

默认值: 30*60 (seconds)

当启用 [cors](#cors) 时, 此配置指定 `Access-Control-Max-Age` 标头的默认值.

#### [content_suffix_aware_enabled]content_suffix.aware.enabled

别名

* **content_suffix.aware**
* **act.content_suffix.aware**
* **act.content_suffix.aware.enabled**

启用此配置项，框架将自动识别具有内容后缀的请求，例如 `/customer/123/json` 或 `/customer/123.json`, 将匹配路径 `/customer/123`, 并将请求 `Accept` 头的值设置为 `application/json`.

默认值: `false`

#### [csrf]csrf.enabled

别名

* **csrf**
* **act.csrf**
* **act.csrf.enabled**

开启/关闭全局 [CSRF](https://www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF)_Prevention_Cheat_Sheet) 保护.

默认值: `false`

此配置开启，框架将检查所有 POST/PUT/DELETE 请求的 CSRF 令牌. 如果它不匹配, 则该请求将返回 403 Forbidden 的响应.

#### [csrf_param_name]csrf.param_name

别名

* **act.csrf.param_name**

设置 CSRF 令牌的请求参数名称.

默认值: `__csrf__`

#### [csrf_header_name]csrf.header_name

别名

* **act.csrf.param_name**

设置从服务端生成的 CSRF 令牌的响应头的名称. 

默认值: `XSRF-TOKEN`

#### [csrf_protector]csrf.protector.impl

别名

* **csrf.protector**
* **act.csrf.protector**
* **act.csrf.protector.impl**

设置 `act.security.CSRFProtector` 的实现. 此配置的值可以是 `act.security.CSRFProtector` 接口的类的实现, 也可以是 `act.security.CSRFProtector.Predefined` 中定义的枚举的名称.

默认值: `HMAC`

`act.security.CSRFProtector.Predefined` 中的其他选项: `RANDOM`

对于 `HMAC` 和 `RANDOM` 之间的区别, 请查阅 http://security.stackexchange.com/questions/52224/csrf-random-value-or-hmac

#### [db_seq_gen_impl]db.seq_gen.impl

别名

* **db.seq_gen**
* **act.db.seq_gen**
* **act.db.seq_gen.impl**

指定数据库序列生成器. 它必须是 `act.db.util._SequenceNumberGenerator` 的实现类. 如果没有指定，那么它将返回 ActFramework 扫描的第一个 `act.db.util._SequenceNumberGenerator` 实现.

#### [encoding]encoding

别名

* **act.encoding**

指定应用程序默认编码. 默认值为 `UTF-8`. 强烈建议不要更改默认设置.

#### [fmt_date]fmt.date

别名

* **act.fmt.date**

指定用于解析/输出日期字符串的默认日期格式.

默认值: `java.text.DateFormat.getDateInstance()` 的格式

#### [fmt_date_time]fmt.date_time

别名

* **act.fmt.date_time**

指定用于解析/输出日期和时间字符串的默认日期和时间格式.

默认值: `java.text.DateFormat.getDateTimeInstance()` 的格式

#### [fmt_time]fmt.time

别名

* **act.fmt.time**

指定用于解析/输出时间字符串的默认时间格式.

默认值: `java.text.DateFormat.getTimeInstance()` 的格式

#### [handler_missing_authentication_impl]handler.missing_authentication.impl

别名

* **handler.missing_authentication**
* **act.handler.missing_authentication**
* **act.handler.missing_authentication.impl**

通过类名指定 `act.util.MissingAuthenticationHandler` 接口的实现。 当 [CSRF token](csrf) 无法验证时调用实现.

默认值: `act.util.RedirectToLoginUrl` 它将重定向到用户的 [login URL](url_login)

其它选项: `act.util.ReturnUnauthorized` 它将返回 `401 Unauthorised` 响应

#### [handler_missing_authentication_ajax_impl]handler.missing_authentication.ajax.impl

别名

* **handler.missing_authentication.ajax**
* **act.handler.missing_authentication.ajax**
* **act.handler.missing_authentication.ajax.impl**

通过类名指定 `act.util.MissingAuthenticationHandler` 接口的实现. 当无法对 ajax 请求验证 [CSRF token](csrf) 时调用实现.

默认值: [handler.missing_authentication.impl](handler_missing_authentication_ajax_impl) 的配置

#### [host]host

别名

* **act.host**

指定应用程序侦听的主机名.

默认值: `localhost`

#### [http.external_server.enabled]http.external_server.enabled

别名

* **http.external_server**
* **act.http.external_server**
* **act.http.external_server.enabled**

指定应用程序是否在运行在前端 http 服务器, 例如 nginx.

默认值: 当运行在 `PROD` 模式下默认是 `true`; 当运行在 `DEV` 模式下默认是 `false`.

注意 ACT 不会直接侦听外部端口. 推荐的模式是使用前端 HTTP 服务器（例如 nginx）来处理外部请求并转发到 ACT.

#### [http.port.external]http.port.external

别名

* **act.http.port.external**

指定用于构造完整 URL 的外部端口.

默认值: `80`

#### [http.port.external.secure]http.port.external.secure

指定外部加密端口, 用于应用在安全通道上运行时构造完整的 URL.

#### [http_params_max]http.params.max

别名

* **act.http.params.max**

指定 http 参数的最大值. 这可以用来防止哈希冲突的 DOS 攻击. 如果此配置设置为任何大于 0 的值, ActFramework 将检查请求参数数目, 如果该数量大于该设置, 则立即返回 `413 Request Entity Too Large` 响应.

默认值: `1000`

#### [http_port]http.port

别名

* **act.http.port**

指定应用程序侦听的默认 http 端口.

默认值: `5460`

#### [http_secure_enabled]http.secure.enabled

别名 

* **http.secure**
* **act.http.secure**
* **act.http.secure.enabled**

指定默认 http 端口是否正在侦听加密通道.

默认值: 当应用运行在 `DEV` 模式下默认为 `false`, 当应用运行在 `RPOD` 模式下默认为 `true`.

#### [i18n_enabled]i18n.enabled

别名

* **i18n**
* **act.i18n**
* **act.i18n.enabled**

在 ActFramework 应用程序中打开/关闭 i18n 支持.

默认值: `false`

#### [i18n_locale_param_name]i18n.locale.param_name

别名

* **act.i18n.locale.param_name**

指定参数名称以在 http 请求中设置客户端区域设置.

默认值: `act_locale`

#### [i18n_locale_cookie_name]i18n.locale.cookie_name

别名

* **act.i18n.locale.cookie_name**

指定本地保存的 cookie 名称.

默认值: `act_locale`

#### [idgen_node_id_provider_impl]idgen.node_id.provider.impl

别名

* **idgen.node_id.provider**
* **act.idgen.node_id.provider**
* **act.idgen.node_id.provider.impl**

按类名指定 `act.util.IdGenerator.NodeIdProvider` 实现. 节点 id 提供者负责生成 CUID (簇唯一标识符) 的节点 id. 当没有指定时，Act将使用 `IdGenerator.NodeIdProvider.IpProvider` 返回根据节点的 IP 地址  [effective ip bytes](#idgen_node_id_effective_ip_bytes_size) 配置计算出的节点 id.

默认值: `null`

#### [idgen_node_id_effective_ip_bytes_size]idgen.node_id.effective_ip_bytes.size

别名

* **idgen.node_id.effective_ip_bytes**
* **act.idgen.node_id.effective_ip_bytes**
* **act.idgen.node_id.effective_ip_bytes.size**

指定 IP 地址中将使用多少字节来计算节点 ID. 通常在群集环境中, IP 地址将仅在（最后）一个字节段或（最后）两个字节段不同，在这种情况下，它可以将此配置设置为 `1` 或 `2`. 当配置设置为 `4` 时，表示所有 4 个 IP 字节段将用于计算节点 ID.

注意, 这个数字越大, CUID 就越长. 但是, 应该足以区分集群中的应用程序节点. 

默认值: `4`

#### [idgen_start_id_provider_impl]idgen.start_id.provider.impl

别名

* **idgen.start_id.provider**
* **act.idgen.start_id.provider**
* **act.idgen.start_id.provider.impl**

通过类名指定 `act.util.IdGenerator.StartIdProvider` 实现。 此提供程序生成 CUID 的开始 I D部分.

默认值: `act.util.IdGenerator.StartIdProvider.DefaultStartIdProvider`

默认提供程序将从 [predefined file](#idgen_start_id_file) 获取ID, 或者如果不允许文件 IO, 它将使用时间戳.

#### [idgen_start_id_file]idgen.start_id.file

别名

* **act.idgen.start_id.file**

Specifies the start id persistent file for start ID counter.

默认值: `.act.id-app`

#### [idgen_seq_id_provider_impl]idgen.seq_id.provider.impl

别名

* **idgen.seq_id.provider**
* **act.idgen.seq_id.provider**
* **act.idgen.seq_id.provider.impl**

Specifies the impelementation of `act.util.IdGenerator.SequenceProvider` by class name, which will be used to generate the sequence part of CUID.

默认值: `act.util.IdGenerator.SequenceProvider.AtomicLongSeq`

#### [idgen_encoder_impl]idgen.encoder.impl

别名

* **idgen.encoder**
* **act.idgen.encoder**
* **act.idgen.encoder.impl**

Specifies an implementation of `act.util.IdGenerator.LongEncoder` interface by class name. The instance will be used to encode long value (the three parts of CUID generated) into a String.

Available options:

* `act.util.IdGenerator.UnsafeLongEncoder` - maximum compression ratio, might generate URL unsafe characters
* `act.util.IdGenerator.SafeLongEncoder` - relevant good compression ratio without URL unsafe characters

默认值: `act.util.IdGenerator.SafeLongEncoder`

#### [locale]locale

别名

* **act.locale**

Specifies the application default locale.

默认值: `java.util.Locale#getDefault`

#### [job_pool_size]job.pool.size

别名

* **job.pool**
* **act.job.pool**
* **act.job.pool.size**

Specifies the maximum number of threads can exists in the application's job manager's thread pool

默认值: `10`

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

#### [session_prefix]session.prefix

别名

* **act.session.prefix**

Specifies the prefix to be prepended to the session cookie name. Let's say the default cookie name is ｀act_session｀, and user specifies the prefix ｀my_app｀
then the session cookie name will be ｀my_app_session｀

Note this setting also impact the ｀AppConfig#flashCookieName()｀

默认值: `act`

#### [session_ttl]session.ttl

别名

* **act.session.ttl**

specifies the session duration in seconds. If user failed to interact with server for amount of time that exceeds the setting then the session will be destroyed

默认值: `60 * 30` i.e half an hour

#### [session_persistent_enabled]session.persistent.enabled

别名

* **session.persistent**
* **act.session.persistent**
* **act.session.persistent.enabled**

Specify whether the system should treat session cookie as [persistent cookie](http://en.wikipedia.org/wiki/HTTP_cookie#Persistent_cookie). If this setting is enabled, then the user's session will not be destroyed after browser closed. 

默认值: `false`

#### [session_encrypt_enabled]session.encrypt.enabled

别名

* **session.encrypt**
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

#### [session_secure_enabled]session.secure.enabled

别名

* **session.secure**
* **act.session.secure**
* **act.session.secure.enabled**

specifies whether the session cookie should be set as secure. Enable secure session will cause session cookie only effective in https connection. Literally this will enforce the web site to run default by https.

默认值: the setting of [http.secure](http_secure_enabled)

**Note** when Act server is running in DEV mode session http only will be disabled without regarding to the `session.secure.enabled` setting

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

**Note** it is highly recommended NOT to set this configuration item

#### [unknown_http_method_handler_impl]unknown_http_method_handler.impl

别名

* **unknown_http_method_handler**
* **act.unknown_http_method_handler**
* **act.unknown_http_method_handler.impl**

Specifies a class/instance that implements `act.handler.UnknownHttpMethodProcessor` that process the HTTP methods that are not recognized by `act.route.Router`, e.g. "OPTION", "PATCH" etc

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
