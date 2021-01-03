package elearning.sfg.beer.brewery.events;

import elearning.sfg.beer.brewery.dtos.BeerOrderDto;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class DeallocateOrderRequested {
    private final BeerOrderDto beerOrderDto;
}
