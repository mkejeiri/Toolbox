package com.supplychain.mssdrink.mappers;

import com.supplychain.mssdrink.domains.Drink;
import com.supplychain.mssdrink.dtos.DrinkDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface DrinkMapper {
    Drink DrinkDtoToDrink(DrinkDto drinkDto);
    DrinkDto DrinkToDrinkDto(Drink drink);
}
