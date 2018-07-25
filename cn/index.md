# ActFramework 文档 - r1.8.7

ActFramework 是一款[高性能 Java 全栈框架](https://www.techempower.com/benchmarks/#section=data-r15&hw=cl&test=fortune&l=hra0e7&c=4&o=4)，用于开发传统的 MVC 应用或 RESTful 服务。和其他现有 MVC/RESTful 框架相比，ActFramework 的优势在于表达力和简洁易用。

---
header-includes:
  - \usepackage{draftwatermark}
output: 
  pdf_document: 
    keep_tex: yes
---

\SetWatermarkText{WIP}

## 了解ActFramework

1. [准备工作](get_start.md#prerequisites)
1. [创建一个"Hello world"程序](get_start.md#create_hello_world_app)
1. [Act程序解析](get_start.md#anatomy)

## 基本概念

1. [配置](configuration.md)
1. [依赖注入](di.md)
1. [路由](routing.md)
1. [控制器](controller.md)
1. [域模型和数据访问](model.md)
1. [模板](templating.md)

## 高级课题

1. [拦截器](interceptor.md)
1. [作业调度](job.md)
1. [事件绑定与分发](event.md)
1. [发送邮件](email.md)
1. [创建命令行程序](cli.md)
1. [WebSocket支持](websocket.md)
1. Using CLI to inspect and manage your application
1. Using act-aaa to implement security
1. Using act-storage to implement file persistence

## 小灶

1. [控制JSON响应字段](recipe/json-response.md)
1. [处理文件上传](recipe/file-upload.md)
1. [任务调度](recipe/job-schedule.md)
1. [ActFramework中使用单例](recipe/singleton.md)
1. [依赖注入 - 注入对象类型](recipe/di-inject-type.md)
1. [用户密码的存储与验证机制与应用](recipe/user-password-hash.md)
1. [依赖注入III - 自定义绑定](recipe/di-binding.md)

## 参考手册

1. [配置](configuration.md)

\newpage
