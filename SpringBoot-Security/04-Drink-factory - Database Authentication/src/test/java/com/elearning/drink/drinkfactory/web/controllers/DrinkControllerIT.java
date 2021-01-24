package com.elearning.drink.drinkfactory.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest
@SpringBootTest
public class DrinkControllerIT extends BaseIT {
//Inherited from BaseIT
   /* @Autowired
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
    }*/

    //force any user to bypass security core module
    @WithMockUser("spring")
    @Test
    void findDrinks() throws Exception {
        mockMvc.perform(get("/drinks/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("drinks/findDrinks"))
                .andExpect(model().attributeExists("drink"));
    }

    @Test
    void initiCreationCustomerForm() throws Exception {
        mockMvc.perform(get("/drinks/new").with(httpBasic("customer", "password")))
                .andExpect(status().isOk())
                .andExpect(view().name("drinks/createDrink"))
                .andExpect(model().attributeExists("drink"));
    }
 @Test
    void initiCreationForm() throws Exception {
        mockMvc.perform(get("/drinks/new").with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(view().name("drinks/createDrink"))
                .andExpect(model().attributeExists("drink"));
    }

    @Test
    void findDrinksWithHttpBasic() throws Exception {
        mockMvc.perform(get("/drinks/find").with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(view().name("drinks/findDrinks"))
                .andExpect(model().attributeExists("drink"));
    }
 @Test
    void findDrinksWithAnonymous() throws Exception {
        mockMvc.perform(get("/drinks/find").with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(view().name("drinks/findDrinks"))
                .andExpect(model().attributeExists("drink"));
    }

}
