package com.supplychain.drinkmicroservice.controllers;

import com.supplychain.drinkmicroservice.model.DrinkDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/drink")
public class DrinkController {

    @GetMapping("/{drinkId}")
    public ResponseEntity<DrinkDto> getDrinkById(@PathVariable("drinkId") UUID drinkId) {
        //return  new ResponseEntity<>(UUID.randomUUID().toString());
        //TODO:impl
        DrinkDto drinkDto = DrinkDto.builder().build();
        return new ResponseEntity<>(drinkDto, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity saveNewDrink(@RequestBody DrinkDto drinkDto) {
        //TODO:impl
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping("/{drinkId}")
    public ResponseEntity updateByDrinkId(@PathVariable("drinkId") UUID drinkId,
                                          @RequestBody DrinkDto drinkDto) {
        //TODO: impl
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


}
