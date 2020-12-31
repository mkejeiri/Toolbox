package com.elearning.msscstatemachine.repository;

import com.elearning.msscstatemachine.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}