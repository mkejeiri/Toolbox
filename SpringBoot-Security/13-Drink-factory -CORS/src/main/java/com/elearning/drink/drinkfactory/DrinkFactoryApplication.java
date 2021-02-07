package com.elearning.drink.drinkfactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
//exclude will disable them from spring context!
//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
public class DrinkFactoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrinkFactoryApplication.class, args);
    }

}

