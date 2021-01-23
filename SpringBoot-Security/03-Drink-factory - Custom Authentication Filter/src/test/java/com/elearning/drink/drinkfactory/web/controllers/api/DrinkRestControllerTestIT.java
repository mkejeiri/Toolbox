package com.elearning.drink.drinkfactory.web.controllers.api;

import com.elearning.drink.drinkfactory.bootstrap.DefaultDrinkLoader;
import com.elearning.drink.drinkfactory.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class DrinkRestControllerTestIT extends BaseIT {

    @Test
    void deleteDrinkBadCredentialsParamAuth() throws Exception {
        mockMvc.perform(delete("/api/v1/drink/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                .param("ApiKey", "customer")
                .param("ApiSecret", "wrongpassword"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void deleteDrinkParamAuth() throws Exception {
        mockMvc.perform(delete("/api/v1/drink/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                .param("ApiKey", "customer")
                .param("ApiSecret", "password"))
                .andExpect(status().isOk());
    }
    @Test
    void deleteDrinkBadCredentials() throws Exception {
        mockMvc.perform(delete("/api/v1/drink/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                .header("Api-Key", "customer")
                .header("Api-Secret", "wrongpassword"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void deleteDrink() throws Exception {
        mockMvc.perform(delete("/api/v1/drink/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                .header("Api-Key", "customer")
                .header("Api-Secret", "password"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteDrinkHttpBasic() throws Exception {
        mockMvc.perform(delete("/api/v1/drink/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                .with(httpBasic("customer","password")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteDrinkNoAuth() throws Exception {
        mockMvc.perform(delete("/api/v1/drink/493410b3-dd0b-4b78-97bf-289f50f6e74f"))
                .andExpect(status().isUnauthorized());
    }


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