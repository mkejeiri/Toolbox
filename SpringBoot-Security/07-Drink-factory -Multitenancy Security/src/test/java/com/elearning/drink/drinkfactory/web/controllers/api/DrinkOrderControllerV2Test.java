package com.elearning.drink.drinkfactory.web.controllers.api;

import com.elearning.drink.drinkfactory.bootstrap.DefaultDrinkLoader;
import com.elearning.drink.drinkfactory.domain.Customer;
import com.elearning.drink.drinkfactory.domain.Drink;
import com.elearning.drink.drinkfactory.domain.DrinkOrder;
import com.elearning.drink.drinkfactory.repositories.CustomerRepository;
import com.elearning.drink.drinkfactory.repositories.DrinkOrderRepository;
import com.elearning.drink.drinkfactory.repositories.DrinkRepository;
import com.elearning.drink.drinkfactory.web.controllers.BaseIT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import javax.transaction.Transactional;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class DrinkOrderControllerV2Test extends BaseIT {
    public static final String API_ROOT = "/api/v2/orders/";

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    DrinkOrderRepository drinkOrderRepository;

    @Autowired
    DrinkRepository drinkRepository;

    @Autowired
    ObjectMapper objectMapper;

    Customer stPeteCustomer;
    Customer dunedinCustomer;
    Customer keyWestCustomer;
    List<Drink> loadedDrinks;

    @BeforeEach
    void setUp() {
        stPeteCustomer = customerRepository.findAllByCustomerName(DefaultDrinkLoader.ST_PETE_DISTRIBUTING).orElseThrow();
        dunedinCustomer = customerRepository.findAllByCustomerName(DefaultDrinkLoader.DUNEDIN_DISTRIBUTING).orElseThrow();
        keyWestCustomer = customerRepository.findAllByCustomerName(DefaultDrinkLoader.KEY_WEST_DISTRIBUTORS).orElseThrow();
        loadedDrinks = drinkRepository.findAll();
    }

    @Test
    void listOrdersNotAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "admin")
    @Test
    void listOrdersAdminAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultDrinkLoader.STPETE_USER)
    @Test
    void listOrdersCustomerAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultDrinkLoader.DUNEDIN_USER)
    @Test
    void listOrdersCustomerDunedinAuth() throws Exception {
        mockMvc.perform(get(API_ROOT ))
                .andExpect(status().isOk());
    }


    @Disabled
    @Transactional
    @Test
    void getByOrderIdNotAuth() throws Exception {
        DrinkOrder drinkOrder = stPeteCustomer.getDrinkOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + drinkOrder.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Disabled
    @Transactional
    @WithUserDetails("admin")
    @Test
    void getByOrderIdADMIN() throws Exception {
        DrinkOrder drinkOrder = stPeteCustomer.getDrinkOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + drinkOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Disabled
    @Transactional
    @WithUserDetails(DefaultDrinkLoader.STPETE_USER)
    @Test
    void getByOrderIdCustomerAuth() throws Exception {
        DrinkOrder drinkOrder = stPeteCustomer.getDrinkOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + drinkOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Disabled
    @Transactional
    @WithUserDetails(DefaultDrinkLoader.DUNEDIN_USER)
    @Test
    void getByOrderIdCustomerNOTAuth() throws Exception {
        DrinkOrder drinkOrder = stPeteCustomer.getDrinkOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + drinkOrder.getId()))
                .andExpect(status().isForbidden());
    }
}