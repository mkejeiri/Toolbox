package com.supplychain.mssdrink.controllers;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import com.supplychain.mssdrink.dtos.DrinkDto;
import com.supplychain.mssdrink.services.DrinkService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

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
    public ResponseEntity handleUpdate(@PathVariable("drinkId") UUID drinkId,
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
    public void deleteDrinkById(@PathVariable("drinkId") UUID drinkId) {
        //TODO:impl
        UUID deletedDrinkId = drinkService.deleteDrinkById(drinkId);
    }

    //if any of the model validation constraints is violated a ConstraintViolationException
    // is thrown and handled at this level, for that to happens all @RequestBody's should be preceded by @Valid

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List> validationErrorHandler(ConstraintViolationException constraintViolationException){
        List<String> errors = new ArrayList<>(constraintViolationException.getConstraintViolations().size());

        //alternative loop
        /*for (ConstraintViolation<?> constraintViolation : constraintViolationException.getConstraintViolations()) {
            errors.add(constraintViolation.getPropertyPath() + " : " + constraintViolation.getMessage());
        }*/

        constraintViolationException.getConstraintViolations().forEach(constraintViolation -> {
            errors.add(constraintViolation.getPropertyPath() + " : " + constraintViolation.getMessage());
        });

        //Lambda alternative syntax
        /*constraintViolationException.getConstraintViolations().forEach(new Consumer<ConstraintViolation<?>>() {
            @Override
            public void accept(ConstraintViolation<?> constraintViolation) {
                errors.add(constraintViolation.getPropertyPath() + " : " + constraintViolation.getMessage());
            }
        });*/
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
