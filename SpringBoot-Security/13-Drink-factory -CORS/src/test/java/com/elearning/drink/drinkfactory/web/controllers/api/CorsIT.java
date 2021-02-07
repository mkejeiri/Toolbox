package com.elearning.drink.drinkfactory.web.controllers.api;

import com.elearning.drink.drinkfactory.web.controllers.BaseIT;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;

@SpringBootTest
public class CorsIT extends BaseIT {
    @WithUserDetails("admin")
    @Test
    void findDrinksAUTH() throws Exception {
        mockMvc.perform(get("/api/v1/drink/")
                .header("Origin", "https://elearning.drinkfactory"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void findDrinksPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/drink/")
                .header("Origin", "https://elearning.drinkfactory")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void postDrinksPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/drink/")
                .header("Origin", "https://elearning.drinkfactory")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void putDrinksPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/drink/1234")
                .header("Origin", "https://elearning.drinkfactory")
                .header("Access-Control-Request-Method", "PUT"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void deleteDrinksPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/drink/1234")
                .header("Origin", "https://elearning.drinkfactory")
                .header("Access-Control-Request-Method", "DELETE"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }
}
