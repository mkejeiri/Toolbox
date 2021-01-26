package com.elearning.drink.drinkfactory.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public abstract class AbstractRestAuthFilter extends AbstractAuthenticationProcessingFilter {

    public AbstractRestAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    //we've override the method for attemptAuthentication, extracting out the username and password from the request,
    //creating Username, PasswordAuthenticationToken and then passing that into the authentication manager
    //which performs the authentication.
    //We are using in-memory authentication manager so it will do the authentication against that.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        String userName = getUserName(request);
        String password = getPassword(request);

        //as we don't know if getAuthenticationManager().authenticate is null safe operations
        if (userName == null) userName = "";
        if (password == null) password = "";

        log.debug("Authenticating User: " + userName);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
        log.debug("token : ", token);

        //If we are setting a username, go ahead and return the AuthenticationManager.
        if (!StringUtils.isEmpty(userName))
            return this.getAuthenticationManager().authenticate(token);
        else
            //null, we just want to skip things,
            //So we'll just return null for this implementation that we are setting up for ourselves.
            return null;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        log.debug("Request is to process authentication");

        try {

            Authentication authResult = attemptAuthentication(request, response);
            //attemptAuthentication result is not null
            if (authResult != null) {
                successfulAuthentication(request, response, chain, authResult);

            } else {
                //attemptAuthentication result is null
                chain.doFilter(request, response);
            }
        } catch (AuthenticationException failed) {
            // Authentication failed
            log.error("Authentication failed", failed);
            unsuccessfulAuthentication(request, response, failed);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        log.debug("Authentication request failed: " + failed.toString(), failed);
        log.debug("Updated SecurityContextHolder to contain null Authentication");

        response.sendError(HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
        //this is a rest API, we don't need is this rememberMeServices (i.e. rememberMe cookie) or to fire an event.

        //using SecurityContextHolder, getting the context and then setting in the authResult.
        //which establishes the authorization within the context of Spring Security.
        SecurityContextHolder.getContext().setAuthentication(authResult);
    }

    abstract protected String getUserName(HttpServletRequest request);
    abstract protected  String getPassword(HttpServletRequest request);
}
