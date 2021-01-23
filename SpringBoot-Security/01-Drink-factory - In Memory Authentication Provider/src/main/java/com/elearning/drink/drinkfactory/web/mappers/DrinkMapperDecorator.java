package com.elearning.drink.drinkfactory.web.mappers;

import com.elearning.drink.drinkfactory.domain.Drink;
import com.elearning.drink.drinkfactory.domain.DrinkInventory;
import com.elearning.drink.drinkfactory.web.model.DrinkDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class DrinkMapperDecorator implements DrinkMapper {

    private DrinkMapper drinkMapper;

    @Autowired
    @Qualifier("delegate")
    public void setDrinkMapper(DrinkMapper drinkMapper) {
        this.drinkMapper = drinkMapper;
    }

    @Override
    public DrinkDto drinkToDrinkDto(Drink drink) {

        DrinkDto dto = drinkMapper.drinkToDrinkDto(drink);

        if(drink.getDrinkInventory() != null && drink.getDrinkInventory().size() > 0) {
            dto.setQuantityOnHand(drink.getDrinkInventory()
                    .stream().map(DrinkInventory::getQuantityOnHand)
                    .reduce(0, Integer::sum));

        }

        return dto;
    }
}
