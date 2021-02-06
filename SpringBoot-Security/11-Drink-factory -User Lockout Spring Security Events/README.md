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

**Step 2** : inject `LoginSuccessRepository`  **repository** into `AuthenticationSuccessListener`

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




