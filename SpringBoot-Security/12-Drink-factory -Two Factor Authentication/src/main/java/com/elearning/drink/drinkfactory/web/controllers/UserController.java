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

        //user from spring security context.
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

    @PostMapping("/register2fa")
    public String confirm2Fa(@RequestParam Integer verifyCode) {

        //user from spring security context.
        User user = getUser();

        log.debug("Entered Code is:" + verifyCode);

        //Validate the code: authorizeUser method returns back a boolean.
        //using the username, it will look up the user in the database and get the google2FaSecret
        //and checks that google2FaSecret code does matches with verifyCode using GoogleAuthenticator.checkCode method.
        if (googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {

            //update the verified user because user object is detached from hibernate and
            //it could be stale (i.e. doesn't reflect the latest version from the database).
            User savedUser = userRepository.findById(user.getId()).orElseThrow();

            //setUserGoogle2fa(true): user has completed registration for Two-Factor authentication.
            //default is false.
            savedUser.setUserGoogle2fa(true);
            userRepository.save(savedUser);
            return "/index";

        }
        //if bad code, resubmit the form.
        return "user/register2fa";
    }

    //render the forms
    @GetMapping("/verify2fa")
    public String verify2fa() {
        return "user/verify2fa";
    }

    //Verify code
    @PostMapping
    public String verifyPostOf2Fa(@RequestParam Integer verifyCode) {

        //pull the user out of spring context
        User user = getUser();

        //Validate the code: authorizeUser method returns back a boolean.
        //using the username, it will look up the user in the database and get the google2FaSecret
        //and checks that google2FaSecret code does matches with verifyCode using GoogleAuthenticator.checkCode method.
        if (googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
            //Proper code entered.
            //in the spring security context, set `google2faRequired Transient` property to `false`,
            //i.e. user entered the authentication code in the Two-Factor properly.
            ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setGoogle2faRequired(false);
            return "/index";
        }

        //bad code return back to verify2fa form.
        return "user/verify2fa";
    }

    private User getUser() {
        //user from spring security context.
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}