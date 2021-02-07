package com.elearning.drink.drinkfactory.security;

import com.elearning.drink.drinkfactory.domain.security.User;
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
