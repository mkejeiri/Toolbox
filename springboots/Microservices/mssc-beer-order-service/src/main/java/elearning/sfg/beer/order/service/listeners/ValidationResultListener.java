package elearning.sfg.beer.order.service.listeners;

import elearning.sfg.beer.brewery.events.ValidateOrderResult;
import elearning.sfg.beer.order.service.config.JmsConfig;
import elearning.sfg.beer.order.service.services.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ValidationResultListener {
    private final JmsTemplate jmsTemplate;
    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(ValidateOrderResult validateOrderResult) {
        final UUID beerOrderId = validateOrderResult.getOrderId();

        log.debug("Validation result for OrderId: " + beerOrderId);

        beerOrderManager.processValidation(beerOrderId, validateOrderResult.isValid());
    }
}
