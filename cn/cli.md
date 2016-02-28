# 命令行应用

命令行是一种历史悠久且极有生命力的人机交互界面。即便在多数情况下最新的Web应用提供了各种丰富易用的人机交互体验，命令行应用依然是及其重要的工具，因为它：

1. 适合系统管理员使用
1. 通常只用于内部网中，所以安全性更高

ActFramework考虑了命令行应用的开发需求，特地提供了一些工具让开发命令行应用简单地不可思议。

## 创建命令响应器

```java
import act.app.CliContext;
import act.cli.Command;
import act.cli.Optional;
import act.cli.Required;
import act.cli.JsonView;
import act.util.PropSpec;

public class CustomerAdmin {
    
    @Inject
    private Customer.Dao customerDao;
    
    @Command(name = "cust.list", help = "list customers")
    @PropSpec("email,fullName as name,phone")
    public Iterable<Customer> list(
        @Optional(lead = "-q", help = "optionally specify the query string") String q
    ) {
        return customerDao.search(q);
    }
    
    @Command(name = "cust.show", help = "show customer details")
    @JsonView
    public Customer show(
        @Required(lead = "--id", help = "specify the customer ID") String id
    ) {
        return customerDao.findById(id);
    }
}
```

## 运行命令行并执行命令

```bash
#nc是一种简单的网络链接工具，如果系统中没有nc，可以使用telnet作为替代
$nc localhost 5461
act[1sbKUt2E1]>

act[1sbKUt2E1]>help -a

APPLICATION COMMANDS
cust.list             - list customers
cust.show             - show customer details

act[1sbKUt2E1]>cust.list -h
Usage: cust.list [options]
list customers

Options:
  -q                     optionally specify the query string

act[1sbKUt2E1]>cust.list -q "com1.com"
+--------------------------+--------------------+--------------+------------+
|            ID            |        EMAIL       |     NAME     |    PHONE   |
+--------------------------+--------------------+--------------+------------+
| 5684c35e6e250d52baa94935 | john@com1.com      | John Smith   | 11,111,111 |
| 569174e5b47e271add049154 | peter@com1.com     | Peter Brad   | 22,222,222 |
+--------------------------+--------------------+--------------+------------+
Items found: 2

act[1sbKUt2E1]>cust.show -h
Usage: cust.show [options]
show customer details

Options:
  --id                   specify the customer ID

act[1sbKUt2E1]>cust.show --id 5684c35e6e250d52baa94935
{
    "id": "5684c35e6e250d52baa94935",
    "email": "john@com1.com",
    "firstName": "John",
    "lastName": "Smith",
    "phone": "11,111,111",
    ...
}
```

## 在RESTful控制器和CLI命令器上的代码复用

通常你会发现同样的逻辑总是出现在控制器和命令响应器上。这意味着你可能需要一些拷贝粘贴工作，作为一名负责任的程序猿，你会对此非常恼火。所幸ActFramework允许你将同样的代码同时用于控制器和命令响应器，只需使用相应的注解即可：

```java
@Controller("/customer")
public class CustomerService {
    
    @Inject
    private Customer.Dao customerDao;
    
    @GetAction("/")
    @Command(name = "cust.list", help = "list customers")
    @PropSpec("email,fullName as name,phone")
    public Iterable<Customer> list(
        @Optional(lead = "-q", help = "optionally specify the query string") String q
    ) {
        return customerDao.search(q);
    }
    
    @GetAction("/{id}")
    @Command(name = "cust.show", help = "show customer details")
    @JsonView
    public Customer show(
        @Required(lead = "--id", help = "specify the customer ID") String id
    ) {
        return customerDao.findById(id);
    }
}
```

[回目录](index.md)