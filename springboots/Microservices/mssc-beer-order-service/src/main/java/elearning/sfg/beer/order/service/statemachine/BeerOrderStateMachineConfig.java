package elearning.sfg.beer.order.service.statemachine;

import elearning.sfg.beer.order.service.domain.BeerOrderEventEnum;
import elearning.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

    @Override
    public void configure(StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> states)
            throws Exception {
        //NEW, VALIDATED, VALIDATION_EXCEPTION, ALLOCATED, ALLOCATION_EXCEPTION,
        //PENDING_INVENTORY, PICKED_UP, DELIVERED, DELIVERY_EXCEPTION
        states.withStates()
                .initial(BeerOrderStatusEnum.NEW)
                .states(EnumSet.allOf(BeerOrderStatusEnum.class))
                .end(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
                .end(BeerOrderStatusEnum.ALLOCATION_EXCEPTION)
                .end(BeerOrderStatusEnum.DELIVERED)
                .end(BeerOrderStatusEnum.PICKED_UP)
                .end(BeerOrderStatusEnum.DELIVERY_EXCEPTION);
    }


    @Override
    public void configure(StateMachineTransitionConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> transitions) throws Exception {
        //VALIDATION_REQUESTED, VALIDATION_APPROVED, VALIDATION_FAILED,
        //    ALLOCATION_APPROVED, ALLOCATION_NO_INVENTORY, ALLOCATION_FAILED, PICKED_UP
        transitions.withExternal()
                //doesn't cause a state change
                .source(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.NEW).event(BeerOrderEventEnum.VALIDATION_REQUESTED)
                //todo: add validationAction
                //.action(validationAction)
                //.guard(checkOrderGuard)

                .and().withExternal()
                .source(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.VALIDATION_EXCEPTION).event(BeerOrderEventEnum.VALIDATION_FAILED)

                .and().withExternal()
                .source(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.VALIDATED).event(BeerOrderEventEnum.VALIDATION_APPROVED)
                //.action(preAuthDeclineAction)

                .and().withExternal()
                .source(BeerOrderStatusEnum.VALIDATED).target(BeerOrderStatusEnum.ALLOCATED).event(BeerOrderEventEnum.ALLOCATION_APPROVED)
                //.action(authAction)

                .and().withExternal()
                .source(BeerOrderStatusEnum.VALIDATED).target(BeerOrderStatusEnum.ALLOCATION_EXCEPTION).event(BeerOrderEventEnum.ALLOCATION_FAILED)
                //.action(authDeclineAction)

                .and().withExternal() //TODO: Check this transition
                .source(BeerOrderStatusEnum.VALIDATED).target(BeerOrderStatusEnum.PENDING_INVENTORY).event(BeerOrderEventEnum.ALLOCATION_NO_INVENTORY_FOUND)

                .and().withExternal()
                .source(BeerOrderStatusEnum.ALLOCATED).target(BeerOrderStatusEnum.PICKED_UP).event(BeerOrderEventEnum.PICKED_UP)

                .and().withExternal()
                .source(BeerOrderStatusEnum.PICKED_UP).target(BeerOrderStatusEnum.DELIVERY_EXCEPTION).event(BeerOrderEventEnum.DELIVERY_FAILED)

                .and().withExternal()
                .source(BeerOrderStatusEnum.PICKED_UP).target(BeerOrderStatusEnum.DELIVERED).event(BeerOrderEventEnum.PICKED_UP);


    }

}
