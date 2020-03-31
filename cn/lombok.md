# 与 Lombok 集成。

## 为什么 ActFramework 与 Lombok 集成略显复杂？

ActFramework 拥有运行时重载的特性，即在开发模式下，代码变动，框架能自动载入修改后的代码。  
但正常情况下 Ecj 编译器并不能对 Lombok 产生支持，所以我们要通过特殊手段让 Ecj 能响应 Lombok。

这种手段，就是 JavaAgent。

Lombok 本身提供了对 Ecj 的支持，所以我们只需要把 Lombok 配置到 JavaAgent 就好了。

## 如何与 Lombok 集成

### 开发环境

首先，我们需要引入 Lombok 依赖。  

    <dependency>
        <groupId>org.projectlombok</groupId>  
        <artifactId>lombok</artifactId>  
        <version>1.16.10</version>  
    </dependency>

你可以按需引入高版本，但高版本可能由于某些原因，导致 ActFramework 无法正常启动。

引入依赖后，我们需要找到 Lombok 的位置，并配置好 JavaAgent。
这里我推荐将 Lombok 的 jar 复制到项目目录，并上传到版本控制服务。

![image](/img/lombok/lombokJar.png)

然后，在我们的 IDEA 启动项中配置好 JavaAgent 参数。  
-javaagent:lib/lombok.jar=ECJ

![image](/img/lombok/IDEA.png)

如果你做好了这两步，那么你可以尝试运行，这时候 Lombok 便可以正常的使用了。

### 打包运行
由于 ActFramework 在打包过程中要进行测试。  
测试时也会启动 ActFramework 并编译相关 Class 文件。  
所以我们也应该在这个过程中配置 JavaAgent 以支持 Lombok。  
这个时候，我们只需要在 pom.xml 的 properties 节点中，加入 act.lombok 属性，并指向 Lombok 的路径。

    <properties>
        <act.lombok>lib/lombok.jar</act.lombok>
    </properties>

这样，打包测试的时候，我们就能正常的使用 Lombok 了。