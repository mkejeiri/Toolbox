package com.supplychain.mssdrink.web.services;

import com.supplychain.mssdrink.data.repositories.DrinkRepository;
import com.supplychain.mssdrink.domains.models.Drink;
import com.supplychain.mssdrink.web.dtos.DrinkDto;
import com.supplychain.mssdrink.web.mappers.DrinkMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DrinkServiceImpl implements DrinkService {
    @Autowired
    private final DrinkRepository drinkRepository;
    private final DrinkMapper drinkMapper;
    @Override
    public DrinkDto getDrinkById(UUID id) {
        return drinkMapper.DrinkToDrinkDto(drinkRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    @Override
    public DrinkDto saveNewDrink(DrinkDto drink) {
        return drinkMapper.DrinkToDrinkDto(drinkRepository.save(drinkMapper.DrinkDtoToDrink(drink)));
    }

    @Override
    public DrinkDto updateDrink(UUID id, DrinkDto drinkDto) {
        DrinkDto drinkDtoSaved = getDrinkById(id);
        drinkDtoSaved.setDrinkName(drinkDto.getDrinkName());
        drinkDtoSaved.setDrinkStyle(drinkDto.getDrinkStyle());
        drinkDtoSaved.setPrice(drinkDto.getPrice());
        drinkDtoSaved.setModifiedAt(OffsetDateTime.now());
        drinkDtoSaved.setUpc(drinkDto.getUpc());
        return drinkMapper.DrinkToDrinkDto(drinkRepository.save(drinkMapper.DrinkDtoToDrink(drinkDtoSaved)));
    }

    @Override
    public UUID deleteDrinkById(UUID id) {
        return null;
    }
}


 /* @Override
    public DrinkDto getDrinkById(UUID drinkId) {
        //TODO: impl persistence
        return DrinkDto.builder().id(drinkId)
                .createdAt(OffsetDateTime.now())
                .drinkName("ALE")
                .drinkStyle("ALE")
                .modifiedAt(OffsetDateTime.now())
                .quantityOnHand(1)
                .price(new BigDecimal("12.04"))
                .version(1L)
                .upc(123456789L)
                .build();
    }

    @Override
    public DrinkDto saveNewDrink(DrinkDto drinkDto) {
        //TODO: impl persistence
        return DrinkDto.builder().id(UUID.randomUUID()).build();
    }

    @Override
    public DrinkDto updateDrink(UUID drinkId, DrinkDto drinkDto) {
        //TODO: impl persistence
        return DrinkDto.builder().id(drinkId)
                .createdAt(OffsetDateTime.now())
                .drinkName(drinkDto.getDrinkName())
                .drinkStyle(drinkDto.getDrinkStyle())
                .modifiedAt(OffsetDateTime.now())
                .quantityOnHand(drinkDto.getQuantityOnHand())
                .price(drinkDto.getPrice())
                .version(drinkDto.getVersion())
                .upc(drinkDto.getUpc())
                .build();
    }

    @Override
    public UUID deleteDrinkById(UUID drinkId) {
        //TODO: impl persistence
        return drinkId;
    }*/
