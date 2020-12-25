package com.supplychain.mssdrink.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychain.mssdrink.web.model.DrinkDto;
import com.supplychain.mssdrink.services.DrinkService;
import com.supplychain.mssdrink.web.controller.DrinkController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = { DrinkController.class })
//@WebMvcTest(DrinkController.class)
@ExtendWith(MockitoExtension.class)
class DrinkControllerTest {
    MockMvc mockMvc;

    @InjectMocks
    DrinkController controller;

    @Mock
    DrinkService drinkService;
    ObjectMapper objectMapper;


    public final String BASE_URL = "/api/drink/";
    public final DrinkDto drinkDto;

    public DrinkControllerTest() {
        this.drinkDto = DrinkDto.builder()
                .id(null)
                .drinkName("LAGER")
                .drinkStyle("LAGER")
                .version(123456789L)
                .price(new BigDecimal("12.04"))
                .upc(123456789L)
                .build();
    }

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
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

        String drinkDTOJson = objectMapper.writeValueAsString(drinkDto);
        DrinkDto drinkDtoRtByService = objectMapper.readValue(objectMapper.writeValueAsString(drinkDto),
                DrinkDto.class);
        drinkDtoRtByService.setId(UUID.randomUUID());
        when(drinkService.saveNewDrink(drinkDto)).thenReturn(drinkDtoRtByService);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(drinkDTOJson))
                .andExpect(status().isCreated());
    }

    @Test
    void updateByDrinkId() throws Exception {
        UUID drinkId = UUID.randomUUID();
        when(drinkService.updateDrink(drinkId, drinkDto)).thenReturn(drinkDto);
        String drinkDTOJson = objectMapper.writeValueAsString(drinkDto);
        mockMvc.perform(put(BASE_URL + drinkId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(drinkDTOJson))
                .andExpect(status().isNoContent());
    }
}