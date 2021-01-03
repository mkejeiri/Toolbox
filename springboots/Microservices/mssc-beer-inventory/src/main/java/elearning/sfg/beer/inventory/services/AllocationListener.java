package elearning.sfg.beer.inventory.services;

import elearning.sfg.beer.brewery.events.AllocateOrderRequested;
import elearning.sfg.beer.brewery.events.AllocateOrderResult;
import elearning.sfg.beer.inventory.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocationListener {
    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateOrderRequested allocateOrderRequested) {

        AllocateOrderResult.AllocateOrderResultBuilder builder =
                AllocateOrderResult.builder();
        builder.beerOrderDto(allocateOrderRequested.getBeerOrderDto());

        try {
            Boolean allocationResult = allocationService.allocateOrder(allocateOrderRequested.getBeerOrderDto());
            builder.isPendingInventory(!allocationResult);
            builder.isAllocationError(false);

        } catch (Exception e) {
            builder.isAllocationError(true);
            log.error("Allocation failed for OrderId: " + allocateOrderRequested.getBeerOrderDto().getId());
        }

        AllocateOrderResult allocateOrderResult = builder.build();

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, allocateOrderResult);
    }
}
