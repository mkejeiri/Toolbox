# Spring Security Core : CORS

Cross-Origin Resource Sharing - CORS
-------

- **Cross-Origin Resources** are common in web applications.
- A typical web page may use **Cross-Origin Resources** for:
	- **Images/videos**.
	- **Content Delivery Network** (CDN).
	- **Web fonts**.
- Some **Cross-Origin Requests** are **forbidden** (mainly **Ajax**).
- **CORS** is a **standard** to **enable** **Cross-Origin Requests** when needed.
- **CORS** is a **browser standard**, it is the **browser** implementing the **security restrictions**: **Server** provides **data**, browser implements.
- **Browser** displaying page from `myapplication.com`, Javascript makes request from `api.myapplication.com` : **Browser** performs **HTTP GET** at `api.myapplication.com` ; with **HTTP Header** `Origin:myapplication.com`

- **Server** Responds with **header** `Access-Control-Allow-Origin: myapplication.com`
	- This indicates the **api domain** can be **accessed** from **root domain**.
	- Can be an **asterisk** `(*)`: **wildcard** for **all domains**.
- An Example of this could be:
	- a **ReactJS** application running at `myapplication.com`.
	- **Spring Boot application** running at `api.myapplication.com`.
	

CORS Headers
-----	

- **Request Headers** coming from browser:
	- `Origin`
	- `Access-Control-Request-Method`.
	- `Access-Control-Headers`.
	
- **Response Headers** coming from server:
	- `Access-Control-Allow-Origin`.
	- `Access-Control-Allow-Credentials`.
	- `Access-Control-Expose-Headers`.
	- `Access-Control-Max-Age`.
	- `Access-Control-Allow-Methods`.
	- `Access-Control-Allow-Headers`.

CORS Preflight
-----
- **Browsers** can also do a `preflight` to check if an action is allowed with the server.
- **Browser** makes a `HTTP OPTIONS` request using **request headers** for **method** (`Access-Control-Request-Method`) and/or **headers** (`Access-Control-Headers`).
- **Server** responds with `HTTP 204` if **okay**, **error** if **not**.


Spring Framework CORS Support
------------
- **Spring MVC/WebFlux**.
	- Majority of **CORS** support is **built** into **Spring MVC/WebFlux**.
	- **Configuration** can be very **granular**.
- **Spring Security**
	- Works in **conjunction** with **Spring MVC/Webflux**.
	- Alternatively can **Intercept requests** in **security filter chain**.
	
> We could work either with - **Spring security** to apply the **CORS request** inside the **spring security filter chain**, so it gets **intercepted** ahead of **servlet dispatcher** of **spring MVC**, or with - **spring security** within **Spring MVC/Webflux** but **NOT BOTH**.



Disable Spring Security for Testing
-----

**Step 1** - add [CorsIT](src/test/java/com/elearning/drink/drinkfactory/web/controllers/api/CorsIT.java) integration test.
We are emulating through a ** spring mock MVC test**  the ** browser**  ** pre-flight**  ** checks** .
```java
@SpringBootTest
public class CorsIT extends BaseIT {
    @WithUserDetails("admin")
    @Test
    void findDrinksAUTH() throws Exception {
        mockMvc.perform(get("/api/v1/drink/")
                .header("Origin", "https://elearning.drinkfactory"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void findDrinksPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/drink/")
                .header("Origin", "https://elearning.drinkfactory")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void postDrinksPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/drink/")
                .header("Origin", "https://elearning.drinkfactory")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void putDrinksPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/drink/1234")
                .header("Origin", "https://elearning.drinkfactory")
                .header("Access-Control-Request-Method", "PUT"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void deleteDrinksPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/drink/1234")
                .header("Origin", "https://elearning.drinkfactory")
                .header("Access-Control-Request-Method", "DELETE"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }
}
```

 
**Step 2** - in `SecurityConfig` disable all  configuration and  enabled Web security.
```java
@RequiredArgsConstructor
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true)
//@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {...}
```


**Step 3** - Remove `PasswordEncoder` `Bean` into `SecurityBeans`
```java
    @Bean
    PasswordEncoder passwordEncoder() {
        return CustomPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
```


**Step 4** - **Spring security** will still be brought in and we will get **spring** `SecurityAutoConfiguration.class` and  `ManagementWebSecurityAutoConfiguration`, we need to **exclude them** and disable them from spring context.

- `SecurityAutoConfiguration.class` : is the **primary one**.
- `ManagementWebSecurityAutoConfiguration`: handles **Spring boot actuator**.

```java
//@SpringBootApplication
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
public class DrinkFactoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrinkFactoryApplication.class, args);
    }

}
```

**Step 4** - disable **Spring security** from Integration text [BaseIT.java](src/test/java/com/elearning/drink/drinkfactory/web/controllers/BaseIT.java) (i.e. `apply(springSecurity()`).
 
 
```java
public abstract class BaseIT {
    @Autowired
    WebApplicationContext wac;

    protected MockMvc mockMvc;  

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                //.apply(springSecurity())
                .build();
    }
...	
 }

``` 