package com.supplychain.mssdrink.web.services;

import com.supplychain.mssdrink.web.dtos.DrinkDto;

import java.util.UUID;

public interface DrinkService {
    DrinkDto getDrinkById(UUID id) throws NotFoundException;
    DrinkDto saveNewDrink(DrinkDto drink);
    DrinkDto updateDrink(UUID Iid, DrinkDto drink);
    UUID deleteDrinkById(UUID id);
}
