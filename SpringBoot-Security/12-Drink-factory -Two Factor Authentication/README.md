# Spring Security Core :  Two Factor Authentication

- **Two Factor Authentication** is a type of **multi-factor** **authentication** , also called **2FA**.
- **2FA Authentication** requires the **user** to present **two** or more **authentication factors**.
- **Goal** is to prevent **unauthorized account access** from **account password** being **compromised**.
- Could be as simple as **username, password**, and **code sent** via **text message**, thus, **user needs** to know **password** and have **device receiving** text message **code**.
- **2FA** should use two different **Authentication factors**.

**Type of authentication factors**:

- **Something You Have** : A **bank card**, a **USB Key** with **code**, **FOB** with **code**.
- **Something You Know** : Knowledge of the **user**; **password**, **PIN**, **security question**.
- **Something You Are** : Biometric; **fingerprint**, **iris** or **face scan**.
- **Somewhere You Are** : A **location physical**, or **GPS** based.


**Time-Based One-Time Password**:

- **Time-Based** **One-Time** **Password** : Unique **code**, **valid** for ~**30 seconds**, aka **TOTP**.
- Adopted by Internet Engineering Task Force (IETF) under RFC 6328.
- **Algorithm** based on **Unix time**: **Integer** of **seconds** since **January 1, 1970** (dropping any leap seconds)
- Uses a **shared** `secret`, which if **compromised** will allow **attacker** to **generate codes**.

Google Authenticator
---------

- **Open Source TOTP** generator for **Android** or **iOS**: Allows user to easily **setup TOTP** via scanning a **QR Code**.
- **QR Code** generated using 
	- `Label` : **Account Name** (username)
	- `Secret` : **Arbitrary key** value **Base32 encoded** (shared secret, should be protected): Unique to user.
- **Issuer** : Organization Issuing **TOTP**.

**Example of QR Code Generation URI**
- `otpauth://TYPE/LABEL?PARAMETERS`
- `otpauth://totp/ACME%20Co:john@example.com?secret=HXDMVJECJJWSRB3HWIZR4IFUGFTMXBOZ&issuer=ACME%20Co&algorithm=SHA1&digits=6&period=30`



Google Authenticator with Spring Security
---------

- **Goal**: Configure **Spring Security** to use **Google Authenticator** for **2FA**.
- **Google Authenticator Registration**: Scan **QR Code** to register application.
- Provide User Ability to **opt-in** to **2FA**.
- **Users** who have opted into **2FA** required to enter **2FA code** after **login**.
- **Users** **not opted in**, do **not need** to enter **code**.

Spring Security Configuration
----------
- **Update User Entity** to **hold**:
	- Use **2Fa?**: Is user registered for **2FA**.
	- 2FA **Secret**: **Shared Secret** with Google Authenticator.
	- 2FA **Required?**: **Transient** Property to **require** entering of **2FA code**.
- **Spring Security 2FA Filter** : **On Login**, if User is **2FA enabled**, **forward** to **2FA** Authentication **URL**.


2FA Registration
----------
- Add **Menu** Option and page to **Enable 2FA**.
- **Controller** will **update User** with **2FA secret** and show **QR code**.
- **Accept Form Pos**t of **TOPT code** from Google Authenticator:
	- If **valid**, **enable 2FA** for user.
	- **Redirect** to **index**.

2FA Login
-------
- **Accept** **username** **password** as normal.
- If user is **2FA enabled forward** to **2FA verification page**.
- Use Spring Security **Filter** for **forward**.
- Filter will **restrict user** to **verification** page until proper **code** is **entered**.


> This Example is used for Spring MVC, will work for most typical web applications (Traditional Spring MVC application), it's also suitable for Single Page Javascript, e.g. Angular, ReactJS,...


Configure User Entity for 2FA
----------

**Update** the [user.java](src/main/java/com/elearning/drink/drinkfactory/domain/security/User.java) **entity**:
- Have they **registered?** (i.e `useGoogle2fa`)
- **If** they have **registered**, what's the **secret**? (i.e `google2FaSecret`)
- `google2faRequired` is **transient** property which is only used when the **user** object has been set within the **spring security context** to remember the way the authentication flow went through (and `google2faRequired` set to false afterward).

**Scenario** :

1- User log in with username and password. 

2- User object set into the spring security context.

3- User go through the two factor authentication process.



```java
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
//@Table(name = "Users")
public class User implements UserDetails, CredentialsContainer {

...

    @Builder.Default
    //is user will use the google auth?
    private Boolean useGoogle2fa = false;

    //property to hold a secret
    private String google2FaSecret;

    @Transient //set on the User POJO and not persisted
    //set only when user object is filled in within spring security context and used as a property by the filters
    private Boolean google2faRequired = true;
...
}

```
The **User** is **forced** to **authenticate** when `useGoogle2fa=true` and `google2faRequired=true`.
Once the **user enter-in** the **proper** authentication **value**, `google2faRequired` become **false**, i.e. no **longer needed** since the **user** passed through the **Two-Factor authentication**. This operation is **only triggered** if `useGoogle2fa==true`.


Configure 2FA Registration Controller
------------------

**Step 1** - Add controller to handles 2FA.

```java
@Slf4j
@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/register2fa")
    public String register2fa(Model model){

        model.addAttribute("googleurl", "todo");

        return "user/register2fa";
    }

    @PostMapping
    public String confirm2Fa(@RequestParam Integer verifyCode){

        //todo - impl

        return "index";
    }
}
```

**Step 2** - update layout.html to add menuItem `Enable 2FA`


```html
<li th:replace="::menuItem ('/user/register2fa','user','register','th-list','Enable 2FA')">
    <span class="glyphicon glyphicon-wrench" aria-hidden="true"></span>
    <span>Enable 2FA</span>
</li>
```


**Step 3** - Create the template `register2fa.html` to be shown when the menuItem `Enable 2FA` is clicked.
```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org"
      th:replace="~{fragments/layout :: layout (~{::body},'home')}">
<head>
    <meta charset="UTF-8"/>
    <title>Two Factor QR Code</title>
</head>
<body>
<h2>Scan QR Code using Google Authenticator, enter code to verify</h2>

<div class="row">
    <div class="col-md-12">
        <img th:src="${googleurl}"/>
    </div>
</div>
<div class="row">
    <div class="col-md-4">

        <form th:action="@{/user/register2fa}" class="form-horizontal" id="verify-code-form" method="post">
            <div class="form-group has-feedback">
                <label class="control-label" for="verifyCode">Enter Code</label>
                <input class="form-control" type="number" id="verifyCode" name="verifyCode" autofocus="true" autocomplete="off" b />

                <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>

            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button class="btn btn-default" type="submit" >Verify Code</button>
                </div>
            </div>
        </form>
    </div>
</div>


</body>
</html>
```

Configure Google Secret Persistence
-----------

We will use the [googleauth](https://github.com/wstrange/GoogleAuth) library:

**Step 1** - add the dependency

```xml
<dependency>
  <groupId>com.warrenstrange</groupId>
  <artifactId>googleauth</artifactId>
  <version>1.5.0</version>
</dependency>

```

**Step 2** - Create [GoogleCredentialRepository.java](src/main/java/com/elearning/drink/drinkfactory/security/google/GoogleCredentialRepository.java) that will **get** the **secret** and **save** **secret** into the **database**.
```java
@Slf4j
@RequiredArgsConstructor
@Component
public class GoogleCredentialRepository implements ICredentialRepository {

    private final UserRepository userRepository;

    @Override
    public String getSecretKey(String userName) {
        User user = userRepository.findByUsername(userName).orElseThrow();

        return user.getGoogle2FaSecret();
    }

    @Override
    public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
        User user = userRepository.findByUsername(userName).orElseThrow();
        user.setGoogle2FaSecret(secretKey);
		//user opt-in for F2A
        user.setUserGoogle2fa(true);
        userRepository.save(user);
    }
}

```


Generate QR Code for Google Authenticator
-----------
**Step 1** - **create** [GoogleAuthenticator](src/main/java/com/elearning/drink/drinkfactory/config/SecurityBeans.java) `Bean`.
```java
@Configuration
public class SecurityBeans {

    @Bean
   ...

    @Bean
    public GoogleAuthenticator googleAuthenticator(ICredentialRepository credentialRepository) {
        GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder configBuilder
                = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder();

        //this token works is based on time of Unix time,
        //while the time on the device and the server could be different.
        configBuilder
                //The time's up will open up the time window.
                .setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(60))
                .setWindowSize(10)
                //no scratch code, no persisted at all
                .setNumberOfScratchCodes(0);

        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator(configBuilder.build());
        googleAuthenticator.setCredentialRepository(credentialRepository);
        return googleAuthenticator;
    }
}
```


**Step 2** - **update** [UserController](src/main/java/com/elearning/drink/drinkfactory/web/controllers/UserController.java)
```java
public class UserController {

    //ISSUER: show up in the Google authenticator as "my application".
    public static final String ISSUER = "eLearning";
    public static final String GOOGLE_URL_ATTRIBUTE_NAME = "googleurl";
    private final UserRepository userRepository;
    private final GoogleAuthenticator googleAuthenticator;

    @GetMapping("/register2fa")
    public String register2fa(Model model) {

        User user = getUser();

        //this will go out to Google Services to create a QR code and returning back an image
        //for us to display on the Web page.
        String url = GoogleAuthenticatorQRGenerator.getOtpAuthURL(ISSUER, user.getUsername(),
                //Instruct Google to create the credentials which will call the save user credentials.
                //i.e. create the credentials when the process happens -> create shared secret
                //and also save it to our database...
                googleAuthenticator.createCredentials(user.getUsername()));

        log.debug("Google QR URL: " + url);

        model.addAttribute(GOOGLE_URL_ATTRIBUTE_NAME, url);

        return "user/register2fa";
    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
	...
}
```



**Step 3** - **Verify 2FA Opt In** (i.e. `confirm2Fa` method), update [UserController](src/main/java/com/elearning/drink/drinkfactory/web/controllers/UserController.java) 
```java
@Slf4j
@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {

    //ISSUER: show up in the Google authenticator as "my application".
    public static final String ISSUER = "eLearning";
    public static final String GOOGLE_URL_ATTRIBUTE_NAME = "googleurl";
    private final UserRepository userRepository;
    private final GoogleAuthenticator googleAuthenticator;
	
	...
	
    @PostMapping("/register2fa")
    public String confirm2Fa(@RequestParam Integer verifyCode) {

        //user from spring security context.
        User user = getUser();

        log.debug("Entered Code is:" + verifyCode);

        //Validate the code: authorizeUser method returns back a boolean.
        //using the username, it will look up the user in the database and get the google2FaSecret
        //and checks that google2FaSecret code does matches with verifyCode using GoogleAuthenticator.checkCode method.
        if (googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {

            //update the verified user because user object is detached from hibernate and
            //it could be stale (i.e. doesn't reflect the latest version from the database).
            User savedUser = userRepository.findById(user.getId()).orElseThrow();

            //setUserGoogle2fa(true): user has completed registration for Two-Factor authentication.
            //default is false.
            savedUser.setUserGoogle2fa(true);
            userRepository.save(savedUser);
            return "/index";

        }
        //if bad code, resubmit the form.
        return "user/register2fa";
    }
....	
}

```


2FA Verfication UI
---------- 

**Step 1** - Add [verify2fa.html](src/main/resources/templates/user/verify2fa.html)
```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org"
      th:replace="~{fragments/layout :: layout (~{::body},'home')}">
<head>
    <meta charset="UTF-8"/>
    <title>Two Factor QR Code</title>
</head>
<body>
<h2>Enter Your Google Auth Code</h2>
<div class="row">
    <div class="col-md-4">

        <form th:action="@{/user/verify2fa}" class="form-horizontal" id="verify-code-form" method="post">
            <div class="form-group has-feedback">
                <label class="control-label" for="verifyCode">Enter Code</label>
                <input class="form-control" type="number" id="verifyCode" name="verifyCode" autofocus="true" autocomplete="false" />

                <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>

            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button class="btn btn-default" type="submit" >Verify Code</button>
                </div>
            </div>
        </form>
    </div>
</div>
</body>
</html>
```

**Step 2** - Add `verify2fa()` and `verifyPostOf2Fa(@RequestParam Integer verifyCode)` methods into [UserController](src/main/java/com/elearning/drink/drinkfactory/web/controllers/UserController.java) 

```java
@Slf4j
@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final GoogleAuthenticator googleAuthenticator;

	...
    //render the forms
    @GetMapping("/verify2fa")
    public String verify2fa() {
        return "user/verify2fa";
    }

    //Verify code
    @PostMapping("/verify2fa")
    public String verifyPostOf2Fa(@RequestParam Integer verifyCode) {

        //pull the user out of spring context
        User user = getUser();

        //Validate the code: authorizeUser method returns back a boolean.
        //using the username, it will look up the user in the database and get the google2FaSecret
        //and checks that google2FaSecret code does matches with verifyCode using GoogleAuthenticator.checkCode method.
        if (googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
            //Proper code entered.
            //in the spring security context, set `google2faRequired Transient` property to `false`,
            //i.e. user entered the authentication code in the Two-Factor properly.
            ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setGoogle2faRequired(false);
            return "/index";
        }

        //bad code return back to verify2fa form.
        return "user/verify2fa";
    }

    ...
}

```
Spring Security 2FA Filter
--------

We will create the initial implementation of spring security Two-Factor authentication.

Add [Google2faFilter.java](src/main/java/com/elearning/drink/drinkfactory/security/google/Google2faFilter.java) filter.

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class Google2faFilter extends GenericFilterBean {

    private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //step 1 -User could be somebody initially logging in, and we hold until
        //the user has authenticated with username and password (step 2).
        if (authentication != null  && !authenticationTrustResolver.isAnonymous(authentication)){
            log.debug("Processing 2FA Filter");

            //Once the user is authenticated with username and password,
            //we will have an instance of the user within spring security context.
            //step 2 - If the user is successfully logged in and has a security context object
            if (authentication.getPrincipal() != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();

                // if the user has 2 factors authentication enabled, and it's required.
                if (user.getUseGoogle2fa() && user.getGoogle2faRequired()) {
                    log.debug("2FA Required");
                    // todo add failure handler
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

Spring Security 2FA Failure Handler
--------

**Step 1** - Add [Google2faFailureHandler.java](src/main/java/com/elearning/drink/drinkfactory/security/google/Google2faFailureHandler.java) which **allow redirection** of the **authenticated user** who not yet **pass through 2fa**!. 
```java
@Slf4j
//standard spring security interface AuthenticationFailureHandler
public class Google2faFailureHandler implements AuthenticationFailureHandler {

    //We could handle any type of exception here,
    //but we're really not in an exception case, we want to capture the user and redirect them to this page.
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.debug("Forward to 2fa");
        request.getRequestDispatcher("/user/verify2fa")
                .forward(request, response);

    }
}
```


**Step 2** - update [Google2faFilter.java](src/main/java/com/elearning/drink/drinkfactory/security/google/Google2faFilter.java) to handle **2FA Required** for an authenticated user (with userName/password).	 

```java
	....
	//We create a new instance of Google2faFailureHandler,
    //we're not injecting anything into it, so it doesn't need to be a spring bean component.
    private final Google2faFailureHandler google2faFailureHandler = new Google2faFailureHandler();
	@Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
	...
               if (user.getUseGoogle2fa() && user.getGoogle2faRequired()) {
                    log.debug("2FA Required");
                    //If user is authenticated but have not entered in yet his code,
                    //we need to redirect him/her to "/user/verify2fa", which's done through onAuthenticationFailure.
                    google2faFailureHandler.onAuthenticationFailure(request, response,
                            //we pass null, we don't handle exceptions.
                            null);
                    
                    //After this authentication failure, we want to return out of method, 
                    //we want to stop the filter chain and then continue, this will fire on all requests.
                    return; 
                }
	...			
    }
				
....
```


**Step 3** - update [Google2faFilter.java](src/main/java/com/elearning/drink/drinkfactory/security/google/Google2faFilter.java) to **skip the filter anything matches static resources**. We want to continue **normally** with the **spring security filter** and **return**. **Otherwise**, the **filter redirect us again** which get us into an **endless loop!**.

```java
@Slf4j
@Component
public class Google2faFilter extends GenericFilterBean {

   ...
    //Static resources in verify2fa and resources
    private final RequestMatcher urlIs2fa = new AntPathRequestMatcher("/user/verify2fa");
    private final RequestMatcher urlResource = new AntPathRequestMatcher("/resources/**");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //request matcher for css, js, img, webjar, favicon common locations!
        StaticResourceRequest.StaticResourceRequestMatcher
                staticResourceRequestMatcher = PathRequest.toStaticResources().atCommonLocations();

        //Skip the filter is anything matches.
        //for static resources we want to continue normally with the spring security filter and return.
        //otherwise the filter redirect again which get us into an endless loop!;
        if (urlIs2fa.matches(request) || urlResource.matches(request) ||
                staticResourceRequestMatcher.matcher(request).isMatch()) {
            filterChain.doFilter(request, response);
            return;
        }


        if (urlIs2fa.matches(request) || urlResource.matches(request) ||
                staticResourceRequestMatcher.matcher(request).isMatch()) {
            filterChain.doFilter(request, response);
            return;
        }

...
    }
}
```

