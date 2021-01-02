package elearning.sfg.beer.msscbeerservice.services;

import elearning.sfg.beer.brewery.dtos.BeerDto;
import elearning.sfg.beer.brewery.dtos.BeerPagedList;
import elearning.sfg.beer.brewery.dtos.BeerStyleEnum;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface BeerService {
    BeerPagedList listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest, boolean showInventoryOnHand);

    BeerDto getById(UUID beerId, boolean showInventoryOnHand);

    BeerDto saveNewBeer(BeerDto beerDto);

    BeerDto updateBeer(UUID beerId, BeerDto beerDto);

    BeerDto getByUpc(String upc);
}