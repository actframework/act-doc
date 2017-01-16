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

#### [cors_max_age](cors.max_age)

Aliases

* **act.cors.max_age**

Default value: 30*60 (seconds)

This configuration specifies the default value for `Access-Control-Max-Age` header when [cors](#cors) is enabled

