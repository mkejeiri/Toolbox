package com.supplychain.mssdrink.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("snake")
@JsonTest
public class DrinkDtoSnakeTest extends DrinkDtoBase {
    @Test
    void testSerializeDto() throws JsonProcessingException {
        DrinkDto drinkDto = getDrinkDto();
        String jsonString = objectMapper.writeValueAsString(drinkDto);
        System.out.println(jsonString);
    }

    @Test
    void testDeserialize() throws JsonProcessingException {
        String json = "{\"version\":1,\"created_at\":\"2020-12-26T17:29:31+0100\",\"modified_at\":\"2020-12-26T17:29:31.6641126+01:00\",\"drink_name\":\"DrinkName\",\"drink_style\":\"Ale\",\"upc\":123123123123,\"price\":\"12.99\",\"quantity_on_hand\":null,\"my_local_date\":\"20201226\",\"drinkId\":\"78695364-b6b5-41ce-bdc6-251f2940cda7\"}\n";
        DrinkDto drinkDto = objectMapper.readValue(json, DrinkDto.class);
        System.out.println(drinkDto);
    }
}