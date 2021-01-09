package elearning.sfg.beer.order.service.statemachine.actions;

import elearning.sfg.beer.brewery.events.OrderAllocationFailed;
import elearning.sfg.beer.order.service.config.JmsConfig;
import elearning.sfg.beer.order.service.domain.BeerOrderEventEnum;
import elearning.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import elearning.sfg.beer.order.service.statemachine.BeerOrderStateMachineConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleOrderAllocationFailedAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {
    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> context) {
        UUID beerOrderId = (UUID) context.getMessage()
                .getHeaders().get(BeerOrderStateMachineConfig.BEER_ORDER_ID_HEADER);

        OrderAllocationFailed orderAllocationFailed = OrderAllocationFailed.builder()
                .orderId(beerOrderId)
                .build();
        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_FAILURE_QUEUE, orderAllocationFailed);
        log.debug("Sent to ALLOCATE_FAILURE_QUEUE, beerOrderId: " + beerOrderId);
    }
}
