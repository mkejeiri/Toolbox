package com.elearning.drink.drinkfactory.services;

import com.elearning.drink.drinkfactory.web.model.DrinkDto;
import com.elearning.drink.drinkfactory.web.model.DrinkPagedList;
import com.elearning.drink.drinkfactory.web.model.DrinkStyleEnum;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface DrinkService {

    DrinkPagedList listDrinks(String drinkName, DrinkStyleEnum drinkStyle, PageRequest pageRequest, Boolean showInventoryOnHand);

    DrinkDto findDrinkById(UUID drinkId, Boolean showInventoryOnHand);

    DrinkDto saveDrink(DrinkDto drinkDto);

    void updateDrink(UUID drinkId, DrinkDto drinkDto);

    void deleteById(UUID drinkId);

    DrinkDto findDrinkByUpc(String upc);
}
