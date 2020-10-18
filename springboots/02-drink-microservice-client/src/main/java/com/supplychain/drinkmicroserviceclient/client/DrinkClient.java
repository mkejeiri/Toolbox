package com.supplychain.drinkmicroserviceclient.client;

import com.supplychain.drinkmicroserviceclient.client.model.DrinkDto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.UUID;

@Component
//value is a prefix of property baseUrl as defined in the application.properties
@ConfigurationProperties(prefix = "supplychain.drink", ignoreUnknownFields = false /*if the property is not set this will fail*/)
public class DrinkClient {
    //public final String DRINK_PATH = "/api/drink/";
    public String drinkPath ;
    private String baseUrl;

    private final RestTemplate restTemplate;

    public DrinkClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public DrinkDto getDrinkById(UUID drinkId) {
        return restTemplate.getForObject(baseUrl + drinkPath + drinkId.toString(), DrinkDto.class);
    }

    public URI SaveNewDrink(DrinkDto drinkDto) {
        return restTemplate.postForLocation(baseUrl + drinkPath, drinkDto);
    }

    public void updateDrinkById(UUID drinkId, DrinkDto drinkDto) {
       // restTemplate.put(baseUrl + DRINK_PATH, drinkDto, new HashMap<>() {{ put("drinkId",drinkId); }});
        restTemplate.put(baseUrl + drinkPath + drinkId, drinkDto);
    }

    public void deleteDrinkById(UUID drinkId) {
//        restTemplate.delete(baseUrl + DRINK_PATH, new HashMap<>() {{ put("drinkId",drinkId); }});
        restTemplate.delete(baseUrl + drinkPath + drinkId);
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public void setDrinkPath(String drinkPath) {
        this.drinkPath = drinkPath;
    }
}
