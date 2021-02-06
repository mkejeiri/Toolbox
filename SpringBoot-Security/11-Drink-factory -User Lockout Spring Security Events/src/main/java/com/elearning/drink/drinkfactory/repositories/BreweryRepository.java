package com.elearning.drink.drinkfactory.repositories;

import com.elearning.drink.drinkfactory.domain.Brewery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BreweryRepository extends JpaRepository<Brewery, UUID> {
}
