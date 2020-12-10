## 简介

`feign`对微服务之间的`http`调用做了一层封装，如果B项目想调用A项目的一个web服务，只需要编写对应的接口并标注`FeignClient`注解。但如果接口发生了变更，对应的`Feign`代码往往会忘记修改，而且问题往往在服务启动之后才能发现。

`feign-maven-plugin`根据当前项目的`jar`包，自动生成对应`feign`部分的代码，并`install`到本地仓库。使用时，只需要在`pom.xml`中添加对应的依赖，在项目中继承自动生成的`Feign`接口，添加`FeignClient`注解即可。

## 如何使用

### 生成feign工程并添加依赖

在你需要生成对应`feign`代码的项目`pom.xml`文件中添加以下配置。我们暂且称其为`feign`源工程，根据源工程生成`feign`工程，我们会在`feign`使用工程中调用`feign`工程中的代码。

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
        <plugin>
            <groupId>com.github.dewxin</groupId>
            <artifactId>feign-maven-plugin</artifactId>
            <version>1.0.0</version><!-- 使用最新版本 -->
            <executions>
                <execution>
                    <goals>
                        <goal>feign</goal>
                    </goals>
                    <phase>package</phase>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

**注意：此处`<plugin>`标签需要放置在`spring-boot-maven-plugin`之后。**

在项目根目录，使用`mvn packge`对项目进行打包。控制台输出观察到以下日志则表示生成的`feign`工程已经`install`到**本地仓库**。

```xml
[INFO] [INFO] ------------------------------------------------------------------------
[INFO] [INFO] BUILD SUCCESS
[INFO] [INFO] ------------------------------------------------------------------------
[INFO] [INFO] Total time:  3.944 s
[INFO] [INFO] Finished at: 2020-12-08T20:09:55+08:00
[INFO] [INFO] ------------------------------------------------------------------------
[INFO] <<< <<< finish phase maven-install <<< <<<
```

假如`feign`源工程的`pom`部分配置如下：

```xml
<groupId>source-groupId</groupId>
<artifactId>source-artifactId</artifactId>
<version>1.0.2</version>
```

那么生成的`feign`工程对应`pom`配置会是这样：除了`artifactId`根据原有的值添加`-feign`后缀外，其余的部分不发生改变。

```xml
<groupId>source-groupId</groupId>
<artifactId>source-artifactId-feign</artifactId>
<version>1.0.2</version>
```

在`feign`使用工程中 只需要添加如下的依赖即可以使用，需要注意的是生成的`feign`工程会被`install`到本地仓库。

```xml
<dependency>
    <groupId>source-groupId</groupId>
    <artifactId>source-artifactId-feign</artifactId>
    <version>1.0.2</version>
</dependency>
```



如果你在`application.properties`中配置的`spring.application.name`的值为`SERVICE-SOURCE`,`SOURCE-SERVICE`,`source`,`SOURCE`中的任意一个，那么生成对应feign接口的名字则为`SourceClient`。

```java
import com.github.dewxin.generated.auto_client.SourceClient;

@FeignClient(value="SERVICE-SOURCE", contextId = "feign")
public interface SourceFeignClient extends SourceClient {
}

```

此处需要添加 `contextId`，否则的话`feign`会生成两个同名的`bean`。
