package com.supplychain.mssdrink.web.mappers;

import com.supplychain.mssdrink.domains.models.Drink;
import com.supplychain.mssdrink.web.dtos.DrinkDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface DrinkMapper {
    Drink DrinkDtoToDrink(DrinkDto drinkDto);
    DrinkDto DrinkToDrinkDto(Drink drink);
}
