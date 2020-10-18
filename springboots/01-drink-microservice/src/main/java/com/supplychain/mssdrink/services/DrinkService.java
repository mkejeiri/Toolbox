package com.supplychain.mssdrink.services;

import com.supplychain.mssdrink.dtos.DrinkDto;

import java.util.UUID;


public interface DrinkService {
    DrinkDto getDrinkById(UUID drinkId);
    DrinkDto saveNewDrink(DrinkDto drinkDto);
    DrinkDto updateDrink(UUID drinkId, DrinkDto drinkDto);
    UUID deleteDrinkById(UUID drinkId);
}
