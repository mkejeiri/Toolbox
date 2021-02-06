package com.elearning.drink.drinkfactory.repositories.security;

import com.elearning.drink.drinkfactory.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
