package com.elearning.drink.drinkfactory.web.controllers.api;

import com.elearning.drink.drinkfactory.services.DrinkService;
import com.elearning.drink.drinkfactory.web.model.DrinkDto;
import com.elearning.drink.drinkfactory.web.model.DrinkPagedList;
import com.elearning.drink.drinkfactory.web.model.DrinkStyleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
@RestController
public class DrinkRestController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final DrinkService drinkService;

    @PreAuthorize("hasAuthority('drink.read')")
    @GetMapping(produces = { "application/json" }, path = "drink")
    public ResponseEntity<DrinkPagedList> listDrinks(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                    @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                    @RequestParam(value = "drinkName", required = false) String drinkName,
                                                    @RequestParam(value = "drinkStyle", required = false) DrinkStyleEnum drinkStyle,
                                                    @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){

        log.debug("Listing Drinks");

        if (showInventoryOnHand == null) {
            showInventoryOnHand = false;
        }

        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        DrinkPagedList drinkList = drinkService.listDrinks(drinkName, drinkStyle, PageRequest.of(pageNumber, pageSize), showInventoryOnHand);

        return new ResponseEntity<>(drinkList, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('drink.read')")
    @GetMapping(path = {"drink/{drinkId}"}, produces = { "application/json" })
    public ResponseEntity<DrinkDto> getDrinkById(@PathVariable("drinkId") UUID drinkId,
                                                @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){

        log.debug("Get Request for DrinkId: " + drinkId);

        if (showInventoryOnHand == null) {
            showInventoryOnHand = false;
        }

        return new ResponseEntity<>(drinkService.findDrinkById(drinkId, showInventoryOnHand), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('drink.read')")
    @GetMapping(path = {"drinkUpc/{upc}"}, produces = { "application/json" })
    public ResponseEntity<DrinkDto> getDrinkByUpc(@PathVariable("upc") String upc){
        return new ResponseEntity<>(drinkService.findDrinkByUpc(upc), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('drink.create')")
    @PostMapping(path = "drink")
    public ResponseEntity saveNewDrink(@Valid @RequestBody DrinkDto drinkDto){

        DrinkDto savedDto = drinkService.saveDrink(drinkDto);

        HttpHeaders httpHeaders = new HttpHeaders();

        //todo hostname for uri
        httpHeaders.add("Location", "/api/v1/drink_service/" + savedDto.getId().toString());

        return new ResponseEntity(httpHeaders, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('drink.update')")
    @PutMapping(path = {"drink/{drinkId}"}, produces = { "application/json" })
    public ResponseEntity updateDrink(@PathVariable("drinkId") UUID drinkId, @Valid @RequestBody DrinkDto drinkDto){

        drinkService.updateDrink(drinkId, drinkDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('drink.delete')")
    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping({"drink/{drinkId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDrink(@PathVariable("drinkId") UUID drinkId){
        drinkService.deleteById(drinkId);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<List> badReqeustHandler(ConstraintViolationException e){
        List<String> errors = new ArrayList<>(e.getConstraintViolations().size());

        e.getConstraintViolations().forEach(constraintViolation -> {
            errors.add(constraintViolation.getPropertyPath().toString() + " : " + constraintViolation.getMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
