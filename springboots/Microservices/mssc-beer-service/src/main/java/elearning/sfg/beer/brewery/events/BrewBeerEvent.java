package elearning.sfg.beer.brewery.events;

import elearning.sfg.beer.brewery.dtos.BeerDto;
import lombok.NoArgsConstructor;

//Jackson requires to have a no args constructor for serialization/deserialization
@NoArgsConstructor
public class BrewBeerEvent extends BeerEvent {

    public BrewBeerEvent(BeerDto beerDto) {
        super(beerDto);
    }
}