package com.elearning.drink.drinkfactory.web.controllers.api;

import com.elearning.drink.drinkfactory.bootstrap.DefaultDrinkLoader;
import com.elearning.drink.drinkfactory.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class DrinkRestControllerTestIT extends BaseIT {

    @Test
    void findDrinks() throws Exception {
        mockMvc.perform(get("/api/v1/drink"))
                .andExpect(status().isOk());
    }

    @Test
    void getDrinkById() throws Exception {
        mockMvc.perform(get("/api/v1/drink/493410b3-dd0b-4b78-97bf-289f50f6e74f"))
                .andExpect(status().isOk());
    }

    @Test
    void findDrinkByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/drinkUpc/" + DefaultDrinkLoader.DRINK_1_UPC))
                .andExpect(status().isOk());
    }
}