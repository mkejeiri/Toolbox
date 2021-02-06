package com.elearning.drink.drinkfactory.security;

import com.elearning.drink.drinkfactory.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener {
    private final LoginSuccessRepository loginSuccessRepository;
    @EventListener
    //registering this method as an EventListener, and spring framework will look for the @EventListener annotation,
    //and then when we have an event with the type of AuthenticationSuccessEvent,
    //this listen method will get invoked.
    public void listen(AuthenticationSuccessEvent event) {
        LoginSuccess.LoginSuccessBuilder builder = LoginSuccess.builder();

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

        if (token.getPrincipal() instanceof User) {
            User user = (User) token.getPrincipal();
            builder.user(user);
            log.debug("*** User name logged in: " + user.getUsername());
        }

        if (token.getDetails() instanceof WebAuthenticationDetails) {
            WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
            builder.sourceIp(details.getRemoteAddress());
            log.debug("*** Source IP: " + details.getRemoteAddress());
        }
        LoginSuccess loginSuccess = loginSuccessRepository.save(builder.build());
        log.debug("Login Success saved. Id: " + loginSuccess.getId());
    }
}