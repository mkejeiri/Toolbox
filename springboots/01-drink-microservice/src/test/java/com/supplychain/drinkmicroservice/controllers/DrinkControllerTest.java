package com.supplychain.drinkmicroservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychain.drinkmicroservice.model.DrinkDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DrinkController.class)
class DrinkControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;
    public   final  String baseUrl = "/api/drink/";

    @Test
    void getDrinkById() throws Exception {
        mockMvc.perform(get(baseUrl + UUID.randomUUID().toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void saveNewDrink() throws Exception {
        DrinkDto drinkDto = DrinkDto.builder().build();
        String drinkDTOJson = objectMapper.writeValueAsString(drinkDto);
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(drinkDTOJson))
                .andExpect(status().isCreated());
    }

    @Test
    void updateByDrinkId() throws Exception {
        DrinkDto drinkDto = DrinkDto.builder().build();
        String drinkDTOJson = objectMapper.writeValueAsString(drinkDto);
        mockMvc.perform(put(baseUrl+UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(drinkDTOJson))
                .andExpect(status().isNoContent());
    }
}