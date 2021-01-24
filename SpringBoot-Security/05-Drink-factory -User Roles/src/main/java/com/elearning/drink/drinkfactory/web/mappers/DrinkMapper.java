package com.elearning.drink.drinkfactory.web.mappers;

import com.elearning.drink.drinkfactory.domain.Drink;
import com.elearning.drink.drinkfactory.web.model.DrinkDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = DateMapper.class)
@DecoratedWith(DrinkMapperDecorator.class)
public interface DrinkMapper {

    DrinkDto drinkToDrinkDto(Drink drink);

    Drink drinkDtoToDrink(DrinkDto drinkDto);
}
