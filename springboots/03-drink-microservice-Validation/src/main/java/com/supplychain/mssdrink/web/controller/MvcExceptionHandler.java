package com.supplychain.mssdrink.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class MvcExceptionHandler {
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
