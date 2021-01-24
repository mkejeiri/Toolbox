# Spring Security Core - Spring MVC Monolith

## User Authorities 

### Refactor User Authorities:
- **Current user roles** are very **broad** in **scope**.
- Do not match **granularity** of application.
- **Limitation** to describe different access level of **CRUD** type **operations**.
- It grows in **scope** with **complexity of application**.
- As result, we refactor to traditional **model of roles** with **authorities/permissions**.
- **Users** assigned a **role**, which has a set of defined **authorities/permissions**. 

Added new class of **role**:

```java
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    //We can use the project Lombok @Singular annotation, and in Builder pattern,
    //we will get a property called authority, and then we can add in a Singular authority via the Builder pattern.
    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "ROLE_AUTHORITIES", joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID"))
    private Set<Authority> authorities;
}


```

Refactored **Authorithy** class :

```java
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String permission;

    //We can use the project Lombok @Singular annotation, and in Builder pattern,
    //we will get a property called authority, and then we can add in a Singular authority via the Builder pattern.
    //@Singular : we aren't building authorities and adding user to it but the inverse is true.
    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles;
}

```

Refactored **User** class :

```java
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
//@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;

    //We can use the project Lombok @Singular annotation, and in Builder pattern,
    //we will get a property called authority, and then we can add in a Singular authority via the Builder pattern.
    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER) //FetchType.EAGER: one round trip to the db!
    @JoinTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"))
    private Set<Role> roles;

    @Transient
    private Set<Authority> authorities;

    public Set<Authority> getAuthorities() {
        return this.roles.stream()
                .map(Role::getAuthorities)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }


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

Added **RoleRepository** Jpa  to fetch **roles**:

```java
public interface RoleRepository extends JpaRepository<Role, Long> {
}

```

