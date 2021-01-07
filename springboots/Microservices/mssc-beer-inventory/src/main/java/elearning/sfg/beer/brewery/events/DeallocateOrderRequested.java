package elearning.sfg.beer.brewery.events;

import elearning.sfg.beer.brewery.dtos.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DeallocateOrderRequested {
    private BeerOrderDto beerOrderDto;
}
