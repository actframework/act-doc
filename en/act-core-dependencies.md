# ActFramework dependencies

The following list describes the dependencies of ActFramework core

```
+- org.actframework:act:jar:0.6.0-SNAPSHOT:compile 
|  +- javax.inject:javax.inject:jar:1:compile ------------------------------------------------------- DI (JSR 330 API)
|  +- javax.enterprise:cdi-api:jar:1.2:compile ------------------------------------------------------ DI (CDI API, mainly to provides injection Scope annotations)
|  |  +- javax.el:javax.el-api:jar:3.0.0:compile
|  |  \- javax.interceptor:javax.interceptor-api:jar:1.2:compile
|  +- javax.validation:validation-api:jar:1.1.0.Final:compile --------------------------------------- Validation API
|  +- javax.mail:mail:jar:1.5.0-b01:compile --------------------------------------------------------- Java mail API
|  +- com.google.zxing:javase:jar:3.3.0:compile ------------------------------------------------------ For QR Code generation
|  |  +- com.google.zxing:core:jar:3.3.0:compile
|  |  +- com.beust:jcommander:jar:1.48:compile
|  |  \- com.github.jai-imageio:jai-imageio-core:jar:1.3.1:compile
|  +- com.github.lalyos:jfiglet:jar:0.0.8:compile ---------------------------------------------------- For Banner font generation
|  +- org.actframework:act-asm:jar:0.1.0-SNAPSHOT:compile -------------------------------------------- For bytecode scan/enhancment
|  +- org.hibernate:hibernate-validator:jar:5.1.3.Final:compile -------------------------------------- Validation implementation
|  |  +- org.jboss.logging:jboss-logging:jar:3.1.3.GA:compile
|  |  \- com.fasterxml:classmate:jar:1.0.0:compile
|  +- com.alibaba:fastjson:jar:1.2.24:compile -------------------------------------------------------- For JSON support
|  +- io.undertow:undertow-core:jar:1.4.8.Final:compile ---------------------------------------------- For netowork
|  |  +- org.jboss.xnio:xnio-api:jar:3.3.6.Final:compile
|  |  \- org.jboss.xnio:xnio-nio:jar:3.3.6.Final:runtime
|  +- io.undertow:undertow-websockets-jsr:jar:1.4.8.Final:compile ------------------------------------ For network
|  |  +- io.undertow:undertow-servlet:jar:1.4.8.Final:compile
|  |  |  +- org.jboss.spec.javax.servlet:jboss-servlet-api_3.1_spec:jar:1.0.0.Final:compile
|  |  |  \- org.jboss.spec.javax.annotation:jboss-annotations-api_1.2_spec:jar:1.0.0.Final:compile
|  |  \- org.jboss.spec.javax.websocket:jboss-websocket-api_1.1_spec:jar:1.1.0.Final:compile
|  +- com.squareup.okhttp3:okhttp:jar:3.4.1:compile -------------------------------------------------- For network (client side)
|  |  \- com.squareup.okio:okio:jar:1.9.0:compile
|  +- jline:jline:jar:2.14.2:compile ----------------------------------------------------------------- For CLI
|  +- org.eclipse.jdt.core.compiler:ecj:jar:4.6.1:compile -------------------------------------------- For hot-reload
|  +- com.esotericsoftware:reflectasm:jar:1.11.3:compile --------------------------------------------- Fast reflection
|  |  \- org.ow2.asm:asm:jar:5.0.4:compile
|  +- commons-fileupload:commons-fileupload:jar:1.3.2:compile ---------------------------------------- For multipart file parsing
|  |  \- commons-io:commons-io:jar:2.2:compile
|  +- commons-codec:commons-codec:jar:1.10:compile --------------------------------------------------- For various encodig/decoding
|  +- joda-time:joda-time:jar:2.9.7:compile ---------------------------------------------------------- For datetime API and implementation
|  +- org.osgl:genie:jar:0.5.0-SNAPSHOT:compile (version selected from constraint [0.5.0-SNAPSHOT,)) -------------------------------------- The JSR 330 DI implementation
|  |  \- org.osgl:osgl-logging:jar:0.7.0-SNAPSHOT:compile (version selected from constraint [0.7.0-SNAPSHOT,))
|  |     \- org.osgl:osgl-tool:jar:0.11.0-SNAPSHOT:compile (version selected from constraint [0.10.0-SNAPSHOT,0.11.0))
|  +- org.osgl:osgl-mvc:jar:0.9.0-SNAPSHOT:compile (version selected from constraint [0.9.0-SNAPSHOT,)) ----------------------------------- The MVC API (Request/Response/Session/Flash/Result)
|  |  \- org.osgl:osgl-http:jar:0.5.0-SNAPSHOT:compile (version selected from constraint [0.5.0-SNAPSHOT,))
|  |     +- org.osgl:osgl-storage:jar:0.8.0-SNAPSHOT:compile (version selected from constraint [0.8.0-SNAPSHOT,))
|  |     \- org.osgl:osgl-cache:jar:0.5.0-SNAPSHOT:compile (version selected from constraint [0.5.0-SNAPSHOT,))
|  +- org.osgl:osgl-tool-ext:jar:0.1.0-SNAPSHOT:compile (version selected from constraint [0.1.0-SNAPSHOT,)) ------------------------------ The OSGL tool extension
|  \- org.rythmengine:rythm-engine:jar:1.1.7-SNAPSHOT:compile ----------------------------------------------------------------------------- The default template engine
|     +- com.stevesoft.pat:pat:jar:1.5.3:compile
|     +- org.apache.commons:commons-lang3:jar:3.4:compile
|     \- org.mvel:mvel2:jar:2.2.8.Final:compile
------------------------------------------------------------------------
```
