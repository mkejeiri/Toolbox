package com.supplychain.mssdrink.data.boostrap;

import com.supplychain.mssdrink.data.repositories.DrinkRepository;
import com.supplychain.mssdrink.domains.models.Drink;
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
    public static final String DRINK_1_UPC = "0631234200036";
    public static final String DRINK_2_UPC = "0631234300019";
    public static final String DRINK_3_UPC = "0083783375213";

    public DrinkDataLoader(DrinkRepository drinkRepository) {
        this.drinkRepository = drinkRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (drinkRepository.count() == 0) drinkRepository.saveAll(getDrinks());
        System.out.println("loaded drinks : " + drinkRepository.count());
    }

    private List<Drink> getDrinks() {
        return new ArrayList<Drink>() {{
            add(Drink.builder()
                    .id(null)
                    .drinkName("LAGER")
                    .drinkStyle("LAGER")
                    .minOnHand(2)
                    .quantityToBrew(4)
                    .price(new BigDecimal("12.04"))
                    .version(123456789L)
                    .upc(DRINK_1_UPC)
                    .build());
            add(Drink.builder()
                    .id(null)
                    .drinkName("PILSNER")
                    .drinkStyle("PILSNER")
                    .minOnHand(3)
                    .quantityToBrew(1)
                    .price(new BigDecimal("16.02"))
                    .version(123456788L)
                    .upc(DRINK_2_UPC)
                    .build());
            add(Drink.builder()
                    .id(null)
                    .drinkName("PALE_ALE")
                    .drinkStyle("PALE_ALE")
                    .minOnHand(1)
                    .quantityToBrew(2)
                    .price(new BigDecimal("12.04"))
                    .version(123456787L)
                    .upc(DRINK_3_UPC)
                    .build());
        }};
    }

    //goes with CommandLineRunner
   /* @Override
    public void run(String... args) throws Exception {
        if (drinkRepository.count() == 0) drinkRepository.saveAll(getDrinks());
    }*/
}
