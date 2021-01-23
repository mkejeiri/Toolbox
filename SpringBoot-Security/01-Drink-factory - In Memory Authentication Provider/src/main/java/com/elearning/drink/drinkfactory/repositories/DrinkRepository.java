package com.elearning.drink.drinkfactory.repositories;

import com.elearning.drink.drinkfactory.domain.Drink;
import com.elearning.drink.drinkfactory.web.model.DrinkStyleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DrinkRepository extends JpaRepository<Drink, UUID> {
    
    Page<Drink> findAllByDrinkName(String drinkName, Pageable pageable);

    Page<Drink> findAllByDrinkNameIsLike(String drinkName, Pageable pageable);

    Page<Drink> findAllByDrinkStyle(DrinkStyleEnum drinkStyle, Pageable pageable);

    Page<Drink> findAllByDrinkNameAndDrinkStyle(String drinkName, DrinkStyleEnum drinkStyle, Pageable pageable);

    Drink findByUpc(String upc);
}
