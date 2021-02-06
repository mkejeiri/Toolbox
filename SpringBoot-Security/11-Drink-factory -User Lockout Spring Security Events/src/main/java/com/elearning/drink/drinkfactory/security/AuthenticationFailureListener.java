package com.elearning.drink.drinkfactory.security;

import com.elearning.drink.drinkfactory.domain.User;
import com.elearning.drink.drinkfactory.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationFailureListener {
    private final LoginFailureRepository loginFailureRepository;
    private final UserRepository userRepository;

    @EventListener
    //registering this method as an EventListener, and spring framework will look for the @EventListener annotation,
    //and then when we have an event with the type of AuthenticationFailureBadCredentialsEvent, this listen
    //method will get invoked.
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        log.debug("Login failure");

        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            LoginFailure.LoginFailureBuilder loginFailureBuilder = LoginFailure.builder();
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

            if (token.getPrincipal() instanceof String) {
                log.debug("Attempted Username: " + token.getPrincipal());
                loginFailureBuilder.username(token.getPrincipal().toString());
                userRepository.findByUsername((String) token.getPrincipal()).ifPresent(loginFailureBuilder::user);
            }

            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
                loginFailureBuilder.sourceIp(details.getRemoteAddress());
                log.debug("Source IP: " + details.getRemoteAddress());
            }
            LoginFailure loginFailure = loginFailureRepository.save(loginFailureBuilder.build());
            log.debug("Login Failure saved. Id: " + loginFailure.getId());

            if (loginFailure.getUser() != null) {
                lockUserAccount(loginFailure.getUser());
            }
        }
    }

    private void lockUserAccount(User user) {
        List<LoginFailure> failures = loginFailureRepository.findAllByUserAndCreatedDateIsAfter(user,
                Timestamp.valueOf(LocalDateTime.now().minusDays(1)));

        //three failed attempts in 24 hours period.
        //We look the account.
        if (failures.size() > 3) {
            log.debug("Locking User Account... ");
            user.setAccountNonLocked(false);
            userRepository.save(user);
        }
    }
}