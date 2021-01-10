# MSSC Beer Inventory Service

This repository contains source code examples used to support my on-line courses about the Spring Framework.


Adding basic auth to Inventory service
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
#credentials to log in to inventory service Api
spring.security.user.name=legitimate
spring.security.user.password=ramsis
```