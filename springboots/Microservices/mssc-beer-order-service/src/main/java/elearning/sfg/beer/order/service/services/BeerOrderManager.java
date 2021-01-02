package elearning.sfg.beer.order.service.services;

import elearning.sfg.beer.order.service.domain.BeerOrder;

public interface BeerOrderManager {
    //called when we create a brand new BeerOrder, and ultimately will kick off the validation process.
    BeerOrder newBeerOrder(BeerOrder beerOrder);

}
