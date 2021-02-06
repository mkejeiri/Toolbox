# Spring Security Core - Spring MVC Monolith

## Remember Me cookie 

- Remember Me is a technique of allowing a web application to **Remember** the **login details** of a **user**.
- Allows user to **stay signed** into web application, without having to **login again** : **logins** in **Java** are typically tracked with a **session id**, which is **short lived**.
- **Remember Me** is implemented by **storing user details** in a **cookie** : 
	- Application uses **cookie details** to **authenticate** user upon their return.
	- **Cookie** can be set to **expire** after X period of time.

Problems
---------

- **Data** in **Remember Me** is used to **authenticate**.
- If **compromised**, the Remember Me cookie could be used to **impersonate the user** : Effectively a **username and password** rolled into a cookie
- **Best practice** is to **never send** Remember Me cookies over **HTTP**, Always use **HTTPS** to protect cookies from third parties.

Precautions
---------

- Due to potential compromise of Remember Me Cookies, **sensitive functions** should be **restricted**.
- **Spring Security** has methods for `isRemembered` or `isAuthenticatedFully`
- Require **full authentication** for **functions** such as:
	- **Password change**.
	- **Email change**.
	- **Update** of personal information - name, address, payment information, etc...
	- Making **purchases**.



Spring Security Remember Me
------
- **Spring Security** provides two remember me implementations.
- Simple **Hash-Based Token**.
- **Persistent Token**. 
- Both implementations required a **UserDetailsService**.
- Not all **authentication** provides have a **UserDetailsService** : e.g. LDAP


#### Simple Hash-Based Token

- The **Simple Hash-Based Token** is a **Base64 string** consisting of: `base64(username + ":" + expirationTime + ":" + md5Hex(username + ":" + expirationTime + ":" password + ":" + key))`.
- The advantage of having the **password** in the **hash**, is the user can **change** their **password** and **invalidate** **all remember me tokens**.
- Can **support** multiple browsers/computers.
- **Attacker** can use **cookie** until it's **expired** or **password** **changed**.

#### Persistent Token

- **Remember Me Cookie** contains: **username**, a **series id**, and a **token** (**random** string) : These values are **persisted** to the **database**.
- On **login** via **Remember Me** **values** are **fetched** from **database**:
	- If **matched**, user is **authenticated**, and **new token** is **created** for **series id** (browser/devices).
	- If **username** and **series match**, but **token does not**, **theft** is **assumed** : **Delete all tokens for user**.
- **Shortens attack window** until **real user** logs in VS **expirationTime**.
- From Drupal CMS.

[More on Persistent login cookie best practice](https://web.archive.org/web/20080825010602/http://fishbowl.pastiche.org/2004/01/19/persistent_login_cookie_best_practice/)


Simple Hash-Based Token Remember Me
---------


**Spring security** by default looks for the **property** `remember-me`, we could change that but we need to instruct **Spring security** for new used **value**.

`index.html`
```html
		<form th:action="@{/login}" method="post">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" autofocus="true" autocomplete="off">
            <label for="password">Username:</label>
            <input type="password" id="password" name="password" autofocus="true" autocomplete="off">
            <input type="submit" value="Log In">
            <label for="remember-me">Remember Me:</label>
            <input type="checkbox" id="remember-me" name="remember-me" />
        </form>
```

**Configure** the **remember me** process in the [SecurityConfig.java](src/main/java/com/elearning/drink/drinkfactory/config/SecurityConfig.java).

```java
	...
	private final UserDetailsService userDetailsService;
	...
	
	.httpBasic()
    .and().csrf().ignoringAntMatchers("/h2-console/**", "/api/**")
    .and()
    .rememberMe()
		//Key value built into the hash.
        .key("drink-key")
		//we had to add userDetailsService to make rememberMe work, otherwise apps could get away with it.
        //java.lang.IllegalStateException: UserDetailsService is required.	
		.userDetailsService(userDetailsService)
```

`remember-me` **Cookie structure**:

`remember-me` **cookie** :
- `YWRtaW46MTYxMzgxODE1ODgxMzo1MzIyMDNiZmQ0ZjBmOWQ3YzIxNjgwMjgwMDUzZjgwMw`
- Base64 decoded : `admin:1613818158813:532203bfd4f0f9d7c21680280053f803`

| user|expiration date| hashed password and key value|
| ------------- |:-------------:| -----:|
| `admin`| `1613818158813` | `532203bfd4f0f9d7c21680280053f803` |

 `key value` is random key not shared with general public. **Note** that `532203bfd4f0f9d7c21680280053f803` is a hash value which very difficult to reverse.


				 
- Without `remember-me` **cookie**, if we delete `JSESSIONID` we get logged out. We always receive some kind of `JSESSIONID` from the server, but we won't be able to login with that.  
- With `remember-me` **cookie**, even if we delete `JSESSIONID` we stay logged in (i.e. see `Previously Authenticated` in the output).
```
DEBUG 2660 --- [nio-8080-exec-2] o.s.s.w.a.i.FilterSecurityInterceptor    : Previously Authenticated:
 org.springframework.security.authentication.RememberMeAuthenticationToken@4a87d2be: Principal:
 com.elearning.drink.drinkfactory.domain.User@1938ccc8; Credentials: [PROTECTED]; Authenticated: true; Details:
 org.springframework.security.web.authentication.WebAuthenticationDetails@b364: RemoteIpAddress: 0:0:0:0:0:0:0:1;
 SessionId: null; Granted Authorities: drink.update, customer.read, brewery.delete, order.create, customer.create, 
 drink.delete, brewery.update, drink.read, brewery.create, brewery.read, customer.delete, order.delete, order.read, 
 drink.create, order.update, order.pickup, customer.update
```




