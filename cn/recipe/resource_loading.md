# <a name="chapter_resource_loading">加载资源文件

## <a name="concept"> 1. 概念

存放在 `/src/main/resources` 目录或子目录下的文件是资源文件. 除了 ActFramework 需要的文件, 比如配置文件, 路由表等, 应用可以定义自己的资源文件, 并通过 `@LoadResource` 注解加载到程序中:

```java
public class Foo {
    @LoadResource("name.list")
    private List<String> nameList;
}
```

上面的代码指令 ActFramework 将 `resources/name.list` 文件读取到 `List<String> nameList` 中, 每一行读取为 `nameList` 列表中的一个元素.

## <a name="load-from-text-resource"></a> 2. 从文本资源中加载

ActFramework 支持从文本资源中加载到以下数据结构:

* 字符串
* 字符串列表 (每一行加载为列表中的一个元素)
* `Properties` - 从 `.properties` 文件资源中加载
* `Map` - 从类 `.properties` 文件资源中加载
* `List`
* 任意类型 - 从 JSON 或者 yaml 文件中加载

### <a name="load-text-into-string></a> 2.1 将文本资源加载到字符串

任何类型文本资源都可以加载到字符串:

<a name="s2_1">

```java
// snippet s2.1
@LoadResource("myfile.txt")
private String myStr;
```

### <a name="load-text-into-string-list></a> 2.2 将文本资源加载到字符串列表

任何类型文本资源都可以加载到 `List<String>` 数据结构, 该资源的每一行加载为字符串列表中的一个元素:


<a name="s2_2">

```java
// snippet s2.2
@LoadResource("myfile.txt")
private List<String> myLines;
```

### <a name="load-text-into-properties"></a> 2.3 将文本资源加载到 `Properties` 结构

后缀名为 ".properties" 的资源文件可以加载进 `Properties` 类型数据中:

<a name="s2_3"></a>

```java
// snippet s2.3
@LoadResource("foo.properties")
private Properties foo;
```

### <a name="load-text-into-map"></a> 2.4 加载文本到 `Map` 结构

任何类似 properties 文件内容的文本 (包括 .properties 文件) 都可以加载进 `Map` 类型数据:

<a name="s2_4a"></a>

```java
// snippet s2.4a
@LoadResource("foo.properties")
private Map<String, Object> foo;
```

如果文本中的值是特定类型, 你可以声明为该类型. 假设你的文本文件 `int_values.txt` 内容如下:

<a name="s2_4b"></a>
```
# s2.4b
one=1
two=2
```

你可以使用下面的方式类声明 `Map` 类型参数:

<a name="s2_4c"></a>
```java
// snippet s2.4c
@LoadResource("int_values.txt")
private Map<String, Integer> intValues;
```

### <a name="load-text-into-list"></a> 2.5 加载文本到 `List` 结构

在文本中每一行都可以转换为某个特定类型的情况下,可以将文本加载到非字符类型的 `List` 结构中, 例如资源文本为:

```text
1
2
3
4
5
```

可以加载到 `List<Integer>` 中:

```java
@LoadResource("int.list")
private List<Integer> intList;
```

### <a name="load-json-resource"></a> 2.6 加载 JSON 资源

JSON 资源内容可以加载到任何符合文件内容结构的数据中:

resource file: `chracters.json`

<a name="s2_6a"></a>
```json
// snippet s2.6a
[
{
    "username": "jamesbond",
    "level": 32
},
{
    "username": "ethanhunt",
    "level": 30
},
{
    "username": "jasonbourne",
    "level": 29
}
]
```

The java POJO class

<a name="s2_6b"></a>
```java
// snippet s2.6b
@Data
public class Character implements SimpleBean {
    public String username;
    public int level;
}
```

Load the characters into a list of `Character`s

<a name="s2_6c"></a>
```java
// snippet s2.6c
@LoadResource("characters.json")
private List<Character> characters;
```

### <a name="load-yaml-resource"></a> 2.7 加载 YAML 资源

和 JSON 资源相似, YAML 资源也可以加载到任何符合文件内容结构的数据中:

resource file: `chracters.yml`

<a name="s2_7a"></a>
```yaml
# snippet s2.7a
- username: jamesbond
  level: 32
- username: ethanhunt
  level: 30
- username: jasonbourne
  level: 29
```

The java POJO class

<a name="s2_7b"></a>
```java
// snippet s2.7b
@Data
public class Character implements SimpleBean {
    public String username;
    public int level;
}
```

Load the characters into a list of `Character`s

<a name="s2_7c"></a>
```java
// snippet s2.7c
@LoadResource("characters.yml")
private List<Character> characters;
```

## <a name="load-from-binary-resource"></a> 3. 从二进制资源中加载


ActFramework 支持从任何资源(包括二进制资源)中加载到以下数据结构:

* `byte[]`
* `java.nio.ByteBuffer`
* `java.nio.file.Path`
* `java.net.URL`
* `java.io.File`
* `java.io.InputStream`
* `java.io.Reader`
* `org.osgl.storage.ISObject`

<a name="s3a"></a>

```java
// snippet s3a

@LoadResource("myFile.pdf")
private ISObject myStorageObject;

@LoadResource("myFile.pdf")
private File myFile;

@LoadResource("myFile.pdf")
private byte[] myFileBlob
```