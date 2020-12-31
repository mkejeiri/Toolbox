package elearning.sfg.beer.msscbeerservice.services.brewing;

import elearning.sfg.beer.common.events.BrewBeerEvent;
import elearning.sfg.beer.common.events.NewInventoryEvent;
import elearning.sfg.beer.msscbeerservice.config.JmsConfig;
import elearning.sfg.beer.msscbeerservice.domain.Beer;
import elearning.sfg.beer.msscbeerservice.repositories.BeerRepository;
import elearning.sfg.beer.msscbeerservice.web.model.BeerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BrewBeerListener {
    private final BeerRepository beerRepository;
   // private final JmsTemplate jmsTemplate;

    //@Transactional: Hibernate is doing a lazy initialization of the property beer.getQuantityToBrew()
    //and since we're running outside of transactional scope, there's no Hibernate session to work with.
    @Transactional
    @JmsListener(destination = JmsConfig.BREWING_REQUEST_QUEUE)
    //@Payload : instruct Spring Framework deserialize HelloWorldMessage
    public void listen(@Payload BrewBeerEvent event,
                       //@Headers: instructs Spring Framework to get the message headers which
                       //is equivalent to the JMS message properties and the header properties.
                       @Headers MessageHeaders headers,
                       //here we use javax.jms.Message instead of jms flavor,
                       //Just to prove how spring can abstract away jms implementation
                       Message message) throws JMSException {
        BeerDto beerDto = event.getBeerDto();

        Beer beer = beerRepository.getOne(beerDto.getId());

        beerDto.setQuantityOnHand(beer.getQuantityToBrew());

        NewInventoryEvent newInventoryEvent = new NewInventoryEvent(beerDto);

        log.debug("Brewed beer " + beer.getMinOnHand() + " : QOH: " + beerDto.getQuantityOnHand());

       // jmsTemplate.convertAndSend(JmsConfig.NEW_INVENTORY_QUEUE, newInventoryEvent);
    }
}
