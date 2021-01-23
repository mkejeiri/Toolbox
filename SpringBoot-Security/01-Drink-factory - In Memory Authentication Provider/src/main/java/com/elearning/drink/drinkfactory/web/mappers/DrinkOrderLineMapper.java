package com.elearning.drink.drinkfactory.web.mappers;

import com.elearning.drink.drinkfactory.domain.DrinkOrderLine;
import com.elearning.drink.drinkfactory.web.model.DrinkOrderLineDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(DrinkOrderLineMapperDecorator.class)
public interface DrinkOrderLineMapper {
    DrinkOrderLineDto drinkOrderLineToDto(DrinkOrderLine line);

    DrinkOrderLine dtoToDrinkOrderLine(DrinkOrderLineDto dto);
}
