package com.elearning.drink.drinkfactory.web.controllers.api;

import com.elearning.drink.drinkfactory.domain.security.User;
import com.elearning.drink.drinkfactory.security.perms.DrinkOrderReadV2Permission;
import com.elearning.drink.drinkfactory.services.DrinkOrderService;
import com.elearning.drink.drinkfactory.web.model.DrinkOrderDto;
import com.elearning.drink.drinkfactory.web.model.DrinkOrderPagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/orders/")
public class DrinkOrderControllerV2 {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final DrinkOrderService drinkOrderService;

    @DrinkOrderReadV2Permission
    @GetMapping
    public DrinkOrderPagedList listOrders(@AuthenticationPrincipal User user,
                                          @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                          @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if (user.getCustomer() != null) {
            return drinkOrderService.listOrders(user.getCustomer().getId(), PageRequest.of(pageNumber, pageSize));
        } else {
            return drinkOrderService.listOrders(PageRequest.of(pageNumber, pageSize));
        }
    }

    @DrinkOrderReadV2Permission
    @GetMapping("{orderId}")
    public DrinkOrderDto getOrder(@PathVariable("orderId") UUID orderId) {
        DrinkOrderDto drinkOrderDto = drinkOrderService.getOrderById(orderId);
        if (drinkOrderDto == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        log.debug("drinkOrder found " + drinkOrderDto);
        return drinkOrderDto;
    }
}