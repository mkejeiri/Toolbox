package com.elearning.drink.drinkfactory.bootstrap;

import com.elearning.drink.drinkfactory.domain.*;
import com.elearning.drink.drinkfactory.repositories.*;
import com.elearning.drink.drinkfactory.repositories.security.AuthorityRepository;
import com.elearning.drink.drinkfactory.repositories.security.RoleRepository;
import com.elearning.drink.drinkfactory.repositories.security.UserRepository;
import com.elearning.drink.drinkfactory.web.model.DrinkStyleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultDrinkLoader implements CommandLineRunner {

    public static final String TASTING_ROOM = "Tasting Room";
    public static final String ST_PETE_DISTRIBUTING = "St Pete Distributing";
    public static final String DUNEDIN_DISTRIBUTING = "Dunedin Distributing";
    public static final String KEY_WEST_DISTRIBUTORS = "Key West Distributors";

    public static final String DRINK_1_UPC = "0631234200036";
    public static final String DRINK_2_UPC = "0631234300019";
    public static final String DRINK_3_UPC = "0083783375213";

    private final BreweryRepository breweryRepository;
    private final DrinkRepository drinkRepository;
    private final DrinkInventoryRepository drinkInventoryRepository;
    private final DrinkOrderRepository drinkOrderRepository;
    private final CustomerRepository customerRepository;
    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        loadSecurityData();
        loadBreweryData();
        loadTastingRoomData();
        loadCustomerData();
    }
    private void loadTastingRoomData() {
        Customer tastingRoom = Customer.builder()
                .customerName(TASTING_ROOM)
                .apiKey(UUID.randomUUID())
                .build();

        customerRepository.save(tastingRoom);

        drinkRepository.findAll().forEach(drink -> drinkOrderRepository.save(DrinkOrder.builder()
                .customer(tastingRoom)
                .orderStatus(OrderStatusEnum.NEW)
                .drinkOrderLines(Set.of(DrinkOrderLine.builder()
                        .drink(drink)
                        .orderQuantity(2)
                        .build()))
                .build()));
    }


    private void loadSecurityData() {
        //drink auths
        Authority createDrink = authorityRepository.save(Authority.builder().permission("drink.create").build());
        Authority updateDrink = authorityRepository.save(Authority.builder().permission("drink.update").build());
        Authority readDrink = authorityRepository.save(Authority.builder().permission("drink.read").build());
        Authority deleteDrink = authorityRepository.save(Authority.builder().permission("drink.delete").build());

        //customer auths
        Authority createCustomer = authorityRepository.save(Authority.builder().permission("customer.create").build());
        Authority readCustomer = authorityRepository.save(Authority.builder().permission("customer.read").build());
        Authority updateCustomer = authorityRepository.save(Authority.builder().permission("customer.update").build());
        Authority deleteCustomer = authorityRepository.save(Authority.builder().permission("customer.delete").build());

        //customer brewery
        Authority createBrewery = authorityRepository.save(Authority.builder().permission("brewery.create").build());
        Authority readBrewery = authorityRepository.save(Authority.builder().permission("brewery.read").build());
        Authority updateBrewery = authorityRepository.save(Authority.builder().permission("brewery.update").build());
        Authority deleteBrewery = authorityRepository.save(Authority.builder().permission("brewery.delete").build());


        //drink order
        Authority createOrder = authorityRepository.save(Authority.builder().permission("order.create").build());
        Authority readOrder = authorityRepository.save(Authority.builder().permission("order.read").build());
        Authority updateOrder = authorityRepository.save(Authority.builder().permission("order.update").build());
        Authority deleteOrder = authorityRepository.save(Authority.builder().permission("order.delete").build());
        Authority createOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.create").build());
        Authority readOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.read").build());
        Authority updateOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.update").build());
        Authority deleteOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.delete").build());

        Role adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
        Role customerRole = roleRepository.save(Role.builder().name("CUSTOMER").build());
        Role userRole = roleRepository.save(Role.builder().name("USER").build());

        adminRole.setAuthorities(new HashSet<>(Set.of(createDrink, updateDrink, readDrink, deleteDrink, createCustomer, readCustomer,
                updateCustomer, deleteCustomer, createBrewery, readBrewery, updateBrewery, deleteBrewery,
                createOrder, readOrder, updateOrder, deleteOrder)));

        customerRole.setAuthorities(new HashSet<>(Set.of(readDrink, readCustomer, readBrewery, createOrderCustomer, readOrderCustomer,
                updateOrderCustomer, deleteOrderCustomer)));

        userRole.setAuthorities(new HashSet<>(Set.of(readDrink)));

        roleRepository.saveAll(Arrays.asList(adminRole, customerRole, userRole));

        userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .role(adminRole)
                .build());

        userRepository.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .role(userRole)
                .build());

        userRepository.save(User.builder()
                .username("customer")
                .password(passwordEncoder.encode("password"))
                .role(customerRole)
                .build());

        log.debug("Users Loaded: " + userRepository.count());

    }
    private void loadCustomerData() {
        Role customerRole = roleRepository.findByName("CUSTOMER").orElseThrow();

        //create customers
        Customer stPeteCustomer = customerRepository.save(Customer.builder()
                .customerName(ST_PETE_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());

        Customer dunedinCustomer = customerRepository.save(Customer.builder()
                .customerName(DUNEDIN_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());

        Customer keyWestCustomer = customerRepository.save(Customer.builder()
                .customerName(KEY_WEST_DISTRIBUTORS)
                .apiKey(UUID.randomUUID())
                .build());

        //create users
        User stPeteUser = userRepository.save(User.builder().username("stpete")
                .password(passwordEncoder.encode("password"))
                .customer(stPeteCustomer)
                .role(customerRole).build());

        User dunedinUser = userRepository.save(User.builder().username("dunedin")
                .password(passwordEncoder.encode("password"))
                .customer(dunedinCustomer)
                .role(customerRole).build());

        User keywest = userRepository.save(User.builder().username("keywest")
                .password(passwordEncoder.encode("password"))
                .customer(keyWestCustomer)
                .role(customerRole).build());

        //create orders
        createOrder(stPeteCustomer);
        createOrder(dunedinCustomer);
        createOrder(keyWestCustomer);

        log.debug("Orders Loaded: " + drinkOrderRepository.count());
    }

    private void createOrder(Customer customer) {
        drinkOrderRepository.save(DrinkOrder.builder()
                .customer(customer)
                .orderStatus(OrderStatusEnum.NEW)
                .drinkOrderLines(Set.of(DrinkOrderLine.builder()
                        .drink(drinkRepository.findByUpc(DRINK_1_UPC))
                        .orderQuantity(2)
                        .build()))
                .build());
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
