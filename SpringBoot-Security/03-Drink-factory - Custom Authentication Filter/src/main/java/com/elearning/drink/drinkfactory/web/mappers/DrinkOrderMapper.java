package com.elearning.drink.drinkfactory.web.mappers;

import com.elearning.drink.drinkfactory.domain.DrinkOrder;
import com.elearning.drink.drinkfactory.web.model.DrinkOrderDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class, DrinkOrderLineMapper.class})
public interface DrinkOrderMapper {

    DrinkOrderDto drinkOrderToDto(DrinkOrder drinkOrder);

    DrinkOrder dtoToDrinkOrder(DrinkOrderDto dto);
}
