package elearning.wiremock.brewery.services;

import elearning.wiremock.brewery.web.model.BeerDto;
import elearning.wiremock.brewery.web.model.BeerPagedList;
import elearning.wiremock.brewery.web.model.BeerStyleEnum;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface BeerService {
    BeerPagedList listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest);

    BeerDto findBeerById(UUID beerId);
}
