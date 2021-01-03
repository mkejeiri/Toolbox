package elearning.wiremock.brewery.web.mappers;

import elearning.wiremock.brewery.domain.Beer;
import elearning.wiremock.brewery.domain.BeerOrderLine;
import elearning.wiremock.brewery.web.model.BeerOrderDto;
import elearning.wiremock.brewery.web.model.BeerOrderLineDto;
import elearning.wiremock.brewery.domain.BeerOrder;
import org.mapstruct.Mapper;

@Mapper(uses = DateMapper.class)
public interface BeerOrderMapper {

    BeerOrderDto beerOrderToDto(BeerOrder beerOrder);

    BeerOrder dtoToBeerOrder(BeerOrderDto dto);

    BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line);

    default BeerOrderLine dtoToBeerOrder(BeerOrderLineDto dto){
        return BeerOrderLine.builder()
                .orderQuantity(dto.getOrderQuantity())
                .beer(Beer.builder().id(dto.getBeerId()).build())
                .build();
    }
}
