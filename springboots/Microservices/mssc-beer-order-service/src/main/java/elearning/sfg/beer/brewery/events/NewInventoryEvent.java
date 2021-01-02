package elearning.sfg.beer.brewery.events;

import elearning.sfg.beer.brewery.dtos.BeerDto;
import lombok.NoArgsConstructor;

//Jackson requires to have a no args constructor for serialization/deserialization
@NoArgsConstructor
public class NewInventoryEvent extends BeerEvent {
    public NewInventoryEvent(BeerDto beerDto) {
        super(beerDto);
    }
}