package com.elearning.drink.drinkfactory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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
                            .antMatchers(HttpMethod.GET,"/api/v1/drink/**").permitAll()
                            .mvcMatchers(HttpMethod.GET,"/api/v1/drinkUpc/{upc}").permitAll()
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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //we need to specify {noop} encoder.
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("{noop}password")
                .roles("ADMIN")
                .and()
                .withUser("user")
                .password("{noop}password")
                .roles("USER");

        //We could use 'and' to chain or start over as follow:
        auth.inMemoryAuthentication()
                .withUser("customer")
                .password("{noop}password")
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
