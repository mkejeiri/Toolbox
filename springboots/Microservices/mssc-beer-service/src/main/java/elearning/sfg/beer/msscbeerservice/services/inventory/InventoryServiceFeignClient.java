package elearning.sfg.beer.msscbeerservice.services.inventory;

import elearning.sfg.beer.brewery.dtos.BeerInventoryDto;
import elearning.sfg.beer.msscbeerservice.config.FeignClientBasicAuthInterceptorConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

/*
the feign client works like Spring Data JPA, We are going to provide an interface and decorate the interface with
some annotations and then at runtime Spring is going to provide an implementation for us.
*/

//name of inventory service used by eureka (inventory service application name)
//when it fails, it fallbacks on its BeerInventoryServiceFeignClientFailoverImpl implementation
@FeignClient(name = "beer-inventory-service", fallback = BeerInventoryServiceFeignClientFailoverImpl.class,
        configuration = FeignClientBasicAuthInterceptorConfig.class)
public interface InventoryServiceFeignClient {
    @RequestMapping(method = RequestMethod.GET, value = BeerInventoryServiceRestTemplateImpl.INVENTORY_PATH)
    ResponseEntity<List<BeerInventoryDto>> getOnhandInventory(@PathVariable UUID beerId);
}
