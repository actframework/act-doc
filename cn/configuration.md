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

#### [cookie_prefix]cookie.prefix

别名

* **act.cookie.prefix**

指定要添加到 ActFramework 中使用的 cookie 名称的前缀, 例如 session, flash, xsrf 等等. 假设默认 cookie 名称是 `act_session`, 用户指定前缀 `my_app`, 会话 cookie 名称将是 `my_app_session`

注意, 这个设置也会影响 `AppConfig＃flashCookieName()`

默认值: 根据以下逻辑计算:

1. 找到应用程序的名称，如果没有找到，然后使用 `act` 作为应用程序名称
2. 用空格分割应用程序名称
3. 检查分割字符串数组的长度
3.1 如果数组中只有一个字符串, 则返回字符串的前 3 个字符, 如果字符串长度超过 3, 则返回字符串
3.2 如果数组中有两个字符串，则拾取每个字符串的前 2 个字符, 并通过破折号 `-`
3.3 拾取数组中前 3 个字符串的第一个字符

例如

当应用程序名称是 `HelloWorld` 时, cookie 前缀是 `hel-`
当应用程序名称是 `HelloWorld` 时, cookie 前缀是 `he-wo-`
当应用程序名称是 `HelloWorld` 时, cookie 前缀是 `hmw-`

#### [cors]cors

别名

* **cors.enabled**
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

#### [content_suffix_aware]content_suffix.aware

别名

* **content_suffix.aware.enabled**
* **act.content_suffix.aware**
* **act.content_suffix.aware.enabled**

启用此配置项，框架将自动识别具有内容后缀的请求，例如 `/customer/123/json` 或 `/customer/123.json`, 将匹配路径 `/customer/123`, 并将请求 `Accept` 头的值设置为 `application/json`.

默认值: `false`

**注意** 后缀和有效URL路径之间用`/`分隔

#### [csrf]csrf.enabled

别名

* **csrf.enabled**
* **act.csrf**
* **act.csrf.enabled**

开启/关闭全局 [CSRF](https://www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF)_Prevention_Cheat_Sheet) 保护.

默认值: `false`

此配置开启，框架将检查所有 POST/PUT/DELETE 请求的 CSRF 令牌. 如果它不匹配, 则该请求将返回 403 Forbidden 的响应.

#### [csrf_cookie_name]csrf.cookie_name

别名

* **act.csrf.cookie_name**

指定 cookie 的名称, 用于服务端为来自客户端的首次请求生成的 csrf 令牌.
Specify the name of the cookie used to convey the csrf token generated on the server for the first request coming from a client

默认值: `XSRF-TOKEN`, AngularJs 使用的名称

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

#### [csrf_protector]csrf.protector

别名

* **csrf.protector.impl**
* **act.csrf.protector**
* **act.csrf.protector.impl**

设置 `act.security.CSRFProtector` 的实现. 此配置的值可以是 `act.security.CSRFProtector` 接口的类的实现, 也可以是 `act.security.CSRFProtector.Predefined` 中定义的枚举的名称.

默认值: `HMAC`

`act.security.CSRFProtector.Predefined` 中的其他选项: `RANDOM`

对于 `HMAC` 和 `RANDOM` 之间的区别, 请查阅 http://security.stackexchange.com/questions/52224/csrf-random-value-or-hmac

#### [dsp_token]dsp.token

别名

* **act.dsp.token**

指定 “双提交保护令牌” 的名称

默认值: `act_dsp_token`


#### [db_seq_gen_impl]db.seq_gen

别名

* **db.seq_gen.impl**
* **act.db.seq_gen**
* **act.db.seq_gen.impl**

指定数据库序列生成器. 它必须是 `act.db.util._SequenceNumberGenerator` 的实现类. 如果没有指定，那么它将返回 ActFramework 扫描的第一个 `act.db.util._SequenceNumberGenerator` 实现.

#### [encoding]encoding

别名

* **act.encoding**

指定应用程序默认编码. 默认值为 `UTF-8`. 强烈建议不要更改默认设置.

#### [enum_resolving_case_sensitive]enum.resolving.case_sensitive

别名

* **act.enum.resolving.case_sensitive**

指定它是否允许枚举解析请求参数忽略大小写.

默认值: `false` 意味着枚举解析是不区分大小写的.

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

#### [handler_csrf_check_failure]handler.csrf_check_failure

别名
 
* **handler.csrf_check_failure.impl**
* **act.handler.csrf_check_failure**
* **act.handler.csrf_check_failure.impl**
 
通过类名指定 `act.util.MissingAuthenticationHandler` 接口的实现. 当 [CSRF token](csrf) 无法验证时调用实现.

默认值: [handler.missing_authentication](#handler_missing_authentication) 的配置

#### [handler_csrf_check_failure_ajax]handler.csrf_check_failure.ajax

别名

* **handler.csrf_check_failure.ajax.impl**
* **act.handler.csrf_check_failure.ajax**
* **act.handler.csrf_check_failure.ajax.impl**

通过类名指定 `act.util.MissingAuthenticationHandler `接口的实现. 当无法在 ajax 请求上验证 [CSRF token](csrf) 时调用实现.

默认值: [handler.csrf_check_failure](handler_csrf_check_failure) 的配置

#### [handler_missing_authentication_impl]handler.missing_authentication

别名

* **handler.missing_authentication.impl**
* **act.handler.missing_authentication**
* **act.handler.missing_authentication.impl**

通过类名指定 `act.util.MissingAuthenticationHandler` 接口的实现。 当 [CSRF token](csrf) 无法验证时调用实现.

默认值: `act.util.RedirectToLoginUrl` 它将重定向到用户的 [login URL](url_login)

其它选项: `act.util.ReturnUnauthorized` 它将返回 `401 Unauthorised` 响应

#### [handler_missing_authentication_ajax_impl]handler.missing_authentication.ajax

别名

* **handler.missing_authentication.ajax.impl**
* **act.handler.missing_authentication.ajax**
* **act.handler.missing_authentication.ajax.impl**

通过类名指定 `act.util.MissingAuthenticationHandler` 接口的实现. 当无法对 ajax 请求验证 [CSRF token](csrf) 时调用实现.

默认值: [handler.missing_authentication.impl](handler_missing_authentication_ajax_impl) 的配置

#### [handler_unknown_http_method]handler.unknown_http_method

别名

* **handler.unknown_http_method.impl**
* **act.handler.unknown_http_method**
* **act.handler.unknown_http_method.impl**

指定实现 `act.handler.UnknownHttpMethodProcessor` 的类/实例，它处理 `act.route.Router` 不能识别的HTTP方法. 例如: "OPTION", "HEAD" 等.

#### [host]host

别名

* **act.host**

指定应用程序侦听的主机名.

默认值: `localhost`

#### [http.external_server.enabled]http.external_server

别名

* **http.external_server.enabled**
* **act.http.external_server**
* **act.http.external_server.enabled**

指定应用程序是否在运行在前端 http 服务器, 例如 nginx.

默认值: 当运行在 `PROD` 模式下默认是 `true`; 当运行在 `DEV` 模式下默认是 `false`.

注意 ACT 不会直接侦听外部端口. 推荐的模式是使用前端 HTTP 服务器（例如 nginx）来处理外部请求并转发到 ACT.

#### [http.port.external.secure]http.port.external.secure

指定外部加密端口, 用于应用在安全通道上运行时构造完整的 URL.

#### [http_params_max]http.params.max

别名

* **act.http.params.max**

指定 http 参数的最大值. 这可以用来防止哈希冲突的 DOS 攻击. 如果此配置设置为任何大于 0 的值, ActFramework 将检查请求参数数目, 如果该数量大于该设置, 则立即返回 `413 Request Entity Too Large` 响应.

默认值: `128`

#### [http_port]http.port

别名

* **act.http.port**

指定应用程序侦听的默认 http 端口.

默认值: `5460`

#### [http.port.external]http.port.external

别名

* **act.http.port.external**

指定用于构造完整 URL 的外部端口.

默认值: `80`

#### [http_secure_enabled]http.secure.enabled

别名 

* **http.secure**
* **act.http.secure**
* **act.http.secure.enabled**

指定默认 http 端口是否正在侦听加密通道.

默认值: 当应用运行在 `DEV` 模式下默认为 `false`, 当应用运行在 `RPOD` 模式下默认为 `true`.

#### [i18n_enabled]i18n

别名

* **i18n.enabled**
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

#### [idgen_node_id_provider_impl]idgen.node_id.provider

别名

* **idgen.node_id.provider.impl**
* **act.idgen.node_id.provider**
* **act.idgen.node_id.provider.impl**

按类名指定 `act.util.IdGenerator.NodeIdProvider` 实现. 节点 id 提供者负责生成 CUID (簇唯一标识符) 的节点 id. 当没有指定时，Act将使用 `IdGenerator.NodeIdProvider.IpProvider` 返回根据节点的 IP 地址  [effective ip bytes](#idgen_node_id_effective_ip_bytes_size) 配置计算出的节点 id.

默认值: `act.util.IdGenerator.NodeIdProvider.IpProvider`

#### [idgen_node_id_effective_ip_bytes_size]idgen.node_id.effective_ip_bytes.size

别名

* **idgen.node_id.effective_ip_bytes**
* **act.idgen.node_id.effective_ip_bytes**
* **act.idgen.node_id.effective_ip_bytes.size**

指定 IP 地址中将使用多少字节来计算节点 ID. 通常在群集环境中, IP 地址将仅在（最后）一个字节段或（最后）两个字节段不同，在这种情况下，它可以将此配置设置为 `1` 或 `2`. 当配置设置为 `4` 时，表示所有 4 个 IP 字节段将用于计算节点 ID.

注意, 这个数字越大, CUID 就越长. 但是, 应该足以区分集群中的应用程序节点. 

默认值: `4`

#### [idgen_start_id_provider_impl]idgen.start_id.provider

别名

* **idgen.start_id.provider.impl**
* **act.idgen.start_id.provider**
* **act.idgen.start_id.provider.impl**

通过类名指定 `act.util.IdGenerator.StartIdProvider` 实现。 此提供程序生成 CUID 的开始 I D部分.

默认值: `act.util.IdGenerator.StartIdProvider.DefaultStartIdProvider`

默认提供程序将从 [predefined file](#idgen_start_id_file) 获取ID, 或者如果不允许文件 IO, 它将使用时间戳.

#### [idgen_start_id_file]idgen.start_id.file

别名

* **act.idgen.start_id.file**

指定开始 ID 计数器的持久性文件.

默认值: `.act.id-app`

#### [idgen_seq_id_provider_impl]idgen.seq_id.provider

别名

* **idgen.seq_id.provider.impl**
* **act.idgen.seq_id.provider**
* **act.idgen.seq_id.provider.impl**

通过类名指定 `act.util.IdGenerator.Sequence Provider` 的实现, 它将用于生成 CUID 的序列部分.

默认值: `act.util.IdGenerator.SequenceProvider.AtomicLongSeq`

#### [idgen_encoder_impl]idgen.encoder

别名

* **idgen.encoder.impl**
* **act.idgen.encoder**
* **act.idgen.encoder.impl**

按类名指定  `act.util.IdGenerator.LongEncoder `接口的实现. 该实例将用于将 long 值（生成的 CUID 的三个部分）编码为 String.

Available options:

* `act.util.IdGenerator.UnsafeLongEncoder` - maximum compression ratio, might generate URL unsafe characters
* `act.util.IdGenerator.SafeLongEncoder` - relevant good compression ratio without URL unsafe characters

默认值: `act.util.IdGenerator.SafeLongEncoder`

#### [job_pool_size]job.pool.size

别名

* **job.pool**
* **act.job.pool**
* **act.job.pool.size**

指定应用程序的 Job 管理器的线程池中可以存在的最大线程数.

默认值: `10`

#### [locale]locale

别名

* **act.locale**

指定应用程序默认语言.

默认值: `java.util.Locale#getDefault`

#### [metric]metric
  		  
别名
  		  
* **metric.enabled**
* **act.metric**
* **act.metric.enabled**
  		  
在Act应用程序中打开/关闭统计功能.
  		 
默认值: `true`

#### [modules]modules

别名

* **act.modules**

声明其他应用程序库（用于Maven模块）.

默认值: `null`

#### [namedPorts]namedPorts

别名

* **act.namedPorts**

指定此应用程序侦听的端口名称列表. 这些是除默认 [http.port](#http_port) 之外的其他端口.

该列表格式为

```
act.namedPorts=admin:8888;ipc:8899
```

默认值: `null`

注意，在 [http.port](#http_port) 配置中指定的默认端口, 并且不应在此 namedPort s配置中指定.

#### [ping_path]ping.path

别名

* **act.ping.path**

指定 ping 路径. 如果指定了此设置, 则在会话解析时, 系统将检查当前URL是否与设置匹配. 如果匹配, 则会话 cookie 的过期时间不会更改. 否则, 将刷新到期时间.

默认值: `null`

#### [profile]profile

别名

* **act.profile**

指定要加载的配置文件. 如果指定了此设置, 并且在 `/resource/conf` 文件夹下有一个名为  `profile`  设置的文件夹, 那么将从该文件夹加载配置文件.

默认值: the value of the {@link Act#mode()}

注意, 不同于通常在配置文件中指定的其他配置项. `profile` 设置是通过 `System#getProperty(String)` 加载, 因此通常使用 JVM 参数 `Dprofile=<profile>`

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

指定类是 `act.view.TemplatePathResolver` 的类. 应用开发者可以使用这种配置来灵活配置模板路径解析逻辑. 不同的 home 指定不同的地区或不同的 home 指定不同的设备类型等.

默认值: `act.view.TemplatePathResolver`

#### [resource_preload_size_limit]resource.preload.size.limit

别名

* **act.resource.preload.size.limit**

指定可以预加载到内存中的资源的最大字节数. 指定 `0` 或负数以禁用资源预加载功能.


默认值: `1024 * 10`, 表示 10KB

#### [scan_package]scan_package

别名

* **act.scan_package**

指定应用程序包, 其中所有类都受字节码处理, 例如增强和注入. 应在加载应用程序时指定此设置. 否则 Act 将尝试处理应用程序的 lib 和 classes 文件夹中找到的所有类, 这可能会在启动时导致性能问题.

#### [secret]secret

别名

* **act.secret**

指定应用程序用于执行常规加密/解密/签名等的密钥.

默认值: `myawesomeapp`

注意, 确保在 PROD 模式下设置此值.

#### [server_header]server.header

别名

* **act.server.header**

指定要输出到响应的服务器头

默认值: `act`

#### [session_ttl]session.ttl

别名

* **act.session.ttl**

指定会话持续时间（以秒为单位）, 如果用户无法与服务器交互超过设置的时间, 则会话将被销毁.

默认值: `60 * 30` 即半小时

#### [session_persistent_enabled]session.persistent

别名

* **session.persistent.enabled**
* **act.session.persistent**
* **act.session.persistent.enabled**

指定系统是否应将会话 cookie 视为 [persistent cookie](http://en.wikipedia.org/wiki/HTTP_cookie#Persistent_cookie). 如果启用此设置, 则在浏览器关闭后, 用户的会话不会被销毁.

默认值: `false`

#### [session_encrypt_enabled]session.encrypt

别名

* **session.encrypt.enabled**
* **act.session.encrypt**
* **act.session.encrypt.enabled**

{@code session.encrypted.enabled} 指定系统是否应该加密会话 cookie 中的键/值对. 启用会话加密将大大提高安全性, 但带来额外的 CPU 使用成本和请求处理的时间稍长.

默认值: `false`

#### [session_key_username]session.key.username

别名

* **act.session.key.username**

指定登录用户的用户名的会话密钥. 验证插件应使用配置为访问用户名的会话密钥.

默认值: `username`

#### [session_mapper_impl]session.mapper.impl

别名

* **session.mapper**
* **act.session.mapper**
* **act.session.mapper.impl**

通过类名指定 `act.util.SessionMapper` 的实现. 会话映射器可以用于将会话/闪存串行化以响应或在翻转侧上, 反序列化来自请求的会话/闪存信息.

#### [session_secure_enabled]session.secure

别名

* **session.secure.enabled**
* **act.session.secure**
* **act.session.secure.enabled**

指定会话Cookie是否应设置为安全. 启用安全会话将导致会话 cookie 仅在 https 连接中有效. 这将强制网站默认由 https 运行.

默认值: the setting of [http.secure](http_secure_enabled)

**注意** 当 Act 服务器在 DEV 模式中运行时, http 只会被禁用, 而不涉及 `session.secure.enabled` 设置.

#### [source_version]source.version

别名

* **act.source.version**

指定源代码 Java 版本. 此配置仅当应用程序在 DEV 模式下运行时才起效.

默认值: `1.7`

注意 ActFramework 支持 Java 1.7+. 确保这里不填入 `1.6` 或以下.

#### [target_version]target.version

别名

* **act.target.version**

指定 Java 编译版本. 此配置仅当应用程序在 DEV 模式下运行时才生效.

默认值: `1.7`

注意 ActFramework 支持 Java 1.7+. 确保这里不填入 `1.6` 或以下.

#### [template_home]template.home

别名

* **act.template.home**

指定视图模板所在的位置. 如果未指定, 则将使用视图引擎名称（小写）作为模板 home.

**注意** 强烈建议不要设置此配置项.

#### [url_login]url.login

别名

* **act.url.login**

指定由 `act.util.RedirectToLoginUrl` 使用的登录 URL, 作为 `MissingAuthenticationHandler` 的默认实现，请参阅 handler.missing_authentication.impl](handler_missing_authentication_impl)

默认值: `/login`

#### [url_login_ajax]url.login.ajax

别名

* **act.url.login.ajax**

指定 `act.util.RedirectToLoginUrl` 使用的登录 URL, 在响应 ajax 请求时, 它是 `MissingAuthenticationHandler` 的默认实现. 参见[handler.missing_authentication.ajax.impl](handler_missing_authentication_ajax_impl)

#### [view_default]view.default

别名

* **act.view.default**

指定默认视图引擎名称. 如果有多个视图注册并其中包含默认视图, 那么在加载模板时将优先使用默认视图.

默认值: `rythm` see [Rythm Engine](http://rythmengine.org)

Other options:

* `freemarker` - 需要 [act-freemarker](https://github.com/actframework/act-freemarker) 插件
* `velocity` - 需要 [act-velocity](https://github.com/actframework/act-velocity) 插件
* `mustache` - 需要 [act-mustache](https://github.com/actframework/act-mustache) 插件
* `thymeleaf` - 需要 [act-thymeleaf](https://github.com/actframework/act-thymeleaf) 插件
* `beetl` - 需要 [act-beetl](https://github.com/actframework/act-beetl) 插件
