
package com.elearning.drink.drinkfactory.services;


import com.elearning.drink.drinkfactory.web.model.DrinkOrderDto;
import com.elearning.drink.drinkfactory.web.model.DrinkOrderPagedList;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DrinkOrderService {
    DrinkOrderPagedList listOrders(UUID customerId, Pageable pageable);

    DrinkOrderDto placeOrder(UUID customerId, DrinkOrderDto drinkOrderDto);

    DrinkOrderDto getOrderById(UUID customerId, UUID orderId);

    void pickupOrder(UUID customerId, UUID orderId);

    DrinkOrderPagedList listOrders(Pageable pageable);
}
