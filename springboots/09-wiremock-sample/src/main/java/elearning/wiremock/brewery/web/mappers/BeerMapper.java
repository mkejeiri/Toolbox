package elearning.wiremock.brewery.web.mappers;

import elearning.wiremock.brewery.domain.Beer;
import elearning.wiremock.brewery.web.model.BeerDto;
import org.mapstruct.Mapper;

@Mapper(uses = DateMapper.class)
public interface BeerMapper {

    BeerDto beerToBeerDto(Beer beer);

    Beer beerDtoToBeer(BeerDto beerDto);
}
