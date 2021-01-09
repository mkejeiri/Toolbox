package elearning.sfg.beer.order.service.listeners;

import elearning.sfg.beer.brewery.events.AllocateOrderResult;
import elearning.sfg.beer.order.service.config.JmsConfig;
import elearning.sfg.beer.order.service.services.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BeerOrderAllocationResultListener {
    private final JmsTemplate jmsTemplate;
    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResult allocateOrderResult) {
        final UUID beerOrderId = allocateOrderResult.getBeerOrderDto().getId();

        if (!allocateOrderResult.getIsAllocationError() && !allocateOrderResult.getIsPendingInventory()) {
            //Allocated normally
            beerOrderManager.beerOrderAllocationApproved(allocateOrderResult.getBeerOrderDto());
            log.debug("Allocated OrderId: " + beerOrderId);

        } else if (!allocateOrderResult.getIsAllocationError() && allocateOrderResult.getIsPendingInventory()) {
            //pending inventory
            beerOrderManager.beerOrderAllocationPendingInventory(allocateOrderResult.getBeerOrderDto());
            log.debug("pending inventory for OrderId: " + beerOrderId);

        } else if (allocateOrderResult.getIsAllocationError()) {
            //allocation error
            beerOrderManager.beerOrderAllocationFailed(allocateOrderResult.getBeerOrderDto());
            log.debug("allocation failed for OrderId: " + beerOrderId);
        }
    }
}
