package com.supplychain.mssdrink.controllers;

import com.supplychain.mssdrink.dtos.DrinkDto;
import com.supplychain.mssdrink.services.DrinkService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/drink")
public class DrinkController {
    private final DrinkService drinkService;

    public DrinkController(DrinkService drinkService) {
        this.drinkService = drinkService;
    }

    @GetMapping("/{drinkId}")
    public ResponseEntity<DrinkDto> getDrinkById(@PathVariable("drinkId") UUID drinkId) {
        //return  new ResponseEntity<>(UUID.randomUUID().toString());
        //TODO:impl
        DrinkDto drinkDto = drinkService.getDrinkById(drinkId);
        return new ResponseEntity<>(drinkDto, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity handlePost(@RequestBody DrinkDto drinkDto) {
        //TODO:impl
        DrinkDto savedDto = drinkService.saveNewDrink(drinkDto);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/drink/" + savedDto.getId().toString());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{drinkId}")
    public ResponseEntity handleUpdate(@PathVariable("drinkId") UUID drinkId,
                                          @RequestBody DrinkDto drinkDto) {
        //TODO: impl
        DrinkDto drinkDtoUpdated = drinkService.updateDrink(drinkId,drinkDto);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    /*@DeleteMapping("/{drinkId}")
    public ResponseEntity deleteDrinkById(@PathVariable("drinkId") UUID drinkId) {
        //TODO:impl
        DrinkDto drinkDto = drinkService.deleteDrinkById(drinkId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }*/

    @DeleteMapping("/{drinkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDrinkById(@PathVariable("drinkId") UUID drinkId) {
        //TODO:impl
        UUID deletedDrinkId = drinkService.deleteDrinkById(drinkId);
    }

}
