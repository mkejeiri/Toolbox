package elearning.sfg.beer.order.service.services.testcomponents;

import elearning.sfg.beer.brewery.events.ValidateOrderRequested;
import elearning.sfg.beer.brewery.events.ValidateOrderResult;
import elearning.sfg.beer.order.service.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

//Play the role of a double of real listener
@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderValidationListener {
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public  void listenDouble(Message message) {

        ValidateOrderRequested requested = (ValidateOrderRequested) message.getPayload();
        ValidateOrderResult validateOrderResult = ValidateOrderResult
                .builder()
                .isValid(true)
                .orderId(requested.getBeerOrderDto().getId())
                .build();

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                validateOrderResult);
    }
}
