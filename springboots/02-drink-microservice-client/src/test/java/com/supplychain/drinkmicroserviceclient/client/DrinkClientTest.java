package com.supplychain.drinkmicroserviceclient.client;

import com.supplychain.drinkmicroserviceclient.client.model.DrinkDto;
import com.supplychain.drinkmicroserviceclient.client.model.DrinkStyleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class DrinkClientTest {
    @Autowired
    DrinkClient drinkClient;

    DrinkDto drinkDto;

    @BeforeEach
    void setUp() {
        //given
        drinkDto = DrinkDto.builder().id(UUID.randomUUID())
                .createdAt(OffsetDateTime.now())
                .drinkName("coca")
                .drinkStyle(DrinkStyleEnum.COCACOLA)
                .modifiedAt(OffsetDateTime.now())
                .quantityOnHand(1)
                .price(new BigDecimal("12.04"))
                .version(1)
                .upc(123456789L)
                .build();
    }

    @Test
    void getDrinkById()  {
        DrinkDto getDrinkDto = drinkClient.getDrinkById(drinkDto.getId());
        assertNotNull(getDrinkDto);
    }

    @Test
    void saveNewDrinkById() {
        URI uri = drinkClient.SaveNewDrink(drinkDto);
        assertNotNull(uri);
    }

    @Test
    void updateDrinkById() {
        drinkClient.updateDrinkById(drinkDto.getId(), drinkDto);
        System.out.println(drinkDto);
    }

    @Test
    void deleteDrinkById() {
        drinkClient.deleteDrinkById(drinkDto.getId());
        System.out.println(drinkDto.getId());
    }
}