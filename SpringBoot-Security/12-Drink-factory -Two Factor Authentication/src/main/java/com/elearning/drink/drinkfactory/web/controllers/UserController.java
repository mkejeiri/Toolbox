package com.elearning.drink.drinkfactory.web.controllers;

import com.elearning.drink.drinkfactory.domain.security.User;
import com.elearning.drink.drinkfactory.repositories.security.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {

    //ISSUER: show up in the Google authenticator as "my application".
    public static final String ISSUER = "eLearning";
    public static final String GOOGLE_URL_ATTRIBUTE_NAME = "googleurl";
    private final UserRepository userRepository;
    private final GoogleAuthenticator googleAuthenticator;

    @GetMapping("/register2fa")
    public String register2fa(Model model) {

        User user = getUser();

        //this will go out to Google Services to create a QR code and returning back an image
        //for us to display on the Web page.
        String url = GoogleAuthenticatorQRGenerator.getOtpAuthURL(ISSUER, user.getUsername(),
                //Instruct Google to create the credentials which will call the save user credentials.
                //i.e. create the credentials when the process happens -> create shared secret
                //and also save it to our database...
                googleAuthenticator.createCredentials(user.getUsername()));

        log.debug("Google QR URL: " + url);

        model.addAttribute(GOOGLE_URL_ATTRIBUTE_NAME, url);

        return "user/register2fa";
    }

    @PostMapping
    public String confirm2Fa(@RequestParam Integer verifyCode) {

        //todo - impl
        return "index";
    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}