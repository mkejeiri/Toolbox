package com.elearning.drink.drinkfactory.web.controllers.api;

import com.elearning.drink.drinkfactory.security.perms.CustomerAndOrderCreatePermission;
import com.elearning.drink.drinkfactory.security.perms.CustomerAndOrderReadPermission;
import com.elearning.drink.drinkfactory.security.perms.CustomerAndOrderUpdatePermission;
import com.elearning.drink.drinkfactory.services.DrinkOrderService;
import com.elearning.drink.drinkfactory.web.model.DrinkOrderDto;
import com.elearning.drink.drinkfactory.web.model.DrinkOrderPagedList;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/customers/{customerId}/")
@RestController
public class DrinkOrderController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final DrinkOrderService drinkOrderService;

    public DrinkOrderController(DrinkOrderService drinkOrderService) {
        this.drinkOrderService = drinkOrderService;
    }

    @CustomerAndOrderReadPermission
    @GetMapping("orders")
    public DrinkOrderPagedList listOrders(@PathVariable("customerId") UUID customerId,
                                          @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                          @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return drinkOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @CustomerAndOrderCreatePermission
    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public DrinkOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody DrinkOrderDto drinkOrderDto) {
        return drinkOrderService.placeOrder(customerId, drinkOrderDto);
    }

    @CustomerAndOrderReadPermission
    @GetMapping("orders/{orderId}")
    public DrinkOrderDto getOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        return drinkOrderService.getOrderById(customerId, orderId);
    }

    @CustomerAndOrderUpdatePermission
    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        drinkOrderService.pickupOrder(customerId, orderId);
    }
}