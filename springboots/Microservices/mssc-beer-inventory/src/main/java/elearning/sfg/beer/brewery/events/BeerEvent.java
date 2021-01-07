package elearning.sfg.beer.brewery.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BeerEvent implements Serializable {
    static final long serialVersionUID = 1956315996100526693L;
    private BeerDto beerDto;
}

