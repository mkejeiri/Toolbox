package elearning.wiremock.brewery.web.controllers;

import elearning.wiremock.brewery.domain.Customer;
import elearning.wiremock.brewery.repositories.CustomerRepository;
import elearning.wiremock.brewery.web.model.BeerOrderPagedList;
import org.assertj.core.api.Java6Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BeerOrderControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    CustomerRepository customerRepository;

    Customer customer;

    @BeforeEach
    void setUp() {
        customer = customerRepository.findAll().get(0);
    }

    @Test
    void testListOrders() {
        String url = "/api/v1/customers/" + customer.getId().toString() + " /orders";

        BeerOrderPagedList pagedList = testRestTemplate.getForObject(url, BeerOrderPagedList.class);

        Java6Assertions.assertThat(pagedList.getContent()).hasSize(1);
    }
}
