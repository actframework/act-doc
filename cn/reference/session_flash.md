# 第五章 控制器, 请求处理方法与响应返回
# <a name="inside_session_flash">附录 4. Session 与 Flash 的处理详解

ActFramework 对 Session/Flash 的处理流程如下图所示:

![session-flow-chart](https://user-images.githubusercontent.com/216930/38778305-5bf285e2-40fb-11e8-8f65-98a433dd039e.png)

1. 处理请求
	1.1 ActFramework 使用 `SessionMapper` 将请求中的某个特定 Cookie 或者 Header 映射为一个字串
	1.2 然后使用 `SessionCodec` 将字串解析为 `H.Session` 或者 `H.Flash` Scope 对象
2. 处理响应
	2.1 ActFramework 使用 `SessionCodec` 将 `H.Session` 或者 `H.Flash` Scope 对象打包进一个字串
	2.2 然后使用 SessionMapper 将该字串映射到响应特定 Cookie 或者 Header 上

### <a name="session_mapper"></a>1 `SessionMapper`

`SessionMapper` 负责将序列化之后的 session/flash 字串映射到响应上, 以及从请求中获取 session/flash 字串. 具体来说 ActFramework 内置两大类型的 SessionMapper:

#### <a name="cookie_session_mapper"></a>1.1 `CookieSessionMapper`

`CookieSessionMapper` 将 session 字串写入特定名字的 cookie 之中:

1. session cookie 名字为 ${app-short-id}-session; flash cookie 名字为 ${app-short-id}-flash
	* 关于 `app-short-id` 的详细内容,参见[启动手册](reference/bootstrap.md#short_id)
2. cookie path: `/`
3. cookie domain: 当 localhost 为 host 时, 为空值, 否则为 host 配置
4. httpOnly: true
5. secure: `http.secure` 的配置值
6. value: 序列化之后的 session 或者 flash 字串
7. ttl: `session.ttl` 配置, 默认为 60 * 30, 即半小时

#### <a name="header_session_mapper"></a>1.1 `HeaderSessionMapper`

`HeaderSessionMapper` 将 session 字串写入某个 HTTP 响应头. Session 头的名字为 `X-Act-Session`, Flash 头的名字为 `X-Act-Flash`

**注意** 如果配置文件中存在 `jwt=true` 会使用 `Authorization` 作为 Session 头的名字, 且会设置 `session.header.payload.prefix` 为 `Bearer `, 表明 session 字串会存放在 `Authorization` 头的值中, 且使用 `Bearer `作为前缀. 这直接按照标准方式提供了 JWT 服务, 且应用无需任何改变.

### <a name="session_codec"></a>2 `SessionCodec`

`SessionCodec` 负责将 `H.Session`, `H.Flash` scope 对象和字串做相互转换. 在请求处理过程中, `SessionMapper` 从请求中拿到 Session/Flash 的序列化字串, `SessionCodec` 负责将字串转换为 Scope 对象. 送出响应之前, `SessionCodec` 负责将 Scope 对象序列化为字串, 并交与 `SessionMapper` 放进响应.

#### <a name="default_session_codec"></a>2.1 `DefaultSessionCodec`

这是系统默认使用的 `SessionCodec`. 其编码过程如下:

1. 如果发现 session/flash 对象没有变化且无内容, 则返回 `null`
2. 在 session 对象上设定过期时间 (默认为 30*60 - 半小时, 通过 `session.ttl` 配置参数调整) - 仅适用于 session
3. 对于 session/flash 对象上每一个 (key, value) 配对, 将其用 `\u0001` 拼接, 配对之间使用 `\u0000` 拼接, 生成 payload 字串
4. 对 payload 字串生成签名, 并使用 `-` 拼接在 payload 字串之前 - 仅适用于 session
5. 当配置文件中设置了 `session.encrypt` 为 `true` 的情况下, 对 {签名-payload} 做加密处理 - 仅适用于 session
6. 最后对字串做 url 安全编码生成最终结果

当收到请求的时候解码过程是上面的逆操作.

#### <a name="jwt_session_codec"></a>2.2 `JsonWebTokenSessionCodec`

如果配置文件中设置了 `jwt=true` 则使用 `JsonWebTokenSessionCodec` 来处理 Session 对象的编码与解码:

1. 如果发现 session 对象没有变化且无内容, 则返回 `null`
2. 生成一个空的 JWT 对象
3. 在 JWT 对象上通过 `exp` 设定过期时间 (默认为 30*60 - 半小时, 通过 `session.ttl` 配置参数调整)
4. 对于 session 对象上每一个 (key, value) 配对, 将其放入 JWT 对象的 payload 中
5. 将算法名字使用 `alg` JWT 头部. 默认算法为 `SHA256`, 可以通过 `jwt.algo` 配置为其他算法. 目前支持的算法除了 `SHA256`, 还有 `SHA384` 和 `SHA512`
6. 将 JWT 的头部序列化为 JSON 字串并做 URL 安全编码, payload 部分序列化为 JSON 字串并作 URL 安全编码, 两个部分用 `.` 拼接起来
7. 对上面生成的字串使用算法进行 hash 计算, 并将计算结果使用 `.` 拼接在最后, 生成 JWT 字串

当受到请求的时候发现了 JWT 字串, 采用上面的逆操作生成 session 对象.
