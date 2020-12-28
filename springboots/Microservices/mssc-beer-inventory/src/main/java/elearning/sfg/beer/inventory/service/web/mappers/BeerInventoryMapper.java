package elearning.sfg.beer.inventory.service.web.mappers;

import elearning.sfg.beer.inventory.service.web.model.BeerInventoryDto;
import elearning.sfg.beer.inventory.service.domain.BeerInventory;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface BeerInventoryMapper {

    BeerInventory beerInventoryDtoToBeerInventory(BeerInventoryDto beerInventoryDTO);

    BeerInventoryDto beerInventoryToBeerInventoryDto(BeerInventory beerInventory);
}
