package elearning.sfg.beer.brewery.events;

import elearning.sfg.beer.brewery.dtos.BeerOrderDto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidateOrderRequested {
    private BeerOrderDto beerOrderDto;
}
