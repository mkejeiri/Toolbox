package com.elearning.drink.drinkfactory.web.controllers;

import com.elearning.drink.drinkfactory.domain.Drink;
import com.elearning.drink.drinkfactory.repositories.DrinkRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    DrinkRepository drinkRepository;

    @DisplayName("Init new Form")
    @Nested
    class InitNewForm {
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.elearning.drink.drinkfactory.web.controllers.DrinkControllerIT#getStreamAllUsers")
        void initCreationFormAuth(String user, String password) throws Exception {
            mockMvc.perform(get("/drinks/new").with(httpBasic(user, password)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("drinks/createDrink"))
                    .andExpect(model().attributeExists("drink"));
        }

        @Test
        void initCreationFormNoAuth() throws Exception {
            mockMvc.perform(get("/drinks/new"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Init Find Drink Form")
    @Nested
    class FindForm {
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.elearning.drink.drinkfactory.web.controllers.DrinkControllerIT#getStreamAllUsers")
        void findDrinksFormAUTH(String user, String pwd) throws Exception {
            mockMvc.perform(get("/drinks/find")
                    .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("drinks/findDrinks"))
                    .andExpect(model().attributeExists("drink"));
        }

        @Test
        void findDrinksWithAnonymous() throws Exception {
            mockMvc.perform(get("/drinks/find").with(anonymous()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Process Find Drink Form")
    @Nested
    class ProcessFindForm {
        @Test
        void findDrinkForm() throws Exception {
            mockMvc.perform(get("/drinks").param("drinkName", ""))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.elearning.drink.drinkfactory.web.controllers.DrinkControllerIT#getStreamAllUsers")
        void findDrinkFormAuth(String user, String pwd) throws Exception {
            mockMvc.perform(get("/drinks").param("drinkName", "")
                    .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("Get Drink By Id")
    @Nested
    class GetByID {
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.elearning.drink.drinkfactory.web.controllers.DrinkControllerIT#getStreamAllUsers")
        void getDrinkByIdAUTH(String user, String pwd) throws Exception {
            Drink drink = drinkRepository.findAll().get(0);

            mockMvc.perform(get("/drinks/" + drink.getId())
                    .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("drinks/drinkDetails"))
                    .andExpect(model().attributeExists("drink"));
        }

        @Test
        void getDrinkByIdNoAuth() throws Exception {
            Drink drink = drinkRepository.findAll().get(0);

            mockMvc.perform(get("/drinks/" + drink.getId()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Early Examples - show case")
    @Nested
   class EarlyExample {
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
       void findDrinksWithHttpBasic() throws Exception {
           mockMvc.perform(get("/drinks/find").with(httpBasic("admin", "password")))
                   .andExpect(status().isOk())
                   .andExpect(view().name("drinks/findDrinks"))
                   .andExpect(model().attributeExists("drink"));
       }

       @Test
       void findDrinksWithAnonymous() throws Exception {
           mockMvc.perform(get("/drinks/find").with(anonymous()))
                   .andExpect(status().isUnauthorized());
       }
   }

}
