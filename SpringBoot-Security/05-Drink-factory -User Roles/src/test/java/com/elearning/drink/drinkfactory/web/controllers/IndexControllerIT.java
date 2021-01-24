package com.elearning.drink.drinkfactory.web.controllers;

import com.elearning.drink.drinkfactory.repositories.CustomerRepository;
import com.elearning.drink.drinkfactory.repositories.DrinkInventoryRepository;
import com.elearning.drink.drinkfactory.repositories.DrinkRepository;
import com.elearning.drink.drinkfactory.services.BreweryService;
import com.elearning.drink.drinkfactory.services.DrinkService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
public class IndexControllerIT extends  BaseIT{
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
    @Test
    void testGetIndexSlash() throws Exception{
        mockMvc.perform(get("/" ))
                .andExpect(status().isOk());
    }
}
