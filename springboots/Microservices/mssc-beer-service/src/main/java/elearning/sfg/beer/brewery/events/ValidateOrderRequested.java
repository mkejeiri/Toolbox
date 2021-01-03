package elearning.sfg.beer.brewery.events;

import elearning.sfg.beer.brewery.dtos.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidateOrderRequested {
    private BeerOrderDto beerOrderDto;
}
