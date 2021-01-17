# Spring Security Core - Spring MVC Monolith

This repository contains source code examples used to support Spring Security Core.

#### Difference between `/api/v1/user/*` and `/api/v1/user/**`?

`/api/v1/user/*` - will match any value, up to another "/"

`/api/v1/user/**` - will match all values beginning with start of string (including if another "/" is found.

Basic Auth Security
----------
We need to login to get into the root path and also to get the static assets!, to solve this, we need to do the following:

- Create a config `SecurityConfig` which extends `WebSecurityConfigurerAdapter`
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {...}
```

- Override the `configure method`

Allows any user to access:
- root directory "/"
- All webjars under "/webjars"
- login form under path "/login"
- All static resources under path "/resources"

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //this should before the 2nd authorizeRequests,
                //because the 2nd authorizeRequests is for anyRequest()!!!
                .authorizeRequests(expressionInterceptUrlRegistry -> {
				//Permit root path & static assets				
                    expressionInterceptUrlRegistry.antMatchers("/", "/webjars/**", "/login", "/resources/**")
                            .permitAll();
                })

                //Any other request rules!
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();
    }
}
```


