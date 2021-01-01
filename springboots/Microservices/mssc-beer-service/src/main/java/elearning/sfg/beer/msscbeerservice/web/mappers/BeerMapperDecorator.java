package elearning.sfg.beer.msscbeerservice.web.mappers;

import elearning.sfg.beer.msscbeerservice.services.inventory.BeerInventoryService;
import elearning.sfg.beer.msscbeerservice.web.model.BeerDto;
import elearning.sfg.beer.msscbeerservice.domain.Beer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor
public abstract class BeerMapperDecorator implements BeerMapper {
    @Autowired
    private BeerInventoryService beerInventoryService;

    @Autowired
    //@Qualifier("delegate")
    private BeerMapper delegate;


   /* @Autowired
    public void setBeerInventoryService(BeerInventoryService beerInventoryService) {
        this.beerInventoryService = beerInventoryService;
    }

    @Autowired
    public void setMapper(BeerMapper mapper) {
        this.mapper = mapper;
    }*/

    @Override
    public BeerDto beerToBeerDto(Beer beer) {
        BeerDto dto = delegate.beerToBeerDto(beer);
        return dto;
    }
    @Override
    public BeerDto beerToBeerDtoWithInventory(Beer beer) {
        BeerDto dto = delegate.beerToBeerDto(beer);
        dto.setQuantityOnHand(beerInventoryService.getOnhandInventory(beer.getId()));
        return dto;
    }

    @Override
    public Beer beerDtoToBeer(BeerDto beerDto) {
        return delegate.beerDtoToBeer(beerDto);
    }
}