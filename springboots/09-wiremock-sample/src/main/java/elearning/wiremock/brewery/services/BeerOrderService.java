package elearning.wiremock.brewery.services;

import elearning.wiremock.brewery.web.model.BeerOrderPagedList;
import elearning.wiremock.brewery.web.model.BeerOrderDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BeerOrderService {
    BeerOrderPagedList listOrders(UUID customerId, Pageable pageable);

    BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto);

    BeerOrderDto getOrderById(UUID customerId, UUID orderId);

    void pickupOrder(UUID customerId, UUID orderId);
}
