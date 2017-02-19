# Implement File upload in ActFramework

File upload is a common web app function. This recipe introduce how to implement file upload in actframework.
Including single and multiple file(s) upload

First define a model class `Document` to demonstrate single file upload:

```java
public class Document implements SimpleBean {
    public String desc;
    public String subject;
    public File attachment;

    public Document(String subject, String desc, File attachment) {
        this.desc = desc;
        this.subject = subject;
        this.attachment = attachment;
    }
}
```

Then the request handler method:

```java
@PostAction("/single")
public Document handleSingleFile(File file, String subject, String desc) {
    return new Document(subject, desc, file);
}
```

The corresponding HTML Form for single file upload:

```html
<form action="/single" method="post" enctype="multipart/form-data">
  <div>
    <input name="subject" placeholder="subject">
  </div>
  <div>
    <input name="desc" placeholder="description">
  </div>
  <div>
    <input name="file" type="file" placeholder="file">
  </div>
  <div>
    <button type="submit">Submit</button>
  </div>
</form>
```

That's one approach on handling single file upload in Actframework. Next 
let's check out how to handle multiple file uploads.

Again define a model class to demonstrate multiple file uploads:

```java
public class Archive implements SimpleBean {
    public String desc;
    public String subject;
    public File[] attachments;

    public Archive(String subject, String desc, File[] attachments) {
        this.desc = desc;
        this.subject = subject;
        this.attachments = attachments;
    }
}
```

And the action handler to process multiple file upload request:

```java
@PostAction("/multi")
// Note the param type `File[]` can be changed to `List<File>`
public Archive handleMultipleFiles(File[] files, String subject, String desc) {
    return new Archive(subject, desc, files);
}
```

The corresponding HTML Form：

```html
<form action="/multi" method="post" enctype="multipart/form-data">
  <div>
    <input name="subject" placeholder="subject">
  </div>
  <div>
    <input name="desc" placeholder="description">
  </div>
  <div>
    <input name="files" type="file" placeholder="file">
  </div>
  <div>
    <input name="files" type="file" placeholder="file">
  </div>
  <div>
    <input name="files" type="file" placeholder="file">
  </div>
  <div>
    <button type="submit">Submit</button>
  </div>
</form>
```


Complele source code used in this recipe can be found on [git@oschina](http://git.oschina.net/greenlaw110/blog_act_file_upload)
