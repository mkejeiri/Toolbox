package com.elearning.drink.drinkfactory.web.controllers.api;

import com.elearning.drink.drinkfactory.bootstrap.DefaultDrinkLoader;
import com.elearning.drink.drinkfactory.domain.Customer;
import com.elearning.drink.drinkfactory.domain.Drink;
import com.elearning.drink.drinkfactory.domain.DrinkOrder;
import com.elearning.drink.drinkfactory.repositories.CustomerRepository;
import com.elearning.drink.drinkfactory.repositories.DrinkOrderRepository;
import com.elearning.drink.drinkfactory.repositories.DrinkRepository;
import com.elearning.drink.drinkfactory.web.controllers.BaseIT;
import com.elearning.drink.drinkfactory.web.model.DrinkOrderDto;
import com.elearning.drink.drinkfactory.web.model.DrinkOrderLineDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class DrinkOrderControllerTest extends BaseIT {

    public static final String API_ROOT = "/api/v1/customers/";

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

//cant use nested tests bug - https://github.com/spring-projects/spring-security/issues/8793
//    @DisplayName("Create Test")
//    @Nested
//    class createOrderTests {


    @Test
    void createOrderNotAuth() throws Exception {
        DrinkOrderDto drinkOrderDto = buildOrderDto(stPeteCustomer, loadedDrinks.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(drinkOrderDto)))
                .andExpect(status().isUnauthorized());
    }

    //UserDetails for the STPETE_USER : underneath the covers,
//Spring Security will authenticate the user, so we don't have to use HTTP basic,
//We don't have to provide the user credentials inside the test itself.
//We're instructing the test environment to execute this test with the Spring Security
//Context for that specific user (i.e. STPETE_USER). Also a very good approach to have when we use more than
//one authentication method or maybe that authentication method is going to change somewhere down the road.
    @WithUserDetails("admin")
    @Test
    void createOrderUserAdmin() throws Exception {
        DrinkOrderDto drinkOrderDto = buildOrderDto(stPeteCustomer, loadedDrinks.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(drinkOrderDto)))
                .andExpect(status().isCreated());
    }

    @WithUserDetails(DefaultDrinkLoader.STPETE_USER)
    @Test
    void createOrderUserAuthCustomer() throws Exception {
        DrinkOrderDto drinkOrderDto = buildOrderDto(stPeteCustomer, loadedDrinks.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(drinkOrderDto)))
                .andExpect(status().isCreated());
    }

    @WithUserDetails(DefaultDrinkLoader.KEYWEST_USER)
    @Test
    void createOrderUserNOTAuthCustomer() throws Exception {
        DrinkOrderDto drinkOrderDto = buildOrderDto(stPeteCustomer, loadedDrinks.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(drinkOrderDto)))
                .andExpect(status().isForbidden());
    }

    // }
    @Test
    void listOrdersNotAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "admin")
    @Test
    void listOrdersAdminAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultDrinkLoader.STPETE_USER)
    @Test
    void listOrdersCustomerAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultDrinkLoader.DUNEDIN_USER)
    @Test
    void listOrdersCustomerNOTAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOrdersNoAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    void getByOrderIdNotAuth() throws Exception {
        DrinkOrder drinkOrder = stPeteCustomer.getDrinkOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + drinkOrder.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @WithUserDetails("admin")
    @Test
    void getByOrderIdADMIN() throws Exception {
        DrinkOrder drinkOrder = stPeteCustomer.getDrinkOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + drinkOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(DefaultDrinkLoader.STPETE_USER)
    @Test
    void getByOrderIdCustomerAuth() throws Exception {
        DrinkOrder drinkOrder = stPeteCustomer.getDrinkOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + drinkOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(DefaultDrinkLoader.DUNEDIN_USER)
    @Test
    void getByOrderIdCustomerNOTAuth() throws Exception {
        DrinkOrder drinkOrder = stPeteCustomer.getDrinkOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + drinkOrder.getId()))
                .andExpect(status().isForbidden());
    }

    @Transactional
    @Test
    void pickUpOrderNotAuth() throws Exception {
        DrinkOrder drinkOrder = stPeteCustomer.getDrinkOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + drinkOrder.getId() + "/pickup"))
                .andExpect(status().isUnauthorized());

    }

    @Transactional
    @WithUserDetails("admin")
    @Test
    void pickUpOrderAdminUser() throws Exception {
        DrinkOrder drinkOrder = stPeteCustomer.getDrinkOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + drinkOrder.getId() + "/pickup"))
                .andExpect(status().isNoContent());
    }

    @Transactional
    @WithUserDetails(DefaultDrinkLoader.STPETE_USER)
    @Test
    void pickUpOrderCustomerUserAUTH() throws Exception {
        DrinkOrder drinkOrder = stPeteCustomer.getDrinkOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + drinkOrder.getId() + "/pickup"))
                .andExpect(status().isNoContent());
    }

    @Transactional
    @WithUserDetails(DefaultDrinkLoader.DUNEDIN_USER)
    @Test
    void pickUpOrderCustomerUserNOT_AUTH() throws Exception {
        DrinkOrder drinkOrder = stPeteCustomer.getDrinkOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + drinkOrder.getId() + "/pickup"))
                .andExpect(status().isForbidden());
    }

    private DrinkOrderDto buildOrderDto(Customer customer, UUID drinkId) {
        List<DrinkOrderLineDto> orderLines = Arrays.asList(DrinkOrderLineDto.builder()
                .id(UUID.randomUUID())
                .drinkId(drinkId)
                .orderQuantity(5)
                .build());

        return DrinkOrderDto.builder()
                .customerId(customer.getId())
                .customerRef("123")
                .orderStatusCallbackUrl("http://example.com")
                .drinkOrderLines(orderLines)
                .build();
    }
}