package com.elearning.drink.drinkfactory.bootstrap;

import com.elearning.drink.drinkfactory.domain.Authority;
import com.elearning.drink.drinkfactory.domain.User;
import com.elearning.drink.drinkfactory.repositories.security.AuthorityRepository;
import com.elearning.drink.drinkfactory.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    //TODO
    private void loadSecurityData() {
        Authority adminRole = authorityRepository.save(Authority.builder().permission("ROLE_ADMIN").build());
        Authority customerRole = authorityRepository.save(Authority.builder().permission("ROLE_CUSTOMER").build());
        Authority userRole = authorityRepository.save(Authority.builder().permission("ROLE_USER").build());

        userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                //.authority(adminRole) //@Singularity
                .build());

        userRepository.save(User.builder()
                .username("customer")
                .password(passwordEncoder.encode("password"))
                //.authority(customerRole) //@Singularity
                .build());

       User domainUser = userRepository.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                //.authority(userRole) //@Singularity
                .build());

        log.debug("users Loaded: " + userRepository.count());
        log.debug("roles Loaded: " + authorityRepository.count());

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (authorityRepository.count() == 0) loadSecurityData();


    }
}
