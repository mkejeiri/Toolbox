package com.elearning.drink.drinkfactory.bootstrap;

import com.elearning.drink.drinkfactory.domain.Authority;
import com.elearning.drink.drinkfactory.domain.Role;
import com.elearning.drink.drinkfactory.domain.User;
import com.elearning.drink.drinkfactory.repositories.security.AuthorityRepository;
import com.elearning.drink.drinkfactory.repositories.security.RoleRepository;
import com.elearning.drink.drinkfactory.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    private void loadSecurityData() {
        //drink auths
        Authority createDrink = authorityRepository.save(Authority.builder().permission("drink.create").build());
        Authority updateDrink = authorityRepository.save(Authority.builder().permission("drink.update").build());
        Authority readDrink = authorityRepository.save(Authority.builder().permission("drink.read").build());
        Authority deleteDrink = authorityRepository.save(Authority.builder().permission("drink.delete").build());

        //customer auths
        Authority createCustomer = authorityRepository.save(Authority.builder().permission("customer.create").build());
        Authority readCustomer = authorityRepository.save(Authority.builder().permission("customer.read").build());
        Authority updateCustomer = authorityRepository.save(Authority.builder().permission("customer.update").build());
        Authority deleteCustomer = authorityRepository.save(Authority.builder().permission("customer.delete").build());

        //customer brewery
        Authority createBrewery = authorityRepository.save(Authority.builder().permission("brewery.create").build());
        Authority readBrewery = authorityRepository.save(Authority.builder().permission("brewery.read").build());
        Authority updateBrewery = authorityRepository.save(Authority.builder().permission("brewery.update").build());
        Authority deleteBrewery = authorityRepository.save(Authority.builder().permission("brewery.delete").build());


        Role adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
        Role customerRole = roleRepository.save(Role.builder().name("CUSTOMER").build());
        Role userRole = roleRepository.save(Role.builder().name("USER").build());

        //Hibernate doesn't support immutable collection (Set), we wrap Set in a HashSet.
        adminRole.setAuthorities(new HashSet<>(Set.of(createDrink, updateDrink, readDrink, deleteDrink, createCustomer, readCustomer,
                updateCustomer, deleteCustomer, createBrewery, readBrewery, updateBrewery, deleteBrewery)));

        customerRole.setAuthorities(new HashSet<>(Set.of(readDrink, readCustomer, readBrewery)));

        userRole.setAuthorities(new HashSet<>(Set.of(readDrink)));

        roleRepository.saveAll(Arrays.asList(adminRole, customerRole, userRole));

        userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .role(adminRole)
                .build());

        userRepository.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .role(userRole)
                .build());

        userRepository.save(User.builder()
                .username("customer")
                .password(passwordEncoder.encode("password"))
                .role(customerRole)
                .build());

        log.debug("Users Loaded: " + userRepository.count());

    }

    @Transactional
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (authorityRepository.count() == 0) loadSecurityData();


    }
}
