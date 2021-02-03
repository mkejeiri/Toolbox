package com.elearning.drink.drinkfactory.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public abstract class BaseIT {
    @Autowired
    WebApplicationContext wac;

    protected MockMvc mockMvc;

   /* @MockBean
    DrinkRepository drinkRepository;

    @MockBean
    DrinkInventoryRepository drinkInventoryRepository;

    @MockBean
    BreweryService breweryService;

    @MockBean
    CustomerRepository customerRepository;

    @MockBean
    DrinkService drinkService;*/

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    public static Stream<Arguments> getStreamAllUsers() {
        return Stream.of(Arguments.of("admin" , "password"),
                Arguments.of("customer", "password"),
                Arguments.of("user", "password"));
    }

    public static Stream<Arguments> getStreamNotAdmin() {
        return Stream.of(Arguments.of("customer", "password"),
                Arguments.of("user", "password"));

    }
    public static Stream<Arguments> getStreamAdminCustomer() {
        return Stream.of(Arguments.of("admin", "password"));
    }
    public static Stream<Arguments> getStreamCustomer() {
        return Stream.of(Arguments.of("admin", "password"),
                Arguments.of("customer", "password"));
    }
}