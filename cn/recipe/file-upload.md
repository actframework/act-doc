# ActFramework中实现文件上传

文件上传是一种常见的web应用功能。这篇小灶讲述如何在ActFramework中实现文件上传，包括单文件上传和多文件上传两种情况。

首先我们定义一个Model类`Document`用于演示单文件上传的情况：

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

下面是处理单文件上传的请求响应函数：

```java
@PostAction("/single")
public Document handleSingleFile(File file, String subject, String desc) {
    return new Document(subject, desc, file);
}
```

对应单文件上传的HTML Form:

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

以上就是Act应用中处理单文件上传的一种方式。下面来看看多文件上传的处理方式。

先定义一个Model类用于演示多文件上传：

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

处理多文件上传的请求响应函数：

```java
@PostAction("/multi")
// Note the param type `File[]` can be changed to `List<File>`
public Archive handleMultipleFiles(File[] files, String subject, String desc) {
    return new Archive(subject, desc, files);
}
```

对应的多文件上传的HTML Form：

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

这就是多文件上传的方式。

完整的源代码保存在[码云](http://git.oschina.net/greenlaw110/blog_act_file_upload)上
