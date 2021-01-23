package com.elearning.drink.drinkfactory.config;

import com.elearning.drink.drinkfactory.security.CustomPasswordEncoderFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //this should before the 2nd authorizeRequests,
                //because the 2nd authorizeRequests is for anyRequest()!!!
                .authorizeRequests(expressionInterceptUrlRegistry -> {
                    expressionInterceptUrlRegistry
                            //Permit root path & static assets
                            .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()

                            //drinks*: allow any query params
                            //&  /drinks/find
                            .antMatchers("/drinks/find", "/drinks*").permitAll()

                            //rest controller filter
                            .antMatchers(HttpMethod.GET, "/api/v1/drink/**").permitAll()
                            .mvcMatchers(HttpMethod.GET, "/api/v1/drinkUpc/{upc}").permitAll()
                    ;
                })

                //Any other request rules!
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();
    }

    /*    @Bean
    //we are overriding the default implementation of the Delegating Password Encoder
    //with NoOpPasswordEncoder (Plain text password), so no need to specify {noop}.
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }*/

    /*@Bean
        //we are overriding the default implementation of the Delegating Password Encoder
        //with LdapShaPasswordEncoder (password hashed with random salt).
    PasswordEncoder passwordEncoder() {
        return new LdapShaPasswordEncoder();
    }*/
/*
    @Bean
        //we are overriding the default implementation of the Delegating Password Encoder
        //with StandardPasswordEncoder SH256 (password hashed with random salt).
    PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
    }*/

 /*@Bean
        //we are overriding the default implementation of the Delegating Password Encoder
        //with BCryptPasswordEncoder (password hashed with random salt), by default strength is 10.
    PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder(10);
        return new BCryptPasswordEncoder();
    }*/
 /*@Bean
        //we are overriding the default implementation of the Delegating Password Encoder
        //new feature of spring 5
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }*/

    @Bean
        //we are overriding the default implementation of the Delegating Password Encoder
        //use of our implementation
    PasswordEncoder passwordEncoder() {
        return CustomPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //we need to specify {noop} encoder.
        auth.inMemoryAuthentication()
                .withUser("admin")
                //.password("{noop}password")
                //.password("password")
                //.password("{SSHA}23Tfbq7Evk7FAU3IhfVUxqH94wD2w0IZicWueA==")
                //.password("c788604069f24a84b241cf3a35dd78e680b7c1be28461b36fc558d924ace8bb5a35cb5e1de753707")
                //.password("$2a$10$ak6SgBKbU/.GnrpkY1pqvuMbUIMq2lTYjKBmBLmR07l7BQp42ohWu")
                //we use bcrypt
                .password("{bcrypt}$2a$10$ak6SgBKbU/.GnrpkY1pqvuMbUIMq2lTYjKBmBLmR07l7BQp42ohWu")
                .roles("ADMIN")
                .and()
                .withUser("user")
                //.password("{noop}password")
                //.password("password")
                //.password("{SSHA}BBdY/iUs1ONkzAiCZ+SVeXQ/XpSKrhP3hCLk1A==")
                //.password("c788604069f24a84b241cf3a35dd78e680b7c1be28461b36fc558d924ace8bb5a35cb5e1de753707")
                //.password("$2a$10$58btxzK2GugVQSLC9e0NIOYuctbFx0ZdupdMd.WbJoG0L7n.xTiqa")

                //we use sha256
                .password("{sha256}c788604069f24a84b241cf3a35dd78e680b7c1be28461b36fc558d924ace8bb5a35cb5e1de753707")
                .roles("USER");

        //We could use 'and' to chain or start over as follow:
        auth.inMemoryAuthentication()
                .withUser("customer")
                //.password("{noop}password")
                //.password("password")
                //.password("{SSHA}BBdY/iUs1ONkzAiCZ+SVeXQ/XpSKrhP3hCLk1A==")
                //.password("4b9c85b77d5939b1588fe020684352491123e22c20f36823c8e96818b72cb22e111c0f758663c4c2")
                //.password("$2a$10$ak6SgBKbU/.GnrpkY1pqvuMbUIMq2lTYjKBmBLmR07l7BQp42ohWu")

                //we use LDAP
                //.password("{ldap}{SSHA}BBdY/iUs1ONkzAiCZ+SVeXQ/XpSKrhP3hCLk1A==")
                .password("{bcrypt15}$2a$15$Gv5r4WKHJQsWP71IzNYkeeB6XEODSr62jMiFk1es5I9FTaRUHl0pW")
                .roles("CUSTOMER");

    }

    /*  @Override
    @Bean //to bring it to spring context
    //In Memory User Details Manager
    protected UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("password")
                .roles("ADMIN")
                .build();

        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin,user);
    }*/
}
