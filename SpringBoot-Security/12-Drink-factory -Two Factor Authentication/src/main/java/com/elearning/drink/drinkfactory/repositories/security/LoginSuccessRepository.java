package com.elearning.drink.drinkfactory.repositories.security;

import com.elearning.drink.drinkfactory.domain.security.LoginSuccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginSuccessRepository extends JpaRepository<LoginSuccess, Integer> {
}
