package com.supplychain.mssdrink.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

@JsonTest
public class DrinkDtoTest extends DrinkDtoBase {

    @Test
    void testSerializeDto() throws JsonProcessingException {
        DrinkDto drinkDto = getDrinkDto();
        String jsonString = objectMapper.writeValueAsString(drinkDto);
        System.out.println(jsonString);
    }

    @Test
    void testDeserialize() throws JsonProcessingException {
        String json = "{\"version\":1,\"createdAt\":\"2020-12-26T17:28:47+0100\",\"modifiedAt\":\"2020-12-26T17:28:47.1369784+01:00\",\"drinkName\":\"DrinkName\",\"drinkStyle\":\"Ale\",\"upc\":123123123123,\"price\":\"12.99\",\"quantityOnHand\":null,\"myLocalDate\":\"20201226\",\"drinkId\":\"b85e1e2e-56d1-4088-a406-8014c5c2536f\"}\n";
        DrinkDto drinkDto = objectMapper.readValue(json, DrinkDto.class);
        System.out.println(drinkDto);
    }
}