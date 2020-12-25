package com.supplychain.mssdrink.web.controller;

import com.supplychain.mssdrink.web.model.DrinkDto;
import com.supplychain.mssdrink.services.DrinkService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;
@Validated //this will perform validation ot controller method input parameters level:
// e.g. an null drinkId will throw exception:
//public ResponseEntity<DrinkDto> getDrinkById(@NotNull @PathVariable("drinkId") UUID drinkId) {
@RestController
@RequestMapping("/api/drink")
public class DrinkController {
    private final DrinkService drinkService;

    public DrinkController(DrinkService drinkService) {
        this.drinkService = drinkService;
    }

    @GetMapping("/{drinkId}")
    public ResponseEntity<DrinkDto> getDrinkById(@NotNull @PathVariable("drinkId") UUID drinkId) {
        //return  new ResponseEntity<>(UUID.randomUUID().toString());
        //TODO:impl
        DrinkDto drinkDto = drinkService.getDrinkById(drinkId);
        return new ResponseEntity<>(drinkDto, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity handlePost(@Valid @RequestBody DrinkDto drinkDto) {
        //TODO:impl
        DrinkDto savedDto = drinkService.saveNewDrink(drinkDto);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/drink/" + savedDto.getId().toString());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{drinkId}")
    public ResponseEntity  handleUpdate(@PathVariable("drinkId") UUID drinkId,
                                       @Valid @RequestBody DrinkDto drinkDto) {
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
    public ResponseEntity deleteDrinkById(@PathVariable("drinkId") UUID drinkId) {
        //TODO:impl
        UUID deletedDrinkId = drinkService.deleteDrinkById(drinkId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
