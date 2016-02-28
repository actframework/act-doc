# Sending Email

Sending email is very simple in ActFramework. What you need to do is:

1. Use `Mailer` annotation to tag your class that contains send mail methods
1. Extend your class to `Mailer.Util` class or add a static import statement:

    `import static act.mail.Mailer.Util.*`
    
## Creating mailer class and methods

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

## Creating mailer templates

Now that you have the mailer created, you can create the template corresponding to the mailer methods:

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

## Calling mailer methods

You can call the mailer method directly from any where, like the following controller code:

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

## Calling mailer method asynchronously

Usually executing mailer method involves IPC to external services (e.g. your SMTP server), which could be time consuming. So you would prefer to execute mailer method asynchrously and return back immediately. You can use ActFramework's event dispatching mechanism to achieve that.

1. Mark your mailer methods to be an event handler
    
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
    
1. Trigger event instead of call mailer method directly:

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