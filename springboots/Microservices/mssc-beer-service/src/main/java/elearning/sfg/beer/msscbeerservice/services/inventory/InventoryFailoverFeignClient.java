package elearning.sfg.beer.msscbeerservice.services.inventory;

import elearning.sfg.beer.brewery.dtos.BeerInventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

/*
the feign client works like Spring Data JPA, We are going to provide an interface and decorate the interface with
some annotations and then at runtime Spring is going to provide an implementation for us.
*/

//name of inventory service used by eureka (inventory service application name)
@FeignClient(name = "inventory-failover")
public interface InventoryFailoverFeignClient  {
    @RequestMapping(method = RequestMethod.GET, value = "/inventory-failover")
    ResponseEntity<List<BeerInventoryDto>> getOnhandInventory();
}
