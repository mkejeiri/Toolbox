package com.elearning.drink.drinkfactory.security.google;

import com.elearning.drink.drinkfactory.domain.security.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class Google2faFilter extends GenericFilterBean {

    private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //step 1 -User could be somebody initially logging in, and we hold until
        //the user has authenticated with username and password (step 2).
        if (authentication != null  && !authenticationTrustResolver.isAnonymous(authentication)){
            log.debug("Processing 2FA Filter");

            //Once the user is authenticated with username and password,
            //we will have an instance of the user within spring security context.
            //step 2 - If the user is successfully logged in and has a security context object
            if (authentication.getPrincipal() != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();

                // if the user has 2 factors authentication enabled, and it's required.
                if (user.getUseGoogle2fa() && user.getGoogle2faRequired()) {
                    log.debug("2FA Required");
                    // todo add failure handler
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}