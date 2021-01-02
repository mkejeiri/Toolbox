package elearning.sfg.beer.msscbeerservice.services.order;

import elearning.sfg.beer.brewery.dtos.BeerOrderDto;

public interface BeerOrderValidator {
    boolean isOrderValid(BeerOrderDto beerOrderDto);
}
