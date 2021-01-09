package elearning.sfg.beer.inventory.services;

import elearning.sfg.beer.brewery.events.DeallocateOrderRequested;
import elearning.sfg.beer.inventory.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeallocationListener {

    private final AllocationService allocationService;

    @JmsListener(destination = JmsConfig.DEALLOCATE_ORDER_QUEUE)
    public void listen(DeallocateOrderRequested event) {
        allocationService.deallocateOrder(event.getBeerOrderDto());
    }
}