# Spring Security Core - Spring MVC Monolith

## Database Authentication

#### Database Authentication - Step 1

We create our custom **User** class similar to the `org.springframework.security.core.userdetails.User` class (which implements **UserDetails**), and  **Authority** class similar to `org.springframework.security.core.authority.SimpleGrantedAuthority` class (which implements **GrantedAuthority**).


**class User**
```java
package com.elearning.drink.drinkfactory.domain;

import lombok.*;
import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;

    //We can use the project Lombok @Singular annotation, and in Builder pattern,
    //we will get a property called authority, and then we can add in a Singular authority via the Builder pattern.
    @Singular
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"))
    private Set<Authority> authorities;

    //Without having the @Builder.Default annotation the default properties will actually
    //get set to null if we use the Project Lombok Builder pattern.
    @Builder.Default
    private Boolean accountNonExpired = true;

    @Builder.Default
    private Boolean accountNonLocked = true;

    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @Builder.Default
    private Boolean enabled = true;
}

```

**class Authority**
```java
package com.elearning.drink.drinkfactory.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "Roles")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String role;

    //We can use the project Lombok @Singular annotation, and in Builder pattern,
    //we will get a property called authority, and then we can add in a Singular authority via the Builder pattern.
    //@Singular : we aren't building authorities and adding users to it but the inverse is true.
    @ManyToMany(mappedBy = "authorities")
    private Set<User> users;
}
```

**Avoid** using **@Data** lombok annotation in **ManyToMany relationship** because the equals and hash code methods, get **Project Lombok** confused and sees the **inverse structure** and then gets into basically an **infinite loop** and **crashes**.

We can use the project Lombok **@Singular** annotation, and in **@Builder pattern**, we will get a property called **authority**, and then we can add in a **Singular authority** via the **@Builder** pattern.


If we use the Project **Lombok @Builder pattern** and without the **@Builder.Default** annotation, the **default properties** will actually get set to **null**, 


-----------

**Notes**: 
- How does **Spring Data JPA** manage **transactions**?
	- Unless we've **initialized a transaction** in the **code**, **Spring Data JPA** will **implicitly create a transaction when repository methods** are **called**. This can later cause **issues** with **lazily loaded references** - if we try to **access** them **outside** of the **transactional scope**.
	- other option, is declare the `authorities` property **eagerly loaded** and to avoid a **round trip** to the **database**!.

- **Access to H2 database**:
	- Since **H2** is not used for **production**, we need to **white list** the path to `/h2-console/` as follow : `.antMatchers( "/h2-console/**").permitAll()` in `SecurityConfig.configure` method.  
	- Also, by **defaul**t, **Spring Security** is **preventing frames**, so we need to add : `http.headers().frameOptions().disable();` or ` http.headers().frameOptions().sameOrigin()` in `SecurityConfig.configure` method.  

-----------



#### Database Authentication - Step 2

We need to implement repositories for User and Authority classes as follow:

```java
package com.elearning.drink.drinkfactory.repositories.security;

import com.elearning.drink.drinkfactory.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

```java
package com.elearning.drink.drinkfactory.repositories.security;

import com.elearning.drink.drinkfactory.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}

```


#### Database Authentication - Step 3

We need to create a **service** (i.e. `JpaUserDetailsService`) that **implements** `UserDetailsService` interface :


```java
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //transactional context/scope was limited to findByUsername call and
        //since getAuthorities is lazy loaded, we ran into transactional problem
        //when we try to load authorities outside the transaction scope-> Hence the need to use '@Transactional'
        //Another solution would be, to eagerly load authorities and to avoid a round trip to the db!
        User domainUser = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Username " + username + "Not found"));

        return new org.springframework.security.core.userdetails
                .User(
                        domainUser.getUsername(),
                        domainUser.getPassword(),
                        domainUser.getEnabled(),
                        domainUser.getAccountNonExpired(),
                        domainUser.getCredentialsNonExpired(),
                        domainUser.getAccountNonLocked(),
                        getConvertedAuthorities(domainUser.getAuthorities()));
    }

    private Set<GrantedAuthority> getConvertedAuthorities(Set<Authority> authorities) {
        return (authorities == null || authorities.size() == 0) ? new HashSet<>() :
                authorities.stream()
                        .map(Authority::getRole)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());
    }
}

```

#### Database Authentication - Step 4

We need to **commenting out** the `configure(AuthenticationManagerBuilder auth)` and let the **auto-configuration** Spring Boot to take over, and because we have a **passwordEncoder**, and our own **UserDetailsService** in the context, Spring Boot's **auto-configuration** will **pick them both up** and **Wire** them into **Spring Security context**.

So if you don't **completely comment out** `configure(AuthenticationManagerBuilder auth)`, it will override the Spring Boot `auto-configuration` which relies on Spring Boot and uses our **UserDetailsService** implementation.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager){
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

 public RestParamAuthFilter restParamAuthFilter(AuthenticationManager authenticationManager){
     RestParamAuthFilter filter = new RestParamAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class)
        .csrf().disable();


        http.addFilterBefore(restParamAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class)
        //.csrf().disable() : no need here, handles globally before.
        ;

        http
                //this should before the 2nd authorizeRequests,
                //because the 2nd authorizeRequests is for anyRequest()!!!
                .authorizeRequests(expressionInterceptUrlRegistry -> {
                    expressionInterceptUrlRegistry
                            //Permit root path & static assets
                            .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                            .antMatchers( "/h2-console/**").permitAll() //don't use in production

                            //drinks*: allow any query params
                            //&  /drinks/find
                            .antMatchers("/drinks/find", "/drinks*").permitAll()

                            //rest controller filter
                            .antMatchers(HttpMethod.GET, "/api/v1/drink/**").permitAll()
                            .mvcMatchers(HttpMethod.GET, "/api/v1/drinkUpc/{upc}").permitAll()
                    ;
                })

                //Any other request rules!
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();

        //by default, Spring Security is preventing frames
//        http.headers().frameOptions().disable();
        http.headers().frameOptions().sameOrigin();
    }

   @Bean
    PasswordEncoder passwordEncoder() {
        return CustomPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        //we need to specify {noop} encoder.
//        auth.inMemoryAuthentication()
//                .withUser("admin")
//                .password("{bcrypt}$2a$10$ak6SgBKbU/.GnrpkY1pqvuMbUIMq2lTYjKBmBLmR07l7BQp42ohWu")
//                .roles("ADMIN")
//                .and()
//                .withUser("user")
//                .password("{sha256}c788604069f24a84b241cf3a35dd78e680b7c1be28461b36fc558d924ace8bb5a35cb5e1de753707")
//                .roles("USER");
//
//        auth.inMemoryAuthentication()
//                .withUser("customer")
//                .password("{bcrypt}$2a$10$ak6SgBKbU/.GnrpkY1pqvuMbUIMq2lTYjKBmBLmR07l7BQp42ohWu")
//                .roles("CUSTOMER");
//    }

}
```


#### Database Authentication - Step 5

The tests are **failing** because we don't bring all springboot test context and our **UserDetailsService** implementation got excluded and default implementation is wired up instead.  We need to change `@WebMvcTest` to `@SpringBootTest`.

