package com.supplychain.mssdrink.services;

import com.supplychain.mssdrink.dtos.DrinkDto;
import com.supplychain.mssdrink.dtos.DrinkStyleEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class DrinkServiceImpl  implements  DrinkService{
    @Override
    public DrinkDto getDrinkById(UUID drinkId) {
        //TODO: impl persistence
        return DrinkDto.builder().id(drinkId)
                .createdAt(OffsetDateTime.now())
                .drinkName("ALE")
                .drinkStyle("ALE")
                .modifiedAt(OffsetDateTime.now())
                .quantityOnHand(1)
                .price(new BigDecimal("12.04"))
                .version(1L)
                .upc(123456789L)
                .build();
    }

    @Override
    public DrinkDto saveNewDrink(DrinkDto drinkDto) {
        //TODO: impl persistence
        return DrinkDto.builder().id(UUID.randomUUID()).build();
    }

    @Override
    public DrinkDto updateDrink(UUID drinkId, DrinkDto drinkDto) {
        //TODO: impl persistence
        return DrinkDto.builder().id(drinkId)
                .createdAt(OffsetDateTime.now())
                .drinkName(drinkDto.getDrinkName())
                .drinkStyle(drinkDto.getDrinkStyle())
                .modifiedAt(OffsetDateTime.now())
                .quantityOnHand(drinkDto.getQuantityOnHand())
                .price(drinkDto.getPrice())
                .version(drinkDto.getVersion())
                .upc(drinkDto.getUpc())
                .build();
    }

    @Override
    public UUID deleteDrinkById(UUID drinkId) {
        //TODO: impl persistence
        return drinkId;
    }
}
