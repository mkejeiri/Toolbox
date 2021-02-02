# Spring Security Core - Spring MVC Monolith

## Multitenancy Security

### add drink order authorithy and update the roles

here we add drink order authorithy and update the roles

[UserDataLoader class](src/main/java/com/elearning/drink/drinkfactory/bootstrap/UserDataLoader.java)

```java 
...
        //drink order
        Authority createOrder = authorityRepository.save(Authority.builder().permission("order.create").build());
        Authority readOrder = authorityRepository.save(Authority.builder().permission("order.read").build());
        Authority updateOrder = authorityRepository.save(Authority.builder().permission("order.update").build());
        Authority deleteOrder = authorityRepository.save(Authority.builder().permission("order.delete").build());
        Authority createOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.create").build());
        Authority readOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.read").build());
        Authority updateOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.update").build());
        Authority deleteOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.delete").build());

        Role adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
        Role customerRole = roleRepository.save(Role.builder().name("CUSTOMER").build());
        Role userRole = roleRepository.save(Role.builder().name("USER").build());

        adminRole.setAuthorities(new HashSet<>(Set.of(createDrink, updateDrink, readDrink, deleteDrink, createCustomer, readCustomer,
                updateCustomer, deleteCustomer, createBrewery, readBrewery, updateBrewery, deleteBrewery,
                createOrder, readOrder, updateOrder, deleteOrder)));

        customerRole.setAuthorities(new HashSet<>(Set.of(readDrink, readCustomer, readBrewery, createOrderCustomer, readOrderCustomer,
                updateOrderCustomer, deleteOrderCustomer)));
...

```
----------

`@RestController` **annotation** tells Spring that all **handler methods** in the **controller** should have their return **value written** directly to the **body of the response**, rather than being carried in the **model** to a **view** for **rendering**.
If we set `@Controller` **annotation for Spring MVC controller**, we need also **annotate** all of the **handler methods** with `@ResponseBody` to achieve the same result. Yet **another option** would be to return a `ResponseEntity` object.

----------
### use domain User as custom spring security user

**domain** `User` need to **implement** `UserDetails` and `CredentialsContainer`.

[User class](src/main/java/com/elearning/drink/drinkfactory/domain/User.java)

```java
public class User implements UserDetails, CredentialsContainer {
...

@Transient
    public Set<GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(Role::getAuthorities)
                .flatMap(Set::stream)
                .map(authority -> {
                    return new SimpleGrantedAuthority(authority.getPermission());
                })
                .collect(Collectors.toSet());
    }
    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
...

}

```

We 're not doing a **type conversion** to **user** provided by **Spring Security**, because we are **implementing** that **type natively** through **User** class this allow us to add a customer property. We need to **adjust** `JpaUserDetailsService` **class** accordingly:

```java
public class JpaUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;
	@Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User domainUser = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Username " + username + "Not found"));
        return  domainUser;
    }
}
```


### Link Customer to user 

We link **customer to user** and indirectly to **role**, it more **cleaner approach**. We also refactored `DefaultDrinkLoader`  class and merge `UserDataLoader` class into it.


### Custom Authentication Manager

**Customer** can only acts on **their orders** :
- For **AuthenticationManager** we don't need to implement a specific **interface** at any old Spring bean.
- We are using the flexibility that we have within Spring Security in the Spring Framework as far as how it interacts with the **Spring Expression Language**. 
- Create a method **customerIdMatches** to **verify** that the **customerid** matches and return back a true or false.

The **purpose** of writing a custom **authentication manager** is to **extract** the **customer** from the **Authentication** and **compare** it to the `customerId` being requested.


```java
package com.elearning.drink.drinkfactory.security;

import com.elearning.drink.drinkfactory.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;

import java.util.UUID;

@Configuration
@Slf4j
public class DrinkOrderAuthenticationManager {
    public boolean customerIdMatches(Authentication authentication, UUID customerId) {
        User authenticatedUser = (User) authentication.getPrincipal();
        log.debug("Authentication User customerId: " + authenticatedUser.getCustomer().getId().toString() +
                "  Customer Id: " + customerId.toString());
        return customerId.equals(authenticatedUser.getCustomer().getId());
    }
}
```

We need to set the **Spring Expression Language** on the **controller methods** :
 
```java
    @PreAuthorize("hasAuthority('order.create') OR " +
            "hasAuthority('customer.order.create') AND" +
            //@drinkOrderAuthenticationManager's referencing DrinkOrderAuthenticationManager Spring components on that Spring context.
            //So instructing Spring Security to pass in the authentication object and the customerId into this method.
            "@drinkOrderAuthenticationManager.customerIdMatches(authentication, #customerId)")
    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public DrinkOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody DrinkOrderDto drinkOrderDto) {
        return drinkOrderService.placeOrder(customerId, drinkOrderDto);
    }
```
Note that we use `@drinkOrderAuthenticationManager` to reference the **DrinkOrderAuthenticationManager bean** and `#customerId` to reference the **PathVariable** property.

**Testing UserDetails for the STPETE_USER** :

```java
 @WithUserDetails(DefaultDrinkLoader.STPETE_USER)
    @Test
    void createOrderUserAuthCustomer() throws Exception {
        DrinkOrderDto drinkOrderDto = buildOrderDto(stPeteCustomer, loadedDrinks.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(drinkOrderDto)))
                .andExpect(status().isCreated());
    }
```

Underneath the covers, **Spring Security will authenticate the user** (i.e. `@WithUserDetails(DefaultDrinkLoader.STPETE_USER)`), so we don't have to use **HTTP basic** and to provide the **user credentials**  inside the test itself.
We're **instructing**  the **test environment**  to execute the test with the **Spring Security Context**  for that specific **user**  (i.e. STPETE_USER). Also a very good approach to have when we use more than **one authentication method**  or maybe that **authentication method**  is going to **change**  somewhere down the road.



Create a new permission for Pickup order
---------
The Pickup order action is not a typical **CRUD** type **action**. It's an **API action**, thus **best practice** would be to **define** a **permission** for the **action**.

and also we create a new `DrinkOrderControllerV2`: 

```java
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/orders/")
public class DrinkOrderControllerV2 {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final DrinkOrderService drinkOrderService;

    @DrinkOrderReadV2Permission
    @GetMapping
    public DrinkOrderPagedList listOrders(@AuthenticationPrincipal User user,
                                          @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                          @RequestParam(value = "pageSize", required = false) Integer pageSize){

        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if (user.getCustomer() != null) {
            return drinkOrderService.listOrders(user.getCustomer().getId(), PageRequest.of(pageNumber, pageSize));
        } else {
            return drinkOrderService.listOrders(PageRequest.of(pageNumber, pageSize));
        }
    }

    @DrinkOrderReadV2Permission
    @GetMapping("orders/{orderId}")
    public DrinkOrderDto getOrder(@PathVariable("orderId") UUID orderId){

        return null;
        //  return drinkOrderService.getOrderById(orderId);
    }
}
```


There is no way for us to specify a different **UserId**, at least not **programmatically** from the **Web interface**, on the **controller**, we **fetch** listOrders based on **authenticated** user coming from **Spring Security**, i.e. coming from the **Security Context** of the **logged in user**.





Spring Security with Spring Data JPA
------------
add the dependency :

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>	
```
Add the **SecurityEvaluationContextExtension** which allows **Spring Security** to be used with **Spring Data**, **Spring Expression language** or **SPel** expression.


```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // needed for use with Spring Data JPA SPeL
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
...
```

And this **hibernate query** (aka **named query**) to `findOrderSecureById`.

Within that query, we are using two different features of Spring Security:
- hasAuthority with **SPel** expression
- if the order customer id equals the principal customerId,  where principal object comes from the Spring Security Context.


```java
public interface DrinkOrderRepository extends JpaRepository<DrinkOrder, UUID> {

    Page<DrinkOrder> findAllByCustomer(Customer customer, Pageable pageable);

    List<DrinkOrder> findAllByOrderStatus(OrderStatusEnum orderStatusEnum);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    DrinkOrder findOneById(UUID id);
    
    @Query("select o from DrinkOrder o where o.id =?1 and " +
            "(true = :#{hasAuthority('order.read')} or o.customer.id = ?#{principal?.customer?.id})")
    DrinkOrder findOrderSecureById(UUID orderId);
}

```



Return a drinkOrderDto found or NOT_FOUND, i.e `throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")`.  

```java
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/orders/")
public class DrinkOrderControllerV2 {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final DrinkOrderService drinkOrderService;
    @DrinkOrderReadV2Permission
    @GetMapping("{orderId}")
    public DrinkOrderDto getOrder(@PathVariable("orderId") UUID orderId) {
        DrinkOrderDto drinkOrderDto = drinkOrderService.getOrderById(orderId);
        if (drinkOrderDto == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        log.debug("drinkOrder found " + drinkOrderDto);
        return drinkOrderDto;
    }
...
```

Check **sql** out from **hibernate** by adding the following into **properties** file:

```properties
#show sql
spring.jpa.properties.hibernate.show_sql=true
#pretty format SQL
spring.jpa.properties.hibernate.format_sql=true
#show bind parameters
logging.level.org.hibernate.type.descriptor.sql=trace
```

