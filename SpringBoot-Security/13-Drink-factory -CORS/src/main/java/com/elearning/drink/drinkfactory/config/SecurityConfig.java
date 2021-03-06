package com.elearning.drink.drinkfactory.config;

import com.elearning.drink.drinkfactory.security.CustomPasswordEncoderFactories;
import com.elearning.drink.drinkfactory.security.filters.RestHeaderAuthFilter;
import com.elearning.drink.drinkfactory.security.filters.RestParamAuthFilter;
import com.elearning.drink.drinkfactory.security.google.Google2faFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true)
//@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final PersistentTokenRepository persistentTokenRepository;
    private final Google2faFilter google2faFilter;

    //needed for use with Spring Data JPA SPeL
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    public RestParamAuthFilter restParamAuthFilter(AuthenticationManager authenticationManager) {
        RestParamAuthFilter filter = new RestParamAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //SessionManagementFilter detects that a user has been authenticated!
        http.addFilterBefore(google2faFilter, SessionManagementFilter.class);

        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class)
        //.csrf().disable()
        ;


        http.addFilterBefore(restParamAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class)
        //.csrf().disable() : no need here, handles globally before.
        ;

        http.cors().and()
                //this should before the 2nd authorizeRequests,
                //because the 2nd authorizeRequests is for anyRequest()!!!
                .authorizeRequests(expressionInterceptUrlRegistry -> {
                    expressionInterceptUrlRegistry
                            //Permit root path & static assets
                            .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                            .antMatchers("/h2-console/**").permitAll() //don't use in production

                    //drinks*: allow any query params
                    //&  /drinks/find
                    //.antMatchers("/drinks/find", "/drinks*")
                    //.hasAnyRole("USER","CUSTOMER","ADMIN")
//                            .mvcMatchers("/brewery/breweries**")
//                            .hasAnyRole("CUSTOMER", "ADMIN")
//                            .mvcMatchers("/drinks/find", "/drinks/{drinkId}")
//                            .hasAnyRole("USER", "CUSTOMER", "ADMIN")


                    //rest controller filter
                    //.antMatchers(HttpMethod.GET, "/api/v1/drink/**").permitAll()
//                            .mvcMatchers(HttpMethod.GET, "/api/v1/drink/**")
//                            .hasAnyRole("USER", "CUSTOMER", "ADMIN")
//                          .mvcMatchers(HttpMethod.DELETE, "/api/v1/drink/**").hasRole("ADMIN")
//                          -> replaced by @PreAuthorize("hasRole('ADMIN')")
//                            .mvcMatchers(HttpMethod.GET, "/brewery/api/v1/breweries")
//                            .hasAnyRole("CUSTOMER", "ADMIN")
//                            .mvcMatchers(HttpMethod.GET, "/api/v1/drinkUpc/{upc}")
//                            .hasAnyRole("USER", "CUSTOMER", "ADMIN")

                    ;
                })

                //Any other request rules!
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin(httpSecurityFormLoginConfigurer ->
                        {
                            httpSecurityFormLoginConfigurer
                                    .loginProcessingUrl("/login")
                                    //we use index as a default page for the login (customizable)
                                    .loginPage("/").permitAll()
                                    //on success we forward to the index page (customizable)
                                    .successForwardUrl("/")
                                    //redirect everything to index page (customizable)
                                    .defaultSuccessUrl("/")
                                    //error  param used by the UI to display alert incorrect username/password
                                    .failureUrl("/?error");
                        }

                ).logout(httpSecurityLogoutConfigurer -> {
                    httpSecurityLogoutConfigurer
                    //Since we have a link to logout (i.e. get request) and spring security
                    //handles post request for logout we need to override.
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                    .logoutSuccessUrl("/")
                     //error param used by the UI to display success of logout
                    .logoutSuccessUrl("/?logout")
                    .permitAll();
        })
//                .and()
                .httpBasic()
                .and().csrf().ignoringAntMatchers("/h2-console/**", "/api/**")
                .and()
                //rememberMe with persistence
                .rememberMe()
                    .tokenRepository(persistentTokenRepository)
                    .userDetailsService(userDetailsService);
        //rememberMe without persistence
                /*
                    .rememberMe()
                    //Key value built into the hash.
                    .key("drink-key")
                    //we had to add userDetailsService to make rememberMe work, otherwise apps could get away with it.
                    //java.lang.IllegalStateException: UserDetailsService is required.
                    .userDetailsService(userDetailsService)*/
        ;

        //by default, Spring Security is preventing frames
//        http.headers().frameOptions().disable();
        http.headers().frameOptions().sameOrigin();
    }

//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return CustomPasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }


//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        //we need to specify {noop} encoder.
//        auth.inMemoryAuthentication()
//                .withUser("admin")
//                .password("{bcrypt}$2a$10$ak6SgBKbU/.GnrpkY1pqvuMbUIMq2lTYjKBmBLmR07l7BQp42ohWu")
//                .roles("ADMIN")
//                .and()
//                .withUser("user")
//                .password("{sha256}c788604069f24a84b241cf3a35dd78e680b7c1be28461b36fc558d924ace8bb5a35cb5e1de753707")
//                .roles("USER");
//
//        auth.inMemoryAuthentication()
//                .withUser("customer")
//                .password("{bcrypt}$2a$10$ak6SgBKbU/.GnrpkY1pqvuMbUIMq2lTYjKBmBLmR07l7BQp42ohWu")
//                .roles("CUSTOMER");
//    }

}
