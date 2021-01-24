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
-----------
Update the **[loadSecurityData](src/main/java/com/elearning/drink/drinkfactory/bootstrap/UserDataLoader.java)** class.

**Note** : Hibernate doesn't support immutable collection (e.g **Set**), we wrap **Set** in a **HashSet**.
`customerRole.setAuthorities(new HashSet<>(Set.of(readDrink, readCustomer, readBrewery)));`
-----------


###  Update Spring Security for RESTful Drink API:


Use a **fine grained authorities/permission** instead of **antMatchers** & **mvcMatchers** for restApi.

Using `@PreAuthorize("hasAuthority('drink.read')")`, `@PreAuthorize("hasAuthority('drink.create')")`,`@PreAuthorize("hasAuthority('drink.update')")`, and `@PreAuthorize("hasAuthority('drink.delete')")` on action methods, we could get rid of **antMatchers** & **mvcMatchers**.


```java
...
 @PreAuthorize("hasAuthority('drink.read')")
    @GetMapping(produces = { "application/json" }, path = "drink")
    public ResponseEntity<DrinkPagedList> listDrinks(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                    @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                    @RequestParam(value = "drinkName", required = false) String drinkName,
                                                    @RequestParam(value = "drinkStyle", required = false) DrinkStyleEnum drinkStyle,
                                                    @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){

        log.debug("Listing Drinks");

        if (showInventoryOnHand == null) {
            showInventoryOnHand = false;
        }

        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        DrinkPagedList drinkList = drinkService.listDrinks(drinkName, drinkStyle, PageRequest.of(pageNumber, pageSize), showInventoryOnHand);

        return new ResponseEntity<>(drinkList, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('drink.read')")
    @GetMapping(path = {"drink/{drinkId}"}, produces = { "application/json" })
    public ResponseEntity<DrinkDto> getDrinkById(@PathVariable("drinkId") UUID drinkId,
                                                @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){

        log.debug("Get Request for DrinkId: " + drinkId);

        if (showInventoryOnHand == null) {
            showInventoryOnHand = false;
        }

        return new ResponseEntity<>(drinkService.findDrinkById(drinkId, showInventoryOnHand), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('drink.read')")
    @GetMapping(path = {"drinkUpc/{upc}"}, produces = { "application/json" })
    public ResponseEntity<DrinkDto> getDrinkByUpc(@PathVariable("upc") String upc){
        return new ResponseEntity<>(drinkService.findDrinkByUpc(upc), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('drink.create')")
    @PostMapping(path = "drink")
    public ResponseEntity saveNewDrink(@Valid @RequestBody DrinkDto drinkDto){

        DrinkDto savedDto = drinkService.saveDrink(drinkDto);

        HttpHeaders httpHeaders = new HttpHeaders();

        //todo hostname for uri
        httpHeaders.add("Location", "/api/v1/drink_service/" + savedDto.getId().toString());

        return new ResponseEntity(httpHeaders, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('drink.update')")
    @PutMapping(path = {"drink/{drinkId}"}, produces = { "application/json" })
    public ResponseEntity updateDrink(@PathVariable("drinkId") UUID drinkId, @Valid @RequestBody DrinkDto drinkDto){

        drinkService.updateDrink(drinkId, drinkDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('drink.delete')")
    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping({"drink/{drinkId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDrink(@PathVariable("drinkId") UUID drinkId){
        drinkService.deleteById(drinkId);
    }
...

```















