<h1 data-book="configuration">Configuration</h1>

This chapter documents each ActFramework configuration item details

#### [basic_authentication]basic_authentication.enabled

Aliases:

* **basic_authentication**
* **act.basic_authentication**
* **act.basic_authentication.enabled**

Turn on/off [Basic Authentication](https://en.wikipedia.org/wiki/Basic_access_authentication) in ActFramework application.

Default value: `false`

**Note** there is no logic around this configuration in the core ActFramework. It is up to the security plugins like `act-aaa-plugin` to use the value of this setting

#### [cache_impl]cache.impl

Aliases

* **cache**
* **act.cache**
* **act.cache.impl**

Specify cache servcie implementation. The configuration must be a name of the `org.osgl.cache.CacheServiceProvider` implementation class.

Default value: `Auto`, i.e. `org.osgl.cache.CacheServiceProvider.Impl.Auto`. This implementation will automatically choose the delegated cache provider using the following order:

1. check if `MemcachedServiceProvider` can be instantiated, if not then
2. check if `EhCacheServiceProvider` can be instantiated, if not then
3. load `SimpleCacheServiceProvider` instance which use in memory map to implement the cache service

#### [cache_name]cache.name

Aliases

* **act.cache.name**

Specify the name of default cache used in Act application.

Default value: `_act_app_`

#### [cache_name_session]cache.name.session

Aliases

* **act.cache.name.session**

Specify the name of session cache used in Act application

Default value: the value set in [cache.name](#cache_name) configuration

#### [cli_page_size_json]cli.page.size.json

Aliases

* **act.cli.page.size.json**

Specify the maximum records in one page for JSON layout by CLI command

Default value: 10

#### [cli_page_size_table]cli.page.size.table

Aliases

* **act.cli.page.size.table**

Specify the maximum records in one page for table layout by CLI command

Default value: 22


#### [cli_port]cli.port

Aliases

* **act.cli.port**

Set the CLI telnet port

Default value: `5461`

#### [cli_session_expiration]cli.session.expiration

Aliases

* **act.cli.session.expiration**

Specify the number of seconds a cli session can exists after last user interaction

Default value: `300`, i.e. 5 minutes

### [cli_session_max]cli.session.max

Aliases

* **act.cli.session.max**

Specifies the maximum number of cli session threads can exists concurrently

Default value: `3`

#### [cli_over_http_enabled]cli_over_http.enabled

Aliases

* **cli_over_http**
* **act.cli_over_http**
* **act.cli_over_http.enabled**

Turn on/off CLI over http feature.

Default value: `false`

Once CLI Over HTTP is turned on, it allows admin to access CLI commands over HTTP through the [configured port](#cli_over_http.port)

#### [cli_over_http_authority_impl]cli_over_http.authority.impl

Aliases

* **cli_over_http.authority**
* **act.cli_over_http.authority**
* **act.cli_over_http.authority.impl**

Configure the authority provider for CLI over http access. The specified value should be a class name of `act.cli.CliOverHttpAuthority` implementation.

Default value: `CliOverHttpAuthority.AllowAll` which allows any request sent through.

#### [cli_over_http_port]cli_over_http.port

Aliases

* **act.cli_over_http.port**

Set the HTTP port for CLI Over HTTP service

Default value: `5462`

#### [cli_over_http_title]cli_over_http.title

Aliases

* **act.cli_over_http.title**

Specify the title to be displayed on the CLI Over Http page

Default value: `Cli Over Http`

#### [cli_over_http_syscmd_enabled]cli_over_http.syscmd.enabled

Aliases

* **cli_over_http.syscmd**
* **act.cli_over_http.syscmd**
* **act.cli_over_http.syscmd.enabled**

Turn on/off access to system command on CLI Over Http

Default value: `true`

#### [cookie_domain_provider]cookie.domain_provider

Aliases

* **act.cookie.domain_provider**

Specify the provider that returns the domain name. When not specified then it returns a provider that always return the value configured in [host](#host) configuration.

Valid configuration values:

1. Class name of implementation of `javax.inject.Provider` that returns a `String` typed value

2. `dynamic` or `flexible` or `contextual`, all means the domain name will get from the current request's domain

Default value: `null`

#### [cors]cors.enabled

Aliases

* **cors**
* **act.cors**
* **act.cors.enabled**

Turn on/off [CORS](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing) in ActFramework application.

Default value: `false`

Once `cors` is enabled, ActFramework will add CORS specific headers (listed below) into the HTTP response by default. ActFramework will also create HTTP Option request handler when this configuration is turned on.

#### [cors_option_check]cors.option.check.enabled

Aliases

* **cors.option.check**
* **act.cors.option.check**
* **act.cors.option.check.enabled**

Default value: `true`

When this configuration is enabled, ActFramework will add the following CORS relevant headers only to HTTP OPTION request:

* access-control-allow-headers
* access-control-expose-headers
* access-control-max-age

However header `access-control-allow-origin` is always added without regarding to the HTTP request method when [cors](#cors) configuration is enabled.

#### [cors_origin]cors.origin

Aliases

* **act.cors.origin**

Default value: `*`

This configuration specifies the default `Access-Control-Allow-Origin` header value

#### [cors_headers]cors.headers

Aliases

* **act.cors.headers**

Default value: `Content-Type, X-HTTP-Method-Override`

This configuration specifies the default value for `Access-Control-Allow-Headers` and `Access-Control-Expose-Headers` headers

#### [cors_headers_expose]cors.headers.expose

Aliases

* **act.cors.headers.expose**

Default value: `null`

This configuration specifies the default value for `Access-Control-Expose-Headers` header value. If not provided then system will use the value provided by [cors.headers](#cors_headers)

#### [cors_headers_allowed]cors.headers.allowed

Aliases

* **act.cors.headers.allowed**

Default value: `null`

This configuration specifies the default value for `Access-Control-Allow-Headers` header value. If not provided then system will use the value provided by [cors.headers](#cors_headers)

#### [cors_max_age]cors.max_age

Aliases

* **act.cors.max_age**

Default value: 30*60 (seconds)

This configuration specifies the default value for `Access-Control-Max-Age` header when [cors](#cors) is enabled

#### [content_suffix_aware_enabled]content_suffix.aware.enabled

Aliases

* **content_suffix.aware**
* **act.content_suffix.aware**
* **act.content_suffix.aware.enabled**

Once enabled then the framework automatically recognize request with content suffix, e.g. `/customer/123/json` or `/customer/123.json` will match the route `/customer/123` and set the request `Accept` header value to `application/json`

Default value: `false`

#### [csrf]csrf.enabled

Aliases

* **csrf**
* **act.csrf**
* **act.csrf.enabled**

Turn on/off global [CSRF](https://www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF)_Prevention_Cheat_Sheet) protect

Default value: `false`

Once this configuration is turned on the framework will check all POST/PUT/DELETE request for CSRF token. If it doesn't match then the request will get rejected with 403 Forbidden response

#### [csrf_param_name]csrf.param_name

Aliases

* **act.csrf.param_name**

Set the request parameter name for CSRF token

Default value: `__csrf__`

#### [csrf_header_name]csrf.header_name

Aliases

* **act.csrf.param_name**

Set the response header name for CSRF token generated from server

Default value: `XSRF-TOKEN`

#### [csrf_protector]csrf.protector.impl

Aliases

* **csrf.protector**
* **act.csrf.protector**
* **act.csrf.protector.impl**

Set the implementation of `act.security.CSRFProtector`. The value of this configuration could be either a name of the class that implements `act.security.CSRFProtector` interface or the name of the enum defined in `act.security.CSRFProtector.Predefined`.

Default value: `HMAC`

Other options in `act.security.CSRFProtector.Predefined`: `RANDOM`

For differences between `HMAC` and `RANDOM` please checkout http://security.stackexchange.com/questions/52224/csrf-random-value-or-hmac

#### [db_seq_gen_impl]db.seq_gen.impl

Aliases

* **db.seq_gen**
* **act.db.seq_gen**
* **act.db.seq_gen.impl**

Specify database sequence generator. Which must be class name of the  implementation of `act.db.util._SequenceNumberGenerator`. If not specified then it will return the first `act.db.util._SequenceNumberGenerator` implementation scanned by ActFramework.

#### [encoding]encoding

Aliases

* **act.encoding**

Specify application default encoding. Default value is `UTF-8`. It is highly recommended not to change the default setting.

#### [fmt_date]fmt.date

Aliases

* **act.fmt.date**

Specifies the default date format used to parse/output date string. 

Default value: the pattern of `java.text.DateFormat.getDateInstance()`

#### [fmt_date_time]fmt.date_time

Aliases

* **act.fmt.date_time**

Specifies the default date and time format used to parse/output date time string

Default value: the pattern of `java.text.DateFormat.getDateTimeInstance()`

#### [fmt_time]fmt.time

Aliases

* **act.fmt.time**

Specifies the default time format used to parse/output time string

Default value: the pattern of `java.text.DateFormat.getTimeInstance()`

#### [handler_missing_authentication_impl]handler.missing_authentication.impl

Aliases

* **handler.missing_authentication**
* **act.handler.missing_authentication**
* **act.handler.missing_authentication.impl**

Specifies implementation of `act.util.MissingAuthenticationHandler` interface by class name. The implementation is called when [CSRF token](csrf) cannot be verified.

Default value: `act.util.RedirectToLoginUrl` which redirect the user to [login URL](url_login)

Other options: `act.util.ReturnUnauthorized` which respond with `401 Unauthorised`

#### [handler_missing_authentication_ajax_impl]handler.missing_authentication.ajax.impl

Aliases

* **handler.missing_authentication.ajax**
* **act.handler.missing_authentication.ajax**
* **act.handler.missing_authentication.ajax.impl**

Specifies implementation of `act.util.MissingAuthenticationHandler` interface by class name. The implementation is called when [CSRF token](csrf) cannot be verified on ajax request

Default value: the setting of [handler.missing_authentication.impl](handler_missing_authentication_ajax_impl)

#### [host]host

Aliases

* **act.host**

Specifies the hostname the application listen to

Default value: `localhost`

#### [http.external_server.enabled]http.external_server.enabled

Aliases

* **http.external_server**
* **act.http.external_server**
* **act.http.external_server.enabled**

Specify if the app is running behind a front end http server, e.g. nginx

Default value: `true` when running in `PROD` mode; `false` when running in `DEV` mode

Note act does not listen to external port directly. The recommended pattern is to have a front end HTTP server (e.g. nginx) to handle the external request and forward to act

#### [http.port.external]http.port.external

Aliases

* **act.http.port.external**

Specifies the external port which is used to construct the full URL

Default value: `80`

#### [http.port.external.secure]http.port.external.secure

Specifies the external secure port which is used to construct the full URL when app is running on secure channel

#### [http_params_max]http.params.max

Aliases

* **act.http.params.max**

Specifies the maximum number of http parameters. This can be used to prevent the hash collision DOS attack. If this configuration is set to any value larger than 0, ActFramework will check the request parameter number, if the number is larger than the setting, then a `413 Request Entity Too Large` response is returned immediately

Default value: `1000`

#### [http_port]http.port

Aliases

* **act.http.port**

Specifies the default http port the application listen to.

Default value: `5460`

#### [http_secure_enabled]http.secure.enabled

Aliases 

* **http.secure**
* **act.http.secure**
* **act.http.secure.enabled**

Specifies whether the default http port is listening on secure channel or not.

Default value: `false` when app is running in `DEV` mode, `true` if app is running in `RPOD` mode

#### [i18n_enabled]i18n.enabled

Aliases

* **i18n**
* **act.i18n**
* **act.i18n.enabled**

Turn on/off i18n support in ActFramework application

Default value: `false`

#### [i18n_locale_param_name]i18n.locale.param_name

Aliases

* **act.i18n.locale.param_name**

Specify the param name to set client locale in http request

Default value: `act_locale`

#### [i18n_locale_cookie_name]i18n.locale.cookie_name

Aliases

* **act.i18n.locale.cookie_name**

Specify the name for the locale cookie

Default value: `act_locale`

#### [idgen_node_id_provider_impl]idgen.node_id.provider.impl

Aliases

* **idgen.node_id.provider**
* **act.idgen.node_id.provider**
* **act.idgen.node_id.provider.impl**

Specify the `act.util.IdGenerator.NodeIdProvider` implementation by class name. The node id provider is responsible to generate the node id for a CUID (Cluster Unique Identifer). When not specified, then Act will use the `IdGenerator.NodeIdProvider.IpProvider` that return the node id calculated from the node's ip address based on [effective ip bytes](#idgen_node_id_effective_ip_bytes_size) configuration

Default value: `null`

#### [idgen_node_id_effective_ip_bytes_size]idgen.node_id.effective_ip_bytes.size

Aliases

* **idgen.node_id.effective_ip_bytes**
* **act.idgen.node_id.effective_ip_bytes**
* **act.idgen.node_id.effective_ip_bytes.size**

Specifies how many bytes in the ip address will be used to calculate node ID. Usually in a cluster environment, the ip address will be different at only (last) one byte or (last) two bytes, in which case it could set this configuration to `1` or `2`. When the configuration is set to `4` then it means all 4 IP bytes will be used to calculate the node ID.

Note the bigger this number is, the longer the CUID will be. However it should be enough to distinct the application nodes inside a cluster.

Default value: `4`

#### [idgen_start_id_provider_impl]idgen.start_id.provider.impl

Aliases

* **idgen.start_id.provider**
* **act.idgen.start_id.provider**
* **act.idgen.start_id.provider.impl**

Specifies the `act.util.IdGenerator.StartIdProvider` implementation by class name. This provider generate the start ID part of a CUID.

Default value: `act.util.IdGenerator.StartIdProvider.DefaultStartIdProvider`

The default provider will get the ID from [predefined file](#idgen_start_id_file), or if file IO is not allowed, it will use the timestamp.

#### [idgen_start_id_file]idgen.start_id.file

Aliases

* **act.idgen.start_id.file**

Specifies the start id persistent file for start ID counter.

Default value: `.act.id-app`

#### [idgen_seq_id_provider_impl]idgen.seq_id.provider.impl

Aliases

* **idgen.seq_id.provider**
* **act.idgen.seq_id.provider**
* **act.idgen.seq_id.provider.impl**

Specifies the impelementation of `act.util.IdGenerator.SequenceProvider` by class name, which will be used to generate the sequence part of CUID.

Default value: `act.util.IdGenerator.SequenceProvider.AtomicLongSeq`

#### [idgen_encoder_impl]idgen.encoder.impl

Aliases

* **idgen.encoder**
* **act.idgen.encoder**
* **act.idgen.encoder.impl**

Specifies an implementation of `act.util.IdGenerator.LongEncoder` interface by class name. The instance will be used to encode long value (the three parts of CUID generated) into a String.

Available options:

* `act.util.IdGenerator.UnsafeLongEncoder` - maximum compression ratio, might generate URL unsafe characters
* `act.util.IdGenerator.SafeLongEncoder` - relevant good compression ratio without URL unsafe characters

Default value: `act.util.IdGenerator.SafeLongEncoder`

#### [locale]locale

Aliases

* **act.locale**

Specifies the application default locale.

Default value: `java.util.Locale#getDefault`

#### [job_pool_size]job.pool.size

Aliases

* **job.pool**
* **act.job.pool**
* **act.job.pool.size**

Specifies the maximum number of threads can exists in the application's job manager's thread pool

Default value: `10`

#### [modules]modules

Aliases

* **act.modules**

Declare additional app base (for maven modules)

Default value: `null`

#### [namedPorts]namedPorts

Aliases

* **act.namedPorts**

specifies a list of port names this application listen to. These are additional ports other than the default [http.port](#http_port)

The list is specified as

```
act.namedPorts=admin:8888;ipc:8899
```

Default value: `null`

Note, the default port that specified in [http.port](#http_port) configuration and shall not be specified in this namedPorts configuration

#### [ping_path]ping.path

Aliases

* **act.ping.path**

Specify the ping path. If this setting is specified, then when session resolving, system will check if the current URL matches the setting. If matched then session cookie expiration time will not be changed. Otherwise the expiration time will refresh

Default value: `null`

#### [profile]profile

Aliases

* **act.profile**

Specifies the profile to load configuration If this setting is specified, and there is a folder named as the `profile` setting sit under `/resource/conf` folder, then the properties files will be loaded from that folder.

Default value: the value of the {@link Act#mode()}

Note, unlike other configuration items which is usually specified in the configuration file. `profile` setting is load by `System#getProperty(String)`, thus it is usually specified with JVM argument `Dprofile=<profile>`

#### [resolver_error_template_path_impl]resolver.error_template_path.impl

Aliases

* **resolver.error_template_path**
* **act.resolver.error_template_path**
* **act.resolver.error_template_path.impl**

Specifies error page (template) path resolver implementation by class name

Default value: `act.util.ErrorTemplatePathResolver.DefaultErrorTemplatePathResolver`

#### [resolver_template_path_impl]resolver.template_path.impl

Aliases

* **resolver.template_path**
* **act.resolver.template_path**
* **resolver.template_path.impl**

specifies the class that is type of `act.view.TemplatePathResolver`. Application developer could use this configuration to add some flexibility to template path resolving logic, e.g. different home for different locale or different home for different device type etc.

Default value: `act.view.TemplatePathResolver`

#### [scan_package]scan_package

Aliases

* **act.scan_package**

Specify the app package in which all classes is subject to bytecode processing, e.g enhancement and injection. This setting should be specified when application loaded. Otherwise Act will try to process all classes found in application's lib and classes folder, which might cause performance issue on startup 

#### [secret]secret

Aliases

* **act.secret**

Specifies the secret key the application used to do general encrypt/decrypt/sign etc

Default value: `myawesomeapp`

Note, make sure you set this value on PROD mode

#### [session_prefix]session.prefix

Aliases

* **act.session.prefix**

Specifies the prefix to be prepended to the session cookie name. Let's say the default cookie name is ｀act_session｀, and user specifies the prefix ｀my_app｀
then the session cookie name will be ｀my_app_session｀

Note this setting also impact the ｀AppConfig#flashCookieName()｀

Default value: ｀act`

#### [session_ttl]session.ttl

Aliases

* **act.session.ttl**

specifies the session duration in seconds. If user failed to interact with server for amount of time that exceeds the setting then the session will be destroyed

Default value: `60 * 30` i.e half an hour

#### [session_persistent_enabled]session.persistent.enabled

Aliases

* **session.persistent**
* **act.session.persistent**
* **act.session.persistent.enabled**

Specify whether the system should treat session cookie as [persistent cookie](http://en.wikipedia.org/wiki/HTTP_cookie#Persistent_cookie). If this setting is enabled, then the user's session will not be destroyed after browser closed. 

Default value: `false`

#### [session_encrypt_enabled]session.encrypt.enabled

Aliases

* **session.encrypt**
* **act.session.encrypt**
* **act.session.encrypt.enabled**

{@code session.encrypted.enabled} specify whether the system should encrypt the key/value pairs in the session cookie. Enable session encryption will greatly improve the security but with the cost of additional CPU usage and a little bit longer time on request processing. 

Default value: `false`

#### [session_key_username]session.key.username

Aliases

* **act.session.key.username**

Specifies the session key for username of the login user. Authentication plugin shall use the session key configured to access the username.

Default value: `username`

#### [session_mapper_impl]session.mapper.impl

Aliases

* **session.mapper**
* **act.session.mapper**
* **act.session.mapper.impl**

Specify the implementation of `act.util.SessionMapper` by class name. A session mapper can be used to serialize session/flash to response or on the flippering side, deserialize session/flash info from request.

#### [session_secure_enabled]session.secure.enabled

Aliases

* **session.secure**
* **act.session.secure**
* **act.session.secure.enabled**

specifies whether the session cookie should be set as secure. Enable secure session will cause session cookie only effective in https connection. Literally this will enforce the web site to run default by https.

Default value: the setting of [http.secure](http_secure_enabled)

**Note** when Act server is running in DEV mode session http only will be disabled without regarding to the `session.secure.enabled` setting

#### [source_version]source.version

Aliases

* **act.source.version**

Specifies the Java source version. This configuration has impact only when app is running in DEV mode

Default value: `1.7`

Note ActFramework support Java 1.7+. Make sure you do NOT put in `1.6` or below here.

#### [source_version]source.version

Aliases

* **act.source.version**

Specifies the Java source version. This configuration has impact only when app is running in DEV mode

Default value: `1.7`

Note ActFramework support Java 1.7+. Make sure you do NOT put in `1.6` or below here.

#### [target_version]target.version

Aliases

* **act.target.version**

Specifies the Java target version. This configuration has impact only when app is running in DEV mode

Default value: `1.7`

Note ActFramework support Java 1.7+. Make sure you do NOT put in `1.6` or below here.

#### [template_home]template.home

Aliases

* **act.template.home**

Specifies where the view templates resides. If not specified then will use the view engine name (in lowercase) as the template home.

**Note** it is highly recommended NOT to set this configuration item

#### [unknown_http_method_handler_impl]unknown_http_method_handler.impl

Aliases

* **unknown_http_method_handler**
* **act.unknown_http_method_handler**
* **act.unknown_http_method_handler.impl**

Specifies a class/instance that implements `act.handler.UnknownHttpMethodProcessor` that process the HTTP methods that are not recognized by `act.route.Router`, e.g. "OPTION", "PATCH" etc

#### [url_login]url.login

Aliases

* **act.url.login**

Specifies the login URL which is used by `act.util.RedirectToLoginUrl`, the default implementation of `MissingAuthenticationHandler`, see [handler.missing_authentication.impl]
(handler_missing_authentication_impl)

Default value: `/login`

#### [url_login_ajax]url.login.ajax

Aliases

* **act.url.login.ajax**

Specifies the login URL which is used by `act.util.RedirectToLoginUrl`, the default implementation of `MissingAuthenticationHandler` when answering ajax request. See [handler.missing_authentication.ajax.impl](handler_missing_authentication_ajax_impl)

#### [view_default]view.default

Aliases

* **act.view.default**

Specifies the default view engine name. If there are multiple views registered and default view are available, then it will be used at priority when loading the templates

Default value: `rythm` see [Rythm Engine](http://rythmengine.org)

Other options:

* freemarker - need [act-freemarker](https://github.com/actframework/act-freemarker) plugin
* velocity - need [act-velocity]https://github.com/actframework/act-velocity() plugin
* mustache - need [act-mustache](https://github.com/actframework/act-mustache) plugin
* thymeleaf - need [act-thymeleaf]https://github.com/actframework/act-thymeleaf() plugin
* beetl - need [act-beetl](https://github.com/actframework/act-beetl) plugin
