# Configuration Server

Dependencies : 
````xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
<dependency>
<groupId>org.springframework.cloud</groupId>
<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
````

add config/LocalDiscoveryConfig 
```java
@Profile("local-discovery")
@EnableDiscoveryClient
@Configuration
public class LocalDiscoveryConfig {
}
```

Add the following into `application.properties` :
```java
spring.application.name=mssc-config-server
server.port=8888
```

Enable Config Server :
````java
@EnableConfigServer
@SpringBootApplication
public class MsscConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsscConfigServerApplication.class, args);
    }

}
````

**application.properties**

```properties
spring.application.name=mssc-config-server

server.port=8888

spring.cloud.config.server.git.uri=https://github.com/Opentechup/config-test.git

#instruct the server when we start up we want to clone it so Spring Cloud Config will 
# clone to a temporary directory when it starts up.The documentation sets a warning 
# that can cause startup time to take a little bit longer. Downside is if we don't
# do that, the initial request is going to take a little longer.  
# So we're either going to do it on startup or on the initial request.
spring.cloud.config.server.git.clone-on-start=true

logging.level.org.springframework.cloud=debug
logging.level.org.springframework.web=debug
```

