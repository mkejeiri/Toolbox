# Spring Security Core - Spring MVC Monolith

## CSRF Protection in Spring Security

### Enable CSRF Protection for HTML

**Restful APIs** by nature are **stateless** we don't have **cookie values** to work with where the **CSRF token** is often **stored** or in a **form**. 
Working with **SPA application**, we need to provide a **CSRF token**, see **Spring Security documentation** on **Ajax** and **JSON Requests**.

We will look at an **HTML** example and the use case of using pure restful APIs where typically we **do not have CSRF Protection**.

We want to **disable CSRF** for the **H2 Management Console** and for our **restful APIs** (e.g. `csrf().ignoringAntMatchers("/h2-console/**", "/api/**")`), and **enable** **CSRF token** for the **rest** of the **application**.

```java
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class);


        http.addFilterBefore(restParamAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class);

        http                
                .authorizeRequests(expressionInterceptUrlRegistry -> {
                    expressionInterceptUrlRegistry
                            //Permit root path & static assets
                            .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                            .antMatchers("/h2-console/**").permitAll() //don't use in production



                    ;
                })

                //Any other request rules!
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic()
                .and().csrf().ignoringAntMatchers("/h2-console/**", "/api/**")
        ;

        //by default, Spring Security is preventing frames
        http.headers().frameOptions().sameOrigin();
    }

```

--------------

Our **tests** is going over the forms and we have two **failing tests** : 
- test covering the **forms** where we are using **Spring Mock MVC** and performing a **post** through the **test**,
- we are not using the **Web Framework** at all, and we are also not using the **thymeleaf templates**. 

We need to add `with(csrf())`, e.g. `mockMvc.perform(post("/customers/new").with(csrf())` :

```java
 @DisplayName("Add Customers")
    @Nested
    class AddCustomers {

        @Rollback
        @Test
        void processCreationForm() throws Exception{
            mockMvc.perform(post("/customers/new").with(csrf())
                    .param("customerName", "Foo Customer")
                    .with(httpBasic("admin", "password")))
                    //is3xxRedirection: redirect to the view.
                    .andExpect(status().is3xxRedirection());
        }

        @Rollback
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.elearning.drink.drinkfactory.web.controllers.CustomerControllerIT#getStreamNotAdmin")
        void processCreationFormNOTAUTH(String user, String password) throws Exception{
            mockMvc.perform(post("/customers/new").with(csrf())
                    .param("customerName", "Foo Customer2")
                    .with(httpBasic(user, password)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void processCreationFormNOAUTH() throws Exception{
            mockMvc.perform(post("/customers/new").with(csrf())
                    .param("customerName", "Foo Customer"))
                    .andExpect(status().isUnauthorized());
        }
    }
```


Our **tests** will be working to **mimic accepting that post**, not necessarily from the **thymeleaf template post**, but for our test. 
We are getting a **403 forbidden** rather than a **401 unauthorized**.  we will throw a **forbidden** if the **CSRF fails** versus not having **Authentication**. See options on `csrf()` method such as `asHeader` or `asInvalidToken`.



  
**Mock MVC** is not the **Web Framework**, So if we run the **Web application**, any **forms** that we have that are posting are going to **fail** because we have not updated **CSRF Protection** yet. 


**Browser case**

We need to change **add/update customer** **form**  (i.e. thymeleaf template)to support **CSRF Protection**. We'd run a full blown integration test which outside the scope the browser.

` <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />`



