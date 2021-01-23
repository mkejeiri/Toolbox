
package com.elearning.drink.drinkfactory.services;

import com.elearning.drink.drinkfactory.domain.DrinkOrder;
import com.elearning.drink.drinkfactory.repositories.CustomerRepository;
import com.elearning.drink.drinkfactory.repositories.DrinkOrderRepository;
import com.elearning.drink.drinkfactory.web.mappers.DrinkOrderMapper;
import com.elearning.drink.drinkfactory.web.model.DrinkOrderDto;
import com.elearning.drink.drinkfactory.web.model.DrinkOrderPagedList;
import com.elearning.drink.drinkfactory.domain.Customer;
import com.elearning.drink.drinkfactory.domain.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DrinkOrderServiceImpl implements DrinkOrderService {

    private final DrinkOrderRepository drinkOrderRepository;
    private final CustomerRepository customerRepository;
    private final DrinkOrderMapper drinkOrderMapper;

    @Override
    public DrinkOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Page<DrinkOrder> drinkOrderPage =
                    drinkOrderRepository.findAllByCustomer(customerOptional.get(), pageable);

            return new DrinkOrderPagedList(drinkOrderPage
                    .stream()
                    .map(drinkOrderMapper::drinkOrderToDto)
                    .collect(Collectors.toList()), PageRequest.of(
                    drinkOrderPage.getPageable().getPageNumber(),
                    drinkOrderPage.getPageable().getPageSize()),
                    drinkOrderPage.getTotalElements());
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public DrinkOrderDto placeOrder(UUID customerId, DrinkOrderDto drinkOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            DrinkOrder drinkOrder = drinkOrderMapper.dtoToDrinkOrder(drinkOrderDto);
            drinkOrder.setId(null); //should not be set by outside client
            drinkOrder.setCustomer(customerOptional.get());
            drinkOrder.setOrderStatus(OrderStatusEnum.NEW);

            drinkOrder.getDrinkOrderLines().forEach(line -> line.setDrinkOrder(drinkOrder));

            DrinkOrder savedDrinkOrder = drinkOrderRepository.saveAndFlush(drinkOrder);

            log.debug("Saved Drink Order: " + drinkOrder.getId());

            return drinkOrderMapper.drinkOrderToDto(savedDrinkOrder);
        }
        //todo add exception type
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    public DrinkOrderDto getOrderById(UUID customerId, UUID orderId) {
        return drinkOrderMapper.drinkOrderToDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        DrinkOrder drinkOrder = getOrder(customerId, orderId);
        drinkOrder.setOrderStatus(OrderStatusEnum.PICKED_UP);

        drinkOrderRepository.save(drinkOrder);
    }

    private DrinkOrder getOrder(UUID customerId, UUID orderId){
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if(customerOptional.isPresent()){
            Optional<DrinkOrder> drinkOrderOptional = drinkOrderRepository.findById(orderId);

            if(drinkOrderOptional.isPresent()){
                DrinkOrder drinkOrder = drinkOrderOptional.get();

                // fall to exception if customer id's do not match - order not for customer
                if(drinkOrder.getCustomer().getId().equals(customerId)){
                    return drinkOrder;
                }
            }
            throw new RuntimeException("Drink Order Not Found");
        }
        throw new RuntimeException("Customer Not Found");
    }
}
