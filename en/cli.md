# Command Line Interface

CLI is a very old but yet long live interface between people and computer. Even for the latest web application that provides different kinds of interfaces, CLI is still an important thing which

1. is administrator friendly
1. more secure because it could be restricted to internal network

ActFramework takes CLI into consideration and makes creating CLI an unexpected simple task for application developers.

## Create commander

```java
import act.app.CliContext;
import act.cli.Command;
import act.cli.Optional;
import act.cli.Required;
import act.cli.JsonView;

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

## Run CLI and issue commander

```bash
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

## Reuse code between RESTful controller and CLI commander

For many time you might find that you are repeating code between RESTful controller and CLI commander. Don't be annoying, ActFramework allows to multiplex your code in both controller and commander, just add the relevant annotation and your job is done!

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

[Back to index](index.md)