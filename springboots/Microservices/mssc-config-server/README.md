# Configuration Server

see : [Spring Cloud Config Reference](https://cloud.spring.io/spring-cloud-config/reference/html/)

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


To test the application : `http://localhost:8888/foo/default`
if there's no profile logically we would think that doing `/application` and we get the config properties. In reality when there is not an active profile, default profile is effectively active. So going just `/application`, get us `not found`. But if we do `/application/default` then we will get the config properties.
also `http://localhost:8888/anyapp/anyprofile` would work for **default global settings**.


Configuration of server config
-------
1- Create a config repo empty(e.g. https://github.com/mkejeiri/mssc-config-repo.git).

2- Create a folder that bears the same `spring.application.name` of the µservice (e.g. beer-service within https://github.com/mkejeiri/mssc-config-repo.git).

3- copy the properties file (e.g. `application-mysql.properties`) and rename it according to the profile.

4- update the search path with the name of the folder (e.g. `beer-service`) : e.g. `spring.cloud.config.server.git.search-paths={application}` 

5- issue a get request to `http://localhost:8888/beer-service/local` : 
- `http://localhost:8888/beer-service/default` gets only config from the root folder.

**Two phases configuration step**: 
- It will start up, i.e. it will at a bootstrap.properties file to find the environment. 
- and then it's will continue its normal course.

**we can bootstrap application and profiles** as well.



Cloud config enryption/decryption
-----------
A very simplistic example with symetric key (asymetric key is also  supported)

1- **Add inside** the file `bootstrap.properties` (not application.properties, this should be loaded at bootstrap) the following: 

```properties
#Should be env property
encrypt.key=MySuperSecretKey
```

2- to encrypt **Issue a post request** to `http://localhost:8888/encrypt`  with the password

3- to decrypt **Issue a post request** to `http://localhost:8888/decrypt`  with the password



add Spring Security to `mssc-config-server`
----

1- Add spring boot starter security dependency

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

2- add the following credentials to `application.properties`

```properties
spring.security.user.name=MyUserName
spring.security.user.password=MySecretPasswor
```


Adding basic auth to the microservice
--------
1- add spring security dependency

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

2- add a config 

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityFilterConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
    }
}

```

3- add credentials in property file (not recommended, this a show case)

```properties
#credentials to log in to microservice RestApi
spring.security.user.name=legitimate
spring.security.user.password=ramsis
```

Add basic auth to the restTemplate
--------
1- Add the credentials to the properties file (not recommended and it will do for dev)

```properties
#credentials for restTemplate to authenticate in Inventory Service
sfg.brewery.inventory-user=legitimate
sfg.brewery.inventory-password=ramsis
```

2- adjust the restTemplate to add authentication when building it

```java
public BeerInventoryServiceRestTemplateImpl(RestTemplateBuilder restTemplateBuilder,
        @Value("${sfg.brewery.inventory-user}") String inventoryServiceUser,
        @Value("${sfg.brewery.inventory-password}") String inventoryServicePassword) {
    this.restTemplate = restTemplateBuilder
                .basicAuthentication(inventoryServiceUser, inventoryServicePassword)
                .build();
    }
```

