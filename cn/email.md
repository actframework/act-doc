# 发送邮件

在ActFramework发送邮件是非常简单的工作。如果你的某个类具有邮件发送方法，你需要：

1. 使用`act.mail.Mailer`注解标记该类
1. 让该类继承`Mailer.Util`类或者使用静态引入

    `import static act.mail.Mailer.Util.*`
    
## 创建邮件发送方法

```java
package com.mycom.myprj;

import act.conf.AppConfig;
import act.job.Every;
import act.mail.Mailer;
import org.osgl.logging.*;
import org.osgl.util.E;
import org.osgl.util.S;

import javax.inject.Inject;

/**
 * This class demonstrate how to write an email sender
 * in act framework.
 */
@Mailer
public class PostOffice extends Mailer.Util {

    public void sendWelcome(Contact who) {
        to(who.getEmail());
        subject("Welcome letter");
        send(who);
    }

    public void sendBye(Contact who) {
        to(who.getEmail());
        send(who);
    }

}
```

## 创建邮件模板

为每一个邮件发送方法创建对应的模板。模板的位置和控制器响应方法模板位置依照同样规则，对于上例中的`sendWelcome`和`sendBye`方法，对应的模板文件分别为:

1. `src/main/resources/rythm/com/mycom/myprj/PostOffice/sendWelcome.html`:

    ```html
    <html>
    <head></head>
    <body>
    @args com.mycom.myprj.Contact who
    <h1>Welcome @who.getFirstName()!</h1>
    <p>Blah Blah</p>
    </body>
    </html>
    ```

1. `src/main/resources/rythm/com/mycom/myprj/PostOffice/sendBye.html`:

    ```html
    <html>
    <head></head>
    <body>
    @args com.mycom.myprj.Contact who
    <h1>Good bye @who.getFirstName()!</h1>
    </body>
    </html>
    ```

## 调用邮件发送方法

你可以简单地通过方法调用发送邮件

```java
public class MyController {

    @Inject
    private PostOffice postOffice;

    @PostAction("/contact")
    public void createContact(Contact contact) {
        contactDao.save(contact);
        postOffice.sendWelcome(contact);
    }
    
    @PutAction("/contact/{contactId}/signOff")
    public void signOff(String contactId) {
        Contact contact = contactDao.findById(contactId);
        contact.signOff();
        contactDao.save(contact);
        postOffice.sendBye(contact);
    }
}
```

## 异步调用邮件发送方法

因为涉及远程通信，邮件发送通常来讲是比较耗时的操作。如果在控制器中调用邮件发送会造成结果返回延时。通常的做法是采用异步方式发送邮件，在ActFramework中，你可以通过事件分派来实现:

1. 使用`act.event.On`注解将邮件发送方法标注为异步事件响应器
    
    ```java
    @On(value = "contact-created", async = true)
    public void sendWelcome(Contact who) {
        to(who.getEmail());
        subject("Welcome letter");
        send(who);
    }
    
    @On(value = "contact-signed-off", async = true)
    public void sendBye(Contact who) {
        to(who.getEmail());
        send(who);
    }
    ``` 
    
1. 在需要调用邮件发送方法的时候触发事件

    ```java
    public class MyController {
        
        @Inject
        EventBus eventBus;

        @PostAction("/contact")
        public void createContact(Contact contact) {
            contactDao.save(contact);
            eventBus.trigger("contact-created", contact);
        }
        
        @PutAction("/contact/{contactId}/signOff")
        public void signOff(String contactId) {
            Contact contact = contactDao.findById(contactId);
            contact.signOff();
            contactDao.save(contact);
            eventBus.trigger("contact-signed-off", contact);
        }
    }
    ```
    
[返回目录](index.md)