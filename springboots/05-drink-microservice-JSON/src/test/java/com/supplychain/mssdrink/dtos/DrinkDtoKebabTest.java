package com.supplychain.mssdrink.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("kebab")
@JsonTest
public class DrinkDtoKebabTest extends DrinkDtoBase {
    @Test
    void testSerializeDto() throws JsonProcessingException {
        DrinkDto drinkDto = getDrinkDto();
        String jsonString = objectMapper.writeValueAsString(drinkDto);
        System.out.println(jsonString);
    }

    @Test
    void testDeserialize() throws JsonProcessingException {
        String json = "{\"version\":1,\"created-at\":\"2020-12-26T17:29:31+0100\",\"modified-at\":\"2020-12-26T17:29:31.9151103+01:00\",\"drink-name\":\"DrinkName\",\"drink-style\":\"Ale\",\"upc\":123123123123,\"price\":\"12.99\",\"quantity-on-hand\":null,\"my-local-date\":\"20201226\",\"drinkId\":\"899bbafb-5813-4ac0-aade-b41e8a311366\"}";
        DrinkDto drinkDto = objectMapper.readValue(json, DrinkDto.class);
        System.out.println(drinkDto);
    }
}