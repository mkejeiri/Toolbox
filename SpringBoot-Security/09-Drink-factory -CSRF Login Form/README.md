# Spring Security Core - Spring MVC Monolith

## Login Security

In this section we will : 

- Configure Spring Security to use Login form.
- Show login form if not logged in, redirect to login form on unauthorized access.
- Hide login form and show logout link if logged in.
- Update Application to show or hide menu options and buttons based on user permissions.



Spring Security Tag Libraries
-------
- **Spring Security** provides a **tag library** for working with **JSP Templates**.
- **Thymeleaf** provides a **tag library** with **feature parity** for **Thymeleaf templates** : Features and functionality are the **same** between **JSP** and **Thymeleaf**.


For **Thymeleaf**:
- Requires **dependency** `org.thymeleaf.extras:thymeleaf-extras-springsecurity5`.
- Add **namespace**: `xmlns:sec="http://www.thymeleaf.org/extras/spring-security"`.


Tag Library Features
-------
- **Spring Security Expression** methods in **SPeL** exposed : i.e. **hasRole()**, **hasAnyAuthority()**, etc...
- **Authentication object available** : implementation of **Spring Security Authentication interface**.
- **Tag** `sec:authentication="xxxx"` : outputs value of named property.
- **Tag** `sec:authorize="expr"` or `sec:authorize-expr="expr"` : **renders element** based on **SPeL**.
expression
- **Tag** `sec:authorize-url="url"` : **renders element** if **user** is **authorized** to **view url**.
- **Tag** `sec:authorize-acl="object::permissions"` : **renders element** based on **ACL permissions**.



### Add Login form for Security

- Display a login error: `loginError` is a default ** property**  that could set upon an error.
- using ** Thymeleaf**  to create a form.
- post to the ** login URL**  using a method post : `th:action="@{/login}"`.
- `username` and `password` **properties** will get submitted to **Spring Security**.

Note to logout using the default **Spring Security** : `http://localhost:8080/logout`

```html
<div class="col-md-12">
        <p th:if="${loginError}" class="error">Wrong Username or password</p>
        <form th:action="@{/login}" method="post">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" autofocus="true" autocomplete="off">
            <label for="password">Username:</label>
            <input type="password" id="password" name="password" autofocus="true" autocomplete="off">
            <input type="submit" value="Log In">
        </form>
</div>
```

**Add dependency**
```
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity5</artifactId>
</dependency>
```

**Add xmlns namespace** `xmlns:sec="http://www.thymeleaf.org/extras/spring-security`

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org" th:replace="~{fragments/layout :: layout (~{::body},'home')}">
<html lang="en" xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
      th:replace="~{fragments/layout :: layout (~{::body},'home')}">

```

**Display the username when authenticated** : 

```html
<div class="row">
    <div class="col-md-12">
        <p sec:authorize="isAuthenticated()" th:text="'Welcome: ' + ${#authentication?.principal?.username}">User</p>
        <p><a th:href="@{/logout}">Logout</a></p>
    </div>
</div>
```

> Spring provides **Spring Security tags** for **JSP templates** with nearly the **same functionality** as we see with **Thymeleaf**.


**Refactore login and logout** using `sec:authorize="!isAuthenticated()"` or `sec:authorize="isAuthenticated()"`

```html
<!--show only if not authenticated-->
<div class="row" sec:authorize="!isAuthenticated()">
    <div class="col-md-12">
        <p th:if="${loginError}" class="error">Wrong Username or password</p>
        <form th:action="@{/login}" method="post">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" autofocus="true" autocomplete="off">
            <label for="password">Username:</label>
            <input type="password" id="password" name="password" autofocus="true" autocomplete="off">
            <input type="submit" value="Log In">
        </form>
    </div>
</div>

<!--show only if authenticated-->
<div class="row" sec:authorize="isAuthenticated()">
    <div class="col-md-12">
        <p th:text="'Welcome: ' + ${#authentication?.principal?.username}">User</p>
        <p><a th:href="@{/logout}">Logout</a></p>
    </div>
</div>
```

Login/Logout configuration
--------

Using `formLogin` and `logout` methods we configure the **pages** that handles **login** and **logout** and also **redirection**.

The **login page** is the **index page**, but we could also use different page (i.e. lot of flexibility).
Some applications are going to have their own login page where we will **show** the **login page** and then **redirect back** to that **application**, here we are building it into the **index page**. When the login page (i.e. index) **success**, we **forward** as well the **default** to the **index page**.


For the **logout behavior**, We use `logoutRequestMatcher`, because **spring security** **default behavior** is expecting a **post** against **logout**, so we need to **specify** and allow a `GET` against the **logout**. If we're using **JavaScript** and we're able to do a **post action** we can **omit** this then.



```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
...
@Override
    protected void configure(HttpSecurity http) throws Exception {
       ...
                .formLogin(httpSecurityFormLoginConfigurer ->
                        {
                            httpSecurityFormLoginConfigurer
                                    .loginProcessingUrl("/login")
                                    //we use index as a default page for the login (customizable)
                                    .loginPage("/").permitAll()
                                    //on success we forward to the index page (customizable)
                                    .successForwardUrl("/")
                                    //redirect everything to index page (customizable)
                                    .defaultSuccessUrl("/");
                        }

                ).logout(httpSecurityLogoutConfigurer -> {
                    httpSecurityLogoutConfigurer
                    //Since we have a link to logout (i.e. get request) and spring security
                    //handles post request for logout we need to override.
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                    .logoutSuccessUrl("/")
                    .permitAll();
        })
                .httpBasic()
                .and().csrf().ignoringAntMatchers("/h2-console/**", "/api/**");

        //by default, Spring Security is preventing frames
        http.headers().frameOptions().sameOrigin();
    }
...

}

```

#### Login and Logout Messages


To be able to use `alert/success` bootstrap formating to display **end user** **messages**, we **add** into the **URL** :
- **error  param** (i.e. `?error`)
- **logout  param** (i.e. `?logout`)

`error` or `logout`, allows the UI to **key off** those **values** and **provide feedback** to the **end user**.


```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
...
@Override
    protected void configure(HttpSecurity http) throws Exception {
       ...
                .formLogin(httpSecurityFormLoginConfigurer ->
                        {
                            httpSecurityFormLoginConfigurer
                                    .loginProcessingUrl("/login")
                                    //we use index as a default page for the login (customizable)
                                    .loginPage("/").permitAll()
                                    //on success we forward to the index page (customizable)
                                    .successForwardUrl("/")
                                    //redirect everything to index page (customizable)
                                    .defaultSuccessUrl("/");
									//error param used by the UI to display alert incorrect username/password
                                    .failureUrl("/?error");
									
                        }

                ).logout(httpSecurityLogoutConfigurer -> {
                    httpSecurityLogoutConfigurer
                    //Since we have a link to logout (i.e. get request) and spring security
                    //handles post request for logout we need to override.
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                    .logoutSuccessUrl("/")
					 //error param used by the UI to display success of logout
                    .logoutSuccessUrl("/?logout")
                    .permitAll();
        })
                .httpBasic()
                .and().csrf().ignoringAntMatchers("/h2-console/**", "/api/**");

        //by default, Spring Security is preventing frames
        http.headers().frameOptions().sameOrigin();
    }
...

}

```

In **UI** we use those params (i.e. `?error` & `?logout`) to **display messages**

```html
<div th:if="${param.error}" class="alert alert-danger">Invalid Username/Password</div>
<div th:if="${param.logout}" class="alert alert-success">You Have Logged Out</div>
```


**show Elements based on User Permission**

Go to `findDrinks.html` template and add the following :

- `xmlns:sec="http://www.thymeleaf.org/extras/spring-security"` to the template
- `sec:authorize="hasAuthority('drink.create')"`, so that tag, it will render this element if the **logged-in user** has authority of `drink.create`.

`<a sec:authorize="hasAuthority('drink.create')" class="btn btn-default" th:href="@{/drinks/new}">Add Drink</a>`
