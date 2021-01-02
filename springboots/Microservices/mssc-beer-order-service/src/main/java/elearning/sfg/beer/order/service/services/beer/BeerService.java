package elearning.sfg.beer.order.service.services.beer;

import elearning.sfg.beer.brewery.dtos.BeerDto;

import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    Optional<BeerDto> getBeerById(UUID uuid);

    Optional<BeerDto> getBeerByUpc(String upc);
}