package com.elearning.drink.drinkfactory.services;

import com.elearning.drink.drinkfactory.domain.Drink;
import com.elearning.drink.drinkfactory.repositories.DrinkRepository;
import com.elearning.drink.drinkfactory.web.mappers.DrinkMapper;
import com.elearning.drink.drinkfactory.web.model.DrinkDto;
import com.elearning.drink.drinkfactory.web.model.DrinkPagedList;
import com.elearning.drink.drinkfactory.web.model.DrinkStyleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DrinkServiceImpl implements DrinkService {

    private final DrinkRepository drinkRepository;
    private final DrinkMapper drinkMapper;

    @Override
    public DrinkPagedList listDrinks(String drinkName, DrinkStyleEnum drinkStyle, PageRequest pageRequest, Boolean showInventoryOnHand) {

        log.debug("Listing Drinks");

        DrinkPagedList drinkPagedList;
        Page<Drink> drinkPage;

        if (!StringUtils.isEmpty(drinkName) && !StringUtils.isEmpty(drinkStyle)) {
            //search both
            drinkPage = drinkRepository.findAllByDrinkNameAndDrinkStyle(drinkName, drinkStyle, pageRequest);
        } else if (!StringUtils.isEmpty(drinkName) && StringUtils.isEmpty(drinkStyle)) {
            //search drink_service name
            drinkPage = drinkRepository.findAllByDrinkName(drinkName, pageRequest);
        } else if (StringUtils.isEmpty(drinkName) && !StringUtils.isEmpty(drinkStyle)) {
            //search drink_service style
            drinkPage = drinkRepository.findAllByDrinkStyle(drinkStyle, pageRequest);
        } else {
            drinkPage = drinkRepository.findAll(pageRequest);
        }

        if (showInventoryOnHand) {
            drinkPagedList = new DrinkPagedList(drinkPage
                    .getContent()
                    .stream()
                    .map(drinkMapper::drinkToDrinkDto)
                    .collect(Collectors.toList()),
                    PageRequest
                            .of(drinkPage.getPageable().getPageNumber(),
                                    drinkPage.getPageable().getPageSize()),
                    drinkPage.getTotalElements());

        } else {
            drinkPagedList = new DrinkPagedList(drinkPage
                    .getContent()
                    .stream()
                    .map(drinkMapper::drinkToDrinkDto)
                    .collect(Collectors.toList()),
                    PageRequest
                            .of(drinkPage.getPageable().getPageNumber(),
                                    drinkPage.getPageable().getPageSize()),
                    drinkPage.getTotalElements());
        }
        return drinkPagedList;
    }

    @Override
    public DrinkDto findDrinkById(UUID drinkId, Boolean showInventoryOnHand) {

        log.debug("Finding Drink by id: " + drinkId);

        Optional<Drink> drinkOptional = drinkRepository.findById(drinkId);

        if (drinkOptional.isPresent()) {
            log.debug("Found DrinkId: " + drinkId);
            if(showInventoryOnHand) {
                return drinkMapper.drinkToDrinkDto(drinkOptional.get());
            } else {
                return drinkMapper.drinkToDrinkDto(drinkOptional.get());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found. UUID: " + drinkId);
        }
    }

    @Override
    public DrinkDto saveDrink(DrinkDto drinkDto) {
        return drinkMapper.drinkToDrinkDto(drinkRepository.save(drinkMapper.drinkDtoToDrink(drinkDto)));
    }

    @Override
    public void updateDrink(UUID drinkId, DrinkDto drinkDto) {
        Optional<Drink> drinkOptional = drinkRepository.findById(drinkId);

        drinkOptional.ifPresentOrElse(drink -> {
            drink.setDrinkName(drinkDto.getDrinkName());
            drink.setDrinkStyle(drinkDto.getDrinkStyle());
            drink.setPrice(drinkDto.getPrice());
            drink.setUpc(drinkDto.getUpc());
            drinkRepository.save(drink);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found. UUID: " + drinkId);
        });
    }

    @Override
    public void deleteById(UUID drinkId) {
        drinkRepository.deleteById(drinkId);
    }

    @Override
    public DrinkDto findDrinkByUpc(String upc) {
        return drinkMapper.drinkToDrinkDto(drinkRepository.findByUpc(upc));
    }
}
