# Deploy and run your ActFramework application

ActFramework app does not require servlet container/server to start. So deployment is very simple:

1. Make sure yous application's `pom.xml` file follow the [sample `pom.xml`](https://github.com/actframework/act-demo-apps/blob/master/helloworld/pom.xml)

2. Make sure your application's project contains the `src/assembly` folder copied from the [sample project](https://github.com/actframework/act-demo-apps/tree/master/helloworld/src/assembly) 

3. run `mvn clean package` command, and then you should be able to find a zip file in your `target/dist` dir

4. scp the zip file to your product server

5. ssh to your product server, locate the zip file uploaded, unzip it

6. type `./run` to run in current process, or `./start` to run in background

Normally you should have a frontend http server, e.g. nginx to service the request especially if you have multiple applications run in the same box. Here is one nginx configuration example:

```
server {
  listen          80;
  server_name     myapp.mycom.com;
  location / {
    proxy_pass        http://localhost:5460;
    proxy_set_header  X-Real-IP $remote_addr;
    proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header  Host $http_host;
  }
}
```
