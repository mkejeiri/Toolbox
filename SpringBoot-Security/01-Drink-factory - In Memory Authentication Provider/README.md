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
**Basic Auth** need to add user in properties file otherwise we get random `password` at startup for `user`

```properties
spring.security.user.name=admin
spring.security.user.password=password
```


Spring Security Authentication Process
--------------
- **Authentication Filter** : A filter for a specific Authentication type in the Spring Security filter chain. (i.e. `basic auth`, `remember me cookie`, etc)
- **Authentication Manager** : `Standard API` interface used by filter
- **Authentication Provider** : The implementation of Authentication (`in memory`, `database`, etc)
- **User Details Service** : Service to provide information about `user`
- **Password Encoder** : Service to `encrypt` and `verify` **passwords**
- **Security Context** : Holds details about `authenticated entity`

![pic](images/spring-filters.jpg)

#### In Memory User Details Manager

- Implements User Details Service
- Used by Spring Boot Auto-configuration
- Non-persistent implementation - uses in-memory map
- Mainly used for testing and demonstration purposes, Not normally used in production systems


 **Adding two users**:  admin with the role of admin and then user password with a role of user, both get wired into in memory user details manager and under the cover it will be using a hash map to store these user credentials and inside the memory of the JVM.
 ```java
 @Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
	//the same as above
	...
	}

    @Override
    @Bean //to bring UserDetailsService to spring context
    //In Memory User Details Manager
    protected UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("password")
                .roles("ADMIN")
                .build();

        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin,user);
    }
}
```
 
> We could comment out `spring.security.user.name=admin` and `spring.security.user.password=password` because these are not being used anymore, and we are providing our own implementation through **UserDetailsService**, Spring Boot won't be providing the auto configuration. So these properties get simply ignored by Spring Boot (our tests still pass).
 
 
#### In Memory Authentication Fluent API

**UserDetailsService** is not suitable for more complicated scenarios. The Fluent API that is available within Spring Security is more flexible.

> Remember to specify **{noop}** encoder.
```java
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //we need to specify {noop} encoder.
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("{noop}password")
                .roles("ADMIN")
                .and()
                .withUser("user")
                .password("{noop}password")
                .roles("USER");

        //We could use 'and' to chain or start over as follow:

        auth.inMemoryAuthentication()
                .withUser("generic")
                .password("password")
                .roles("DISABLED");

    }
```
