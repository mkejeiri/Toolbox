package com.elearning.drink.drinkfactory.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

public class CustomPasswordEncoderFactories {

    public static PasswordEncoder createDelegatingPasswordEncoder() {
        String encodingId = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(encodingId, new BCryptPasswordEncoder());
        encoders.put("bcrypt15", new BCryptPasswordEncoder(15));
        encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
        encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
        encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());

        //default is encodingId (i.e bcrypt or bcrypt10), unless is stated explicitly otherwise.
        return new DelegatingPasswordEncoder(encodingId, encoders);
    }

    //don't instantiate
    private CustomPasswordEncoderFactories() {}
}
