package elearning.sfg.beer.common.events;

import elearning.sfg.beer.msscbeerservice.web.model.BeerDto;
import lombok.NoArgsConstructor;

//Jackson requires to have a no args constructor for serialization/deserialization
@NoArgsConstructor
public class NewInventoryEvent extends BeerEvent {
    public NewInventoryEvent(BeerDto beerDto) {
        super(beerDto);
    }
}