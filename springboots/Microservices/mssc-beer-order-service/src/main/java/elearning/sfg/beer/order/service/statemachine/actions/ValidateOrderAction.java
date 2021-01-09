package elearning.sfg.beer.order.service.statemachine.actions;

import elearning.sfg.beer.brewery.events.ValidateOrderRequested;
import elearning.sfg.beer.order.service.config.JmsConfig;
import elearning.sfg.beer.order.service.domain.BeerOrder;
import elearning.sfg.beer.order.service.domain.BeerOrderEventEnum;
import elearning.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import elearning.sfg.beer.order.service.repositories.BeerOrderRepository;
import elearning.sfg.beer.order.service.statemachine.BeerOrderStateMachineConfig;
import elearning.sfg.beer.order.service.web.mappers.BeerOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {
    private final JmsTemplate jmsTemplate;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> context) {
        UUID beerOrderId = (UUID) context.getMessage()
                .getHeaders().get(BeerOrderStateMachineConfig.BEER_ORDER_ID_HEADER);

        Optional<BeerOrder> beerOrder = beerOrderRepository.findById(beerOrderId);

        beerOrder.ifPresentOrElse(order -> {
                    ValidateOrderRequested validateOrderRequested = ValidateOrderRequested
                            .builder()
                            .beerOrderDto(beerOrderMapper.beerOrderToDto(order))
                            .build();
                    jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_QUEUE, validateOrderRequested);
                    log.debug("Send validation request to queue for order id: " + beerOrderId);
                },
                () -> log.error("Not found beerOrderId: " + beerOrderId.toString()));


    }
}
