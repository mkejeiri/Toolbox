# Spring Security Core - Spring MVC Monolith

## Database Authentication

We create **User** class mapped the class `org.springframework.security.core.userdetails.User` and  **Authority** class mapped to `org.springframework.security.core.authority.SimpleGrantedAuthority`.

**Avoid** using **@Data** lombok annotation in **ManyToMany relationship** because the equals and hash code methods, get **Project Lombok** confused and sees the **inverse structure** and then gets into basically an **infinite loop** and **crashes**.

We can use the project Lombok **@Singular** annotation, and in **@Builder pattern**, we will get a property called **authority**, and then we can add in a **Singular authority** via the **@Builder** pattern.


If we use the Project **Lombok @Builder pattern** and without the **@Builder.Default** annotation, the **default properties** will actually get set to **null**, 

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

