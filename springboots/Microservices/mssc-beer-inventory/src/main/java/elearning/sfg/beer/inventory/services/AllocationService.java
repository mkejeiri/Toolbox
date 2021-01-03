package elearning.sfg.beer.inventory.services;

import elearning.sfg.beer.brewery.dtos.BeerOrderDto;

public interface AllocationService {
    Boolean allocateOrder(BeerOrderDto beerOrderDto);

    void deallocateOrder(BeerOrderDto beerOrderDto);

}
