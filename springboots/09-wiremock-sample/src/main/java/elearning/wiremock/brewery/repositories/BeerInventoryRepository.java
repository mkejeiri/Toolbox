package elearning.wiremock.brewery.repositories;

import elearning.wiremock.brewery.domain.Beer;
import elearning.wiremock.brewery.domain.BeerInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BeerInventoryRepository extends JpaRepository<BeerInventory, UUID> {

    List<BeerInventory> findAllByBeer(Beer beer);
}
