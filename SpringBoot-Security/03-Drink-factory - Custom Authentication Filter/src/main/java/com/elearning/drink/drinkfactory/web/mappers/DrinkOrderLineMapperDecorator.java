package com.elearning.drink.drinkfactory.web.mappers;

import com.elearning.drink.drinkfactory.domain.DrinkOrderLine;
import com.elearning.drink.drinkfactory.repositories.DrinkRepository;
import com.elearning.drink.drinkfactory.web.model.DrinkOrderLineDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class DrinkOrderLineMapperDecorator implements DrinkOrderLineMapper {
    private DrinkRepository drinkRepository;
    private DrinkOrderLineMapper drinkOrderLineMapper;

    @Autowired
    public void setDrinkRepository(DrinkRepository drinkRepository) {
        this.drinkRepository = drinkRepository;
    }

    @Autowired
    @Qualifier("delegate")
    public void setDrinkOrderLineMapper(DrinkOrderLineMapper drinkOrderLineMapper) {
        this.drinkOrderLineMapper = drinkOrderLineMapper;
    }

    @Override
    public DrinkOrderLineDto drinkOrderLineToDto(DrinkOrderLine line) {
        DrinkOrderLineDto orderLineDto = drinkOrderLineMapper.drinkOrderLineToDto(line);
        orderLineDto.setDrinkId(line.getDrink().getId());
        return orderLineDto;
    }

    @Override
    public DrinkOrderLine dtoToDrinkOrderLine(DrinkOrderLineDto dto) {
        DrinkOrderLine drinkOrderLine = drinkOrderLineMapper.dtoToDrinkOrderLine(dto);
        drinkOrderLine.setDrink(drinkRepository.getOne(dto.getDrinkId()));
        drinkOrderLine.setQuantityAllocated(0);
        return drinkOrderLine;
    }
}
