package elearning.sfg.beer.order.service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import elearning.sfg.beer.brewery.dtos.BeerDto;
import elearning.sfg.beer.order.service.domain.BeerOrder;
import elearning.sfg.beer.order.service.domain.BeerOrderLine;
import elearning.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import elearning.sfg.beer.order.service.domain.Customer;
import elearning.sfg.beer.order.service.repositories.BeerOrderRepository;
import elearning.sfg.beer.order.service.repositories.CustomerRepository;
import elearning.sfg.beer.order.service.services.beer.BeerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


//Bring the context
//Since we have Hibernate in the ClassPath the H2 DB will be automatically wired in.
@SpringBootTest
@ExtendWith(WireMockExtension.class)
class BeerOrderManagerImplIT {

    @Autowired
    BeerOrderManager beerOrderManager;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    ObjectMapper objectMapper;

    Customer testCustomer;
    UUID beerId = UUID.randomUUID();

    public static final int PORT = 8083;
    public final String UPC = "0631234300019";

    @TestConfiguration
    static class RestTemplateBuilderProvider {
        //config a wiremock server
        @Bean(destroyMethod = "stop")
        public WireMockServer wireMockServer() {
            WireMockServer server = with(wireMockConfig().port(PORT));
            server.start();
            return server;
        }
    }


    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .customerName("Test Customer")
                .build();
    }


    @Test
    void testNewToAllocated() throws JsonProcessingException, InterruptedException {
        BeerDto beerDto = BeerDto.builder()
                .id(beerId)
                .upc(UPC)
                .build();

        //BeerPagedList beerPagedList = new BeerPagedList(Arrays.asList(beerDto));

        //Create the stub for the post response
        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + UPC)
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();


        //replaced by awaitility
        /*System.out.println("sleep mode");
        Thread.sleep(10000);
        System.out.println("Awakening mode");*/

        await().untilAsserted(() -> {
        BeerOrder order = beerOrderRepository.findById(beerId).get();
            assertEquals(BeerOrderStatusEnum.ALLOCATED, order.getOrderStatus());
        });

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        assertNotNull(savedBeerOrder);
//        assertEquals(BeerOrderStatusEnum.ALLOCATED, savedBeerOrder.getOrderStatus());

    }

    @Test
    void testOtherNewToAllocated() throws JsonProcessingException, InterruptedException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(UPC).build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + UPC)
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        //Asset state machine is ALLOCATED (Happy path)
        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        //Assert OrderQuantity = QuantityAllocated for an order line
        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            BeerOrderLine line = foundOrder.getBeerOrderLines().iterator().next();
            assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
        });

        BeerOrder savedBeerOrder2 = beerOrderRepository.findById(savedBeerOrder.getId()).get();

        assertNotNull(savedBeerOrder2);
        assertEquals(BeerOrderStatusEnum.ALLOCATED, savedBeerOrder2.getOrderStatus());
        savedBeerOrder2.getBeerOrderLines().forEach(line -> {
            assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
        });
    }

    @Test
    void testNewToPickedUp() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(UPC).build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + UPC)
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        beerOrderManager.beerOrderPickedUp(savedBeerOrder.getId());

        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.PICKED_UP, foundOrder.getOrderStatus());
        });

        BeerOrder pickedUpOrder = beerOrderRepository.findById(savedBeerOrder.getId()).get();

        assertEquals(BeerOrderStatusEnum.PICKED_UP, pickedUpOrder.getOrderStatus());
    }

    public BeerOrder createBeerOrder() {

        BeerOrder beerOrder = BeerOrder.builder()
                .customer(testCustomer)
                .build();

        Set<BeerOrderLine> beerOrderLines = new HashSet<>();
        beerOrderLines.add(BeerOrderLine.builder()
                .beerId(beerId)
                .upc(UPC)
                .orderQuantity(1)
                .beerOrder(beerOrder)
                .build());

        beerOrder.setBeerOrderLines(beerOrderLines);
        return beerOrder;
    }
}
