package com.elearning.drink.drinkfactory.bootstrap;

import com.elearning.drink.drinkfactory.domain.*;
import com.elearning.drink.drinkfactory.repositories.*;
import com.elearning.drink.drinkfactory.web.model.DrinkStyleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class DefaultDrinkLoader implements CommandLineRunner {

    public static final String TASTING_ROOM = "Tasting Room";
    public static final String DRINK_1_UPC = "0631234200036";
    public static final String DRINK_2_UPC = "0631234300019";
    public static final String DRINK_3_UPC = "0083783375213";

    private final BreweryRepository breweryRepository;
    private final DrinkRepository drinkRepository;
    private final DrinkInventoryRepository drinkInventoryRepository;
    private final DrinkOrderRepository drinkOrderRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        loadBreweryData();
        loadCustomerData();
    }

    private void loadCustomerData() {
        Customer tastingRoom = Customer.builder()
                .customerName(TASTING_ROOM)
                .apiKey(UUID.randomUUID())
                .build();

        customerRepository.save(tastingRoom);

        drinkRepository.findAll().forEach(drink -> {
            drinkOrderRepository.save(DrinkOrder.builder()
                    .customer(tastingRoom)
                    .orderStatus(OrderStatusEnum.NEW)
                    .drinkOrderLines(Set.of(DrinkOrderLine.builder()
                            .drink(drink)
                            .orderQuantity(2)
                            .build()))
                    .build());
        });
    }

    private void loadBreweryData() {
        if (breweryRepository.count() == 0) {
            breweryRepository.save(Brewery
                    .builder()
                    .breweryName("Cage Brewing")
                    .build());

            Drink mangoBobs = Drink.builder()
                    .drinkName("Mango Bobs")
                    .drinkStyle(DrinkStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(DRINK_1_UPC)
                    .build();

            drinkRepository.save(mangoBobs);
            drinkInventoryRepository.save(DrinkInventory.builder()
                    .drink(mangoBobs)
                    .quantityOnHand(500)
                    .build());

            Drink galaxyCat = Drink.builder()
                    .drinkName("Galaxy Cat")
                    .drinkStyle(DrinkStyleEnum.PALE_ALE)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(DRINK_2_UPC)
                    .build();

            drinkRepository.save(galaxyCat);
            drinkInventoryRepository.save(DrinkInventory.builder()
                    .drink(galaxyCat)
                    .quantityOnHand(500)
                    .build());

            Drink pinball = Drink.builder()
                    .drinkName("Pinball Porter")
                    .drinkStyle(DrinkStyleEnum.PORTER)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(DRINK_3_UPC)
                    .build();

            drinkRepository.save(pinball);
            drinkInventoryRepository.save(DrinkInventory.builder()
                    .drink(pinball)
                    .quantityOnHand(500)
                    .build());

        }
    }
}
