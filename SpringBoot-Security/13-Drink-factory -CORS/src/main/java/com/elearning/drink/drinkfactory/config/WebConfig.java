package com.elearning.drink.drinkfactory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //CORS for GET, POST, PUT are managed globally here.
        //we could set url matchers
        //registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE") //allow all origin OR
                ////or apply filter on origin
                //.allowedOrigins("elearning.drinkfactory")
                ////Default
                //.allowedOrigins("*")
        //;
    }
}
