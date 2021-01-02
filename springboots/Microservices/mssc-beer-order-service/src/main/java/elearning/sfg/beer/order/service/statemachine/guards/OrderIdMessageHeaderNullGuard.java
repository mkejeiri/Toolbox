package elearning.sfg.beer.order.service.statemachine.guards;

import elearning.sfg.beer.order.service.domain.BeerOrderEventEnum;
import elearning.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import elearning.sfg.beer.order.service.statemachine.BeerOrderStateMachineConfig;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class OrderIdMessageHeaderNullGuard implements Guard<BeerOrderStatusEnum, BeerOrderEventEnum> {
    @Override
    public boolean evaluate(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> context) {
        return context.getMessage()
                .getHeaders().get(BeerOrderStateMachineConfig.BEER_ORDER_ID_HEADER) != null;
    }
}
