# Spring Security Core :  User Lockout with Spring Security Events


Spring Security Authentication Events
--------
- **Spring Security** will send an **Authentication Success** or **Authentication Failure** **Event** with every **Authentication Attempt**.
- These **events** are important **hooks** for **monitoring** **system access**.
- Can be used for:
	- **Logging** who **logged in** **when** and from **where**.
	- **Failed log** in attempts.
	- Automatically **lock accounts** after too **many attempts**.	
- **Spring Security Authentication Events** use the **Event functionality** found in Spring Framework.
- **Publish/Subscribe** Type of Model:
	- Spring Security will **publish the event**
	- **One or more listeners** can **register** to **receive** the **event**.
- **Available** in all Spring Security Provided **Authentication Providers** : We have to write our own **event publishing**, if using  a **custom provider**.	

Default Event Mappings Authentication Events
---------
- `BadCredentialsException` = `AuthenticationFailureBadCredentialsEvent`
- `UsernameNotFoundException` = `AuthenticationFailureBadCredentialsEvent`
- `AccountExpiredException` = `AuthenticationFailureExpiredEvent`
- `ProviderNotFoundException` = `AuthenticationFailureProviderNotFoundEvent`
- `DisabledException` = `AuthenticationFailureDisabledEvent`
- `LockedException` = `AuthenticationFailureLockedEvent`
- `AuthenticationServiceException` = `AuthenticationFailureServiceExceptionEvent`
- `CredentialsExpiredException` = `AuthenticationFailureCredentialsExpiredEvent`
- `InvalidBearerTokenException` = `AuthenticationFailureBadCredentialsEvent`


#### Customization of Events
**Spring Security can be configured** for additional or **custom Authentication Events**, we could provide our own instance of **AuthenticationEventPublisher**, and it could be **customized as needed**.


**Step 1** :  add **AuthenticationEventPublisher** `@Bean` into [SecurityConfig.java](src/main/java/com/elearning/drink/drinkfactory/config/SecurityConfig.java)


```java
 @Bean
    public AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher){
	//If we need to do customizations, we'd provide a customized instance of this.
        return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
    }
```

**Step 2** :  create [AuthenticationSuccessListener](src/main/java/com/elearning/drink/drinkfactory/security/AuthenticationSuccessListener.java)
```java
@Slf4j
@Component
    public class AuthenticationSuccessListener {

    @EventListener
    //registering this method as an EventListener, and spring framework will look for the @EventListener annotation,
    //and then when we have an event with the type of AuthenticationSuccessEvent, this listen methode will get invoked.
    public void listen(AuthenticationSuccessEvent event) {
        log.debug("User Logged In Okay");
    }
}
```

#### Logging of Authentication Success Events

We could use a **debugger to introspect** the type of **AuthenticationSuccessEvent** **object** that we dealing at **runtime** and then provide a logging based in that, we could log also activity into a database. 

```java
@Slf4j
@Component
public class AuthenticationSuccessListener {

    @EventListener
    //registering this method as an EventListener, and spring framework will look for the @EventListener annotation,
    //and then when we have an event with the type of AuthenticationSuccessEvent, this listen method will get invoked.
    public void listen(AuthenticationSuccessEvent event) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

        if(token.getPrincipal() instanceof User){
            User user = (User) token.getPrincipal();

            log.debug("*** User name logged in: " + user.getUsername() );
        }

        if(token.getDetails() instanceof WebAuthenticationDetails){
            WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();

            log.debug("*** Source IP: " + details.getRemoteAddress());
        }
    }
}

```

#### Logging of Authentication Failure Events

**Spring Security** leverages the **events feature** found in the **core** of the **Spring Framework**.

```java
@Slf4j
@Component
public class AuthenticationFailureListener {
    @EventListener
    //registering this method as an EventListener, and spring framework will look for the @EventListener annotation,
    //and then when we have an event with the type of AuthenticationFailureBadCredentialsEvent, this listen method will get invoked.
    public void listen(AuthenticationFailureBadCredentialsEvent event){
        log.debug("Login failure");

        if(event.getSource() instanceof UsernamePasswordAuthenticationToken){
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

            if(token.getPrincipal() instanceof String){
                log.debug("Attempted Username: " + token.getPrincipal());
            }

            if(token.getDetails() instanceof WebAuthenticationDetails){
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();

                log.debug("Source IP: " + details.getRemoteAddress());
            }
        }
    }
}
```

#### Persistence of Authentication Success Events

**Step 1** : create `LoginSuccess`  **entity**

```java
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class LoginSuccess {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    private User user;

    private String sourceIp;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;
}
```

**Step 2** : create `LoginSuccessRepository`  **repository**

```java
package com.elearning.drink.drinkfactory.security;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginSuccessRepository extends JpaRepository<LoginSuccess, Integer> {
}

```

**Step 3** : inject `LoginSuccessRepository`  **repository** into `AuthenticationSuccessListener`

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener {
    private final LoginSuccessRepository loginSuccessRepository;
    @EventListener
    //registering this method as an EventListener, and spring framework will look for the @EventListener annotation,
    //and then when we have an event with the type of AuthenticationSuccessEvent,
    //this listen method will get invoked.
    public void listen(AuthenticationSuccessEvent event) {
        LoginSuccess.LoginSuccessBuilder builder = LoginSuccess.builder();

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

        if (token.getPrincipal() instanceof User) {
            User user = (User) token.getPrincipal();
            builder.user(user);
            log.debug("*** User name logged in: " + user.getUsername());
        }

        if (token.getDetails() instanceof WebAuthenticationDetails) {
            WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
            builder.sourceIp(details.getRemoteAddress());
            log.debug("*** Source IP: " + details.getRemoteAddress());
        }
        LoginSuccess loginSuccess = loginSuccessRepository.save(builder.build());
        log.debug("Login Success saved. Id: " + loginSuccess.getId());
    }
}
```


Locking User Account After Failed Attempts
------------

**Step 1** : create `LoginFailure`  **entity**

```java
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class LoginFailure {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;

    @ManyToOne
    private User user;

    private String sourceIp;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;
}
```


**Step 2** : create `LoginFailureRepository`  **repository**, and `findAllByUserAndCreatedDateIsAfter` to count how many times the user **enter a bad credentials to slow down a brute force attack** by locking that account.

```java
package com.elearning.drink.drinkfactory.security;

import com.elearning.drink.drinkfactory.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface LoginFailureRepository extends JpaRepository<LoginFailure, Integer> {
    List<LoginFailure> findAllByUserAndCreatedDateIsAfter(User user, Timestamp timestamp);
}
```


**Step 3** : inject `LoginFailureRepository`  **repository** into `AuthenticationFailureListener` and add `lockUserAccount` method.

```java
@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationFailureListener {
    private final LoginFailureRepository loginFailureRepository;
    private final UserRepository userRepository;

    @EventListener
    //registering this method as an EventListener, and spring framework will look for the @EventListener annotation,
    //and then when we have an event with the type of AuthenticationFailureBadCredentialsEvent, this listen
    //method will get invoked.
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        log.debug("Login failure");

        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            LoginFailure.LoginFailureBuilder loginFailureBuilder = LoginFailure.builder();
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

            if (token.getPrincipal() instanceof String) {
                log.debug("Attempted Username: " + token.getPrincipal());
                loginFailureBuilder.username(token.getPrincipal().toString());
                userRepository.findByUsername((String) token.getPrincipal()).ifPresent(loginFailureBuilder::user);
            }

            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
                loginFailureBuilder.sourceIp(details.getRemoteAddress());
                log.debug("Source IP: " + details.getRemoteAddress());
            }
            LoginFailure loginFailure = loginFailureRepository.save(loginFailureBuilder.build());
            log.debug("Login Failure saved. Id: " + loginFailure.getId());

            if (loginFailure.getUser() != null) {
                lockUserAccount(loginFailure.getUser());
            }
        }
    }

    private void lockUserAccount(User user) {	
        List<LoginFailure> failures = loginFailureRepository.findAllByUserAndCreatedDateIsAfter(user,
                Timestamp.valueOf(LocalDateTime.now().minusDays(1)));

		//three failed attempts in 24 hours period.
		//We look the account.
        if (failures.size() > 3) {
            log.debug("Locking User Account... ");
            user.setAccountNonLocked(false);
            userRepository.save(user);
        }
    }
}
```

#### Show friendly message to the end-user:


**Spring security** uses `SimpleUrlAuthenticationFailureHandler`, when an exception occurs (e.g. bad credentials or user account locked), `onAuthenticationFailure` method get invoked and call `saveException(request, exception)` : 

```java
protected final void saveException(HttpServletRequest request,
			AuthenticationException exception) {
		if (forwardToDestination) {
			request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
		}
		else {
			HttpSession session = request.getSession(false);

			if (session != null || allowSessionCreation) {
				request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION,
						exception);
			}
		}
	}

```

The `saveException` method put an attribute of the exception `AUTHENTICATION_EXCEPTION` into the session `request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION`, i.e. with the key `SPRING_SECURITY_LAST_EXCEPTION`.

if we put `<h2 th:text="${session['SPRING_SECURITY_LAST_EXCEPTION']?.message}"></h2>` into `index.html`, we will see two types of error messages:

- `Bad credentials`
- `User account is locked`

We could use them to diplay a user friendly message, rather than use the **query param**:

```html
      <div th:if="${session['SPRING_SECURITY_LAST_EXCEPTION']?.message} == 'Bad credentials'" class="alert alert-danger">Invalid Username or Password</div>
        <div th:if="${session['SPRING_SECURITY_LAST_EXCEPTION']?.message} == 'User account is locked'" class="alert alert-danger">User Account is Locked.</div>
```


