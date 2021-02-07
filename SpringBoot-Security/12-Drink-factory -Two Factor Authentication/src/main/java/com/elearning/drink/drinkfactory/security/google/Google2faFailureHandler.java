package com.elearning.drink.drinkfactory.security.google;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
//standard spring security interface AuthenticationFailureHandler
public class Google2faFailureHandler implements AuthenticationFailureHandler {

    //We could handle any type of exception here,
    //but we're really not in an exception case, we want to capture the user and redirect them to this page.
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.debug("Forward to 2fa");
        request.getRequestDispatcher("/user/verify2fa")
                .forward(request, response);

    }
}
