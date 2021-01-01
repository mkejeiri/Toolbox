package elearning.sfg.beer.msscbeerservice.services.brewing;

import elearning.sfg.beer.common.events.BrewBeerEvent;
import elearning.sfg.beer.msscbeerservice.config.JmsConfig;
import elearning.sfg.beer.msscbeerservice.domain.Beer;
import elearning.sfg.beer.msscbeerservice.repositories.BeerRepository;
import elearning.sfg.beer.msscbeerservice.services.inventory.BeerInventoryService;
import elearning.sfg.beer.msscbeerservice.web.mappers.BeerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class BrewingService {
    private final BeerRepository beerRepository;
    private final BeerInventoryService beerInventoryService;
    private final JmsTemplate jmsTemplate;
    private final BeerMapper beerMapper;

    @Scheduled(fixedRate = 5000) //every 5 seconds
    public void checkForLowInventory(){
        List<Beer> beers = beerRepository.findAll();

        beers.forEach(beer -> {
            Integer onhandInventory = beerInventoryService.getOnhandInventory(beer.getId());

            log.debug("Min on hand threshold for that beer is: " + beer.getMinOnHand());
            log.debug("on hand Inventory is: "  + onhandInventory);

            if(beer.getMinOnHand() >= onhandInventory){
              jmsTemplate.convertAndSend(JmsConfig.BREWING_REQUEST_QUEUE, new BrewBeerEvent(beerMapper.beerToBeerDto(beer)));
            }
        });

    }
}