package elearning.sfg.beer.msscbeerservice.services.inventory;

import elearning.sfg.beer.brewery.dtos.BeerInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
//@Profile("local-discovery")
@Service
public class BeerInventoryServiceFeignClientFailoverImpl implements InventoryServiceFeignClient {
    private final InventoryFailoverFeignClient inventoryFailoverFeignClient;
    @Override
    public ResponseEntity<List<BeerInventoryDto>> getOnhandInventory(UUID beerId) {
        log.debug("fetching quantity onhand from failover...");
        return inventoryFailoverFeignClient.getOnhandInventory();
    }

}
