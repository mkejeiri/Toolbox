package com.elearning.drink.drinkfactory.web.controllers.api;

import com.elearning.drink.drinkfactory.bootstrap.DefaultDrinkLoader;
import com.elearning.drink.drinkfactory.domain.Drink;
import com.elearning.drink.drinkfactory.repositories.DrinkRepository;
import com.elearning.drink.drinkfactory.web.controllers.BaseIT;
import com.elearning.drink.drinkfactory.web.model.DrinkStyleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest
@SpringBootTest
class DrinkRestControllerTestIT extends BaseIT {

    @Autowired
    DrinkRepository drinkRepository;

    @DisplayName("Delete tests")
    @Nested
    class deleteTests {

        public Drink drinkToDelete() {
            Random random = new Random();
            return drinkRepository.saveAndFlush(Drink.builder()
                    .id(UUID.randomUUID())
                    .drinkName("Drink to delete")
                    .drinkStyle(DrinkStyleEnum.IPA)
                    .minOnHand(12)
                    .upc(String.valueOf(random.nextInt(9999999)))
                    .build());
        }

        @Test
        void deleteDrinkBadCredentialsParamAuth() throws Exception {
            mockMvc.perform(delete("/api/v1/drink/" + drinkToDelete().getId())
                    .param("ApiKey", "customer")
                    .param("ApiSecret", "wrongpassword"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void deleteDrinkParamAuth() throws Exception {
            mockMvc.perform(delete("/api/v1/drink/" + drinkToDelete().getId())
                    .param("ApiKey", "customer")
                    .param("ApiSecret", "password"))
                    .andExpect(status().isOk());
        }

        @Test
        void deleteDrinkBadCredentials() throws Exception {
            mockMvc.perform(delete("/api/v1/drink/" + drinkToDelete().getId())
                    .header("Api-Key", "customer")
                    .header("Api-Secret", "wrongpassword"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void deleteDrink() throws Exception {
            mockMvc.perform(delete("/api/v1/drink/" + drinkToDelete().getId())
                    .header("Api-Key", "customer")
                    .header("Api-Secret", "password"))
                    .andExpect(status().isOk());
        }

        @Test
        void deleteDrinkHttpBasicUserRole() throws Exception {
            mockMvc.perform(delete("/api/v1/drink/" + drinkToDelete().getId())
                    .with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteDrinkHttpBasicCustomerRole() throws Exception {
            mockMvc.perform(delete("/api/v1/drink/" + drinkToDelete().getId())
                    .with(httpBasic("customer", "password")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteDrinkHttpBasic() throws Exception {
            mockMvc.perform(delete("/api/v1/drink/" + drinkToDelete().getId())
                    .with(httpBasic("admin", "password")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void deleteDrinkNoAuth() throws Exception {
            mockMvc.perform(delete("/api/v1/drink/" + drinkToDelete().getId()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void getDrinkById() throws Exception {
            mockMvc.perform(get("/api/v1/drink/" + drinkToDelete().getId()))
                    .andExpect(status().isUnauthorized());
        }

    }


    @Test
    void findDrinks() throws Exception {
        mockMvc.perform(get("/api/v1/drink"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findDrinkByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/drinkUpc/" + DefaultDrinkLoader.DRINK_1_UPC))
                .andExpect(status().isUnauthorized());
    }
}