package elearning.springframework.msscbeerservice.web.mappers;

import elearning.springframework.msscbeerservice.domain.Beer;
import elearning.springframework.msscbeerservice.services.inventory.BeerInventoryService;
import elearning.springframework.msscbeerservice.web.model.BeerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor
public abstract class BeerMapperDecorator implements BeerMapper {
    @Autowired
    private BeerInventoryService beerInventoryService;

    @Autowired
    //@Qualifier("delegate")
    private BeerMapper beerMapper;


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
        BeerDto dto = beerMapper.beerToBeerDto(beer);
        return dto;
    }
    @Override
    public BeerDto beerToBeerDtoWithInventory(Beer beer) {
        BeerDto dto = beerMapper.beerToBeerDto(beer);
        dto.setQuantityOnHand(beerInventoryService.getOnhandInventory(beer.getId()));
        return dto;
    }

    @Override
    public Beer beerDtoToBeer(BeerDto beerDto) {
        return beerMapper.beerDtoToBeer(beerDto);
    }
}