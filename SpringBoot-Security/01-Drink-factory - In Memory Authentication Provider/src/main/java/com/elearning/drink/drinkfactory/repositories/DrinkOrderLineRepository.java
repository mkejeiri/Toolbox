package com.elearning.drink.drinkfactory.repositories;

import com.elearning.drink.drinkfactory.domain.DrinkOrderLine;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface DrinkOrderLineRepository extends PagingAndSortingRepository<DrinkOrderLine, UUID> {
}
