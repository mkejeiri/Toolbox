package com.elearning.drink.drinkfactory.repositories;

import com.elearning.drink.drinkfactory.domain.Customer;
import com.elearning.drink.drinkfactory.domain.DrinkOrder;
import com.elearning.drink.drinkfactory.domain.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

public interface DrinkOrderRepository extends JpaRepository<DrinkOrder, UUID> {

    Page<DrinkOrder> findAllByCustomer(Customer customer, Pageable pageable);

    List<DrinkOrder> findAllByOrderStatus(OrderStatusEnum orderStatusEnum);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    DrinkOrder findOneById(UUID id);

    @Query("select o from DrinkOrder o where o.id =?1 and " +
            "(true = :#{hasAuthority('order.read')} or o.customer.id = ?#{principal?.customer?.id})")
    DrinkOrder findOrderSecureById(UUID orderId);
}
