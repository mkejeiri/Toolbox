package elearning.sfg.beer.order.service.services.testcomponents;

import elearning.sfg.beer.brewery.events.AllocateOrderRequested;
import elearning.sfg.beer.brewery.events.AllocateOrderResult;
import elearning.sfg.beer.order.service.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderAllocationListener
{
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listenDouble(Message message) {
        AllocateOrderRequested requested = (AllocateOrderRequested) message.getPayload();

        boolean isPendingInventory = "partial-allocation".equals(requested.getBeerOrderDto().getCustomerRef());
        boolean isAllocationError = "fail-allocation".equals(requested.getBeerOrderDto().getCustomerRef());

        //do full allocation on the order
        requested.getBeerOrderDto().getBeerOrderLines().forEach(line -> {
            if (isPendingInventory) {
                line.setQuantityAllocated(line.getOrderQuantity() - 1);
            } else {
                line.setQuantityAllocated(line.getOrderQuantity());
            }
        });

        AllocateOrderResult allocateOrderResult = AllocateOrderResult.builder()
                .beerOrderDto(requested.getBeerOrderDto())
                .isPendingInventory(isPendingInventory)
                .isAllocationError(isAllocationError)
                .build();

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                allocateOrderResult);

    }
}
