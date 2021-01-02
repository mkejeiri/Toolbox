package elearning.sfg.beer.msscbeerservice.services.order;

import elearning.sfg.beer.brewery.events.ValidateOrderRequested;
import elearning.sfg.beer.brewery.events.ValidateOrderResult;
import elearning.sfg.beer.msscbeerservice.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BeerOrderValidationListener {

    private BeerOrderValidatorImpl orderValidator;
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void listen(ValidateOrderRequested validateOrderRequested) {
        Boolean isValid = orderValidator.isOrderValid(validateOrderRequested.getBeerOrderDto());
        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                ValidateOrderResult.builder()
                        .isValid(isValid)
                        .orderId(validateOrderRequested.getBeerOrderDto().getId())
                        .build()
        );

    }
}
