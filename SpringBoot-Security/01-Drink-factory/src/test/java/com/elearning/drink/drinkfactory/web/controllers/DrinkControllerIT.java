package com.elearning.drink.drinkfactory.web.controllers;

import com.elearning.drink.drinkfactory.repositories.CustomerRepository;
import com.elearning.drink.drinkfactory.repositories.DrinkInventoryRepository;
import com.elearning.drink.drinkfactory.repositories.DrinkRepository;
import com.elearning.drink.drinkfactory.services.BreweryService;
import com.elearning.drink.drinkfactory.services.DrinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class DrinkControllerIT {

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @MockBean
    DrinkRepository drinkRepository;

    @MockBean
    DrinkInventoryRepository drinkInventoryRepository;

    @MockBean
    BreweryService breweryService;

    @MockBean
    CustomerRepository customerRepository;

    @MockBean
    DrinkService drinkService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

	//force any user to bypass security core module
    @WithMockUser("spring")
    @Test
    void findDrinks() throws Exception{
        mockMvc.perform(get("/drinks/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("drinks/findDrinks"))
                .andExpect(model().attributeExists("drink"));
    }

    @Test
    void findDrinksWithHttpBasic() throws Exception{
        mockMvc.perform(get("/drinks/find").with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(view().name("drinks/findDrinks"))
                .andExpect(model().attributeExists("drink"));
    }




}
