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