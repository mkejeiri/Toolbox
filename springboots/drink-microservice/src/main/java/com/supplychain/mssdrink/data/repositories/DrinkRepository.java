package com.supplychain.mssdrink.data.repositories;
import com.supplychain.mssdrink.domains.models.Drink;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

//No need to define it as '@Repository', because it's already defined as repository by JPA
public interface DrinkRepository  extends PagingAndSortingRepository<Drink,UUID> {
}
