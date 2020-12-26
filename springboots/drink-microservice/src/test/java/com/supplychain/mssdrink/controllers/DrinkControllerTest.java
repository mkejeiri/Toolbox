package com.supplychain.mssdrink.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychain.mssdrink.data.boostrap.DrinkDataLoader;
import com.supplychain.mssdrink.web.dtos.DrinkDto;
import com.supplychain.mssdrink.web.dtos.DrinkStyleEnum;
import com.supplychain.mssdrink.web.services.DrinkService;
import com.supplychain.mssdrink.web.controllers.DrinkController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
@WebMvcTest(DrinkController.class)
class DrinkControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    DrinkService drinkService;


    public final String BASE_URL = "/api/drink/";

    private DrinkDto getValidDrink() {
        return  DrinkDto.builder()
                .id(null)
                .drinkName("LAGER")
                .drinkStyle(DrinkStyleEnum.LAGER)
                .version(123456789L)
                .price(new BigDecimal("12.04"))
                .upc(DrinkDataLoader.DRINK_1_UPC)
                .build();
    }

    @Test
    void getDrinkById() throws Exception {
        UUID drinkId = UUID.randomUUID();//null; //"00000000-0000-0000-0000-000000000000"
        //
        mockMvc.perform(get(BASE_URL + drinkId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void saveNewDrink() throws Exception {

        String drinkDTOJson = objectMapper.writeValueAsString( getValidDrink());
        DrinkDto drinkDtoRtByService = objectMapper.readValue(objectMapper.writeValueAsString( getValidDrink()),
                DrinkDto.class);
        drinkDtoRtByService.setId(UUID.randomUUID());
        when(drinkService.saveNewDrink( getValidDrink())).thenReturn(drinkDtoRtByService);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(drinkDTOJson))
                .andExpect(status().isCreated());
    }

    @Test
    void updateByDrinkId() throws Exception {
        UUID drinkId = UUID.randomUUID();
        when(drinkService.updateDrink(drinkId,  getValidDrink())).thenReturn( getValidDrink());
        String drinkDTOJson = objectMapper.writeValueAsString( getValidDrink());
        mockMvc.perform(put(BASE_URL + drinkId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(drinkDTOJson))
                .andExpect(status().isNoContent());
    }
}