package com.supplychain.mssdrink.boostrap;
import com.supplychain.mssdrink.repositories.DrinkRepository;
import com.supplychain.mssdrink.web.model.DrinkDto;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component //registered as component and get picked up at startup cause it implements  ApplicationListener<ContextRefreshedEvent>
// we could also use CommandLineRunner : public class DrinkDataLoader implements CommandLineRunner {
public class DrinkDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final DrinkRepository drinkRepository;

    public DrinkDataLoader(DrinkRepository drinkRepository) {
        this.drinkRepository = drinkRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (drinkRepository.count() == 0) drinkRepository.saveAll(getDrinks());
        System.out.println("loaded drinks : " + drinkRepository.count());
    }

    private List<DrinkDto> getDrinks() {
        return new ArrayList<DrinkDto>() {{
            add(DrinkDto.builder()
                    .id(null)
                    .drinkName("LAGER")
                    .drinkStyle("LAGER")
                    .quantityOnHand(2)
                    //.quantityToBrew(4)
                    .price(new BigDecimal("12.04"))
                    .version(123456789L)
                    .upc(123456789L)
                    .build());
            add(DrinkDto.builder()
                    .id(null)
                    .drinkName("PILSNER")
                    .drinkStyle("PILSNER")
                    .quantityOnHand(3)
                    //.quantityToBrew(1)
                    .price(new BigDecimal("16.02"))
                    .version(123456788L)
                    .upc(123456788L)
                    .build());
            add(DrinkDto.builder()
                    .id(null)
                    .drinkName("PALE_ALE")
                    .drinkStyle("PALE_ALE")
                    .quantityOnHand(1)
                    //.quantityToBrew(2)
                    .price(new BigDecimal("12.04"))
                    .version(123456787L)
                    .upc(123456787L)
                    .build());
        }};
    }

    //goes with CommandLineRunner
   /* @Override
    public void run(String... args) throws Exception {
        if (drinkRepository.count() == 0) drinkRepository.saveAll(getDrinks());
    }*/
}
