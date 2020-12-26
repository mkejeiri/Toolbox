package com.supplychain.mssdrink.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class DrinkDtoBase {
    @Autowired
    ObjectMapper objectMapper;
    public DrinkDto getDrinkDto() {
        return DrinkDto.builder()
                .id(UUID.randomUUID())
                .version(1L)
                .drinkName("DrinkName")
                .drinkStyle("Ale")
                .createdAt(OffsetDateTime.now())
                .modifiedAt(OffsetDateTime.now())
                .price(new BigDecimal("12.99"))
                .upc(123123123123L)
                .myLocalDate(LocalDate.now())
                .build();
    }
}
