package elearning.sfg.beer.inventory.web.mappers;

import elearning.sfg.beer.brewery.dtos.BeerInventoryDto;
import elearning.sfg.beer.inventory.domain.BeerInventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DateMapper.class})
public interface BeerInventoryMapper {

    BeerInventory beerInventoryDtoToBeerInventory(BeerInventoryDto beerInventoryDTO);

    BeerInventoryDto beerInventoryToBeerInventoryDto(BeerInventory beerInventory);
}
