package com.elearning.drink.drinkfactory.security;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginFailureRepository extends JpaRepository<LoginFailure, Integer> {
}
