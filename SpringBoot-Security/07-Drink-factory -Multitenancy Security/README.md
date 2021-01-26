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

We link customer to user and indirectly to role, it more cleaner approach. We also refactored `DefaultDrinkLoader`  class and merge `UserDataLoader` class into it.







