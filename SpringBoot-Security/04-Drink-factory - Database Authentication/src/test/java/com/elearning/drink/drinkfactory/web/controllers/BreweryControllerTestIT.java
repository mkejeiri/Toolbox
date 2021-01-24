package com.elearning.drink.drinkfactory.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BreweryControllerTestIT extends BaseIT {

    @Test
    void getBreweriesAnonymous() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getBreweriesHttpBasicUserRole() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBreweriesHttpBasicCustomerRole() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                .with(httpBasic("customer", "password")))
                .andExpect(status().isOk());
    }
    @Test
    void getBreweriesHttpBasicAdminRole() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                .with(httpBasic("admin", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void getMvcBreweriesHttpBasicCustomerRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                .with(httpBasic("customer", "password")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getMvcBreweriesHttpBasicAdminRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                .with(httpBasic("admin", "password")))
                .andExpect(status().is2xxSuccessful());
    }


    @Test
    void getMvcBreweriesHttpBasicUserRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }
    @Test
    void getMvcBreweriesHttpBasicAnonymous() throws Exception {
        mockMvc.perform(get("/brewery/breweries"))
                .andExpect(status().isUnauthorized());
    }
}