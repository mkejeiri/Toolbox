package elearning.sfg.beer.inventory.web.mappers;

import elearning.sfg.beer.inventory.domain.BeerInventory;
import elearning.sfg.beer.brewery.dtos.BeerInventoryDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface BeerInventoryMapper {

    BeerInventory beerInventoryDtoToBeerInventory(BeerInventoryDto beerInventoryDTO);

    BeerInventoryDto beerInventoryToBeerInventoryDto(BeerInventory beerInventory);
}
