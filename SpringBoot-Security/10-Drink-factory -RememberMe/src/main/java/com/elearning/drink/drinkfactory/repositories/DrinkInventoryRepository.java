package com.elearning.drink.drinkfactory.repositories;

import com.elearning.drink.drinkfactory.domain.Drink;
import com.elearning.drink.drinkfactory.domain.DrinkInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DrinkInventoryRepository extends JpaRepository<DrinkInventory, UUID> {

    List<DrinkInventory> findAllByDrink(Drink drink);
}
