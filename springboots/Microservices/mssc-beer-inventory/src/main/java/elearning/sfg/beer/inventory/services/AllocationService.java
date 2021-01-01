package elearning.sfg.beer.inventory.services;

import elearning.sfg.beer.inventory.web.model.BeerOrderDto;

public interface AllocationService {
    Boolean allocateOrder(BeerOrderDto beerOrderDto);
}
