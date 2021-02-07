package com.elearning.drink.drinkfactory.repositories.security;

import com.elearning.drink.drinkfactory.domain.security.LoginFailure;
import com.elearning.drink.drinkfactory.domain.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface LoginFailureRepository extends JpaRepository<LoginFailure, Integer> {
    List<LoginFailure> findAllByUserAndCreatedDateIsAfter(User user, Timestamp timestamp);
}
