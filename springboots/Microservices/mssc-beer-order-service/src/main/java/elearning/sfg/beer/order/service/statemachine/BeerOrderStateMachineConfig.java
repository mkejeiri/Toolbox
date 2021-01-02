package elearning.sfg.beer.order.service.statemachine;

import elearning.sfg.beer.order.service.domain.BeerOrderEventEnum;
import elearning.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.util.EnumSet;

@RequiredArgsConstructor
@EnableStateMachineFactory
@Configuration
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

    public static final String BEER_ORDER_ID_HEADER = "beer_order_id";

    //lombok creates required constructors to inject all final's fields (i.e. @RequiredArgsConstructor)
    //spring boot has no ambiguity on which Action<BeerOrderStatusEnum, BeerOrderEventEnum> &
    // Guard<BeerOrderStatusEnum, BeerOrderEventEnum> to inject if we have to deal with multiples interfaces,
    //since it relies on property names of implemented components (validateRequestAction, orderIdMessageHeaderNullGuard)
    //to inject the correct one, otherwise we need to use qualified name as shown (for educational purposes) below.

    //@Qualifier("validateRequestAction")//has been omitted
    //and kept for educational purposes(reason described above)
    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> validateRequestAction;
    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> allocateRequestAction;

    //@Qualifier("orderIdMessageHeaderNullGuard")//has been omitted
    // and kept for educational purposes(reason described above)
    private final Guard<BeerOrderStatusEnum, BeerOrderEventEnum> orderIdMessageHeaderNullGuard;

    @Override
    public void configure(StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> states)
            throws Exception {
        //NEW,VALIDATION_PENDING, VALIDATED, VALIDATION_EXCEPTION, ALLOCATION_PENDING, ALLOCATED, ALLOCATION_EXCEPTION,
        //    PENDING_INVENTORY, PICKED_UP, DELIVERED, DELIVERY_EXCEPTION
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
        //ALLOCATION_APPROVED, ALLOCATION_NO_INVENTORY, ALLOCATION_FAILED, PICKED_UP
        transitions.withExternal()
                .source(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.VALIDATION_PENDING).event(BeerOrderEventEnum.VALIDATION_REQUESTED)
                .action(validateRequestAction)
                .guard(orderIdMessageHeaderNullGuard)

                .and().withExternal()
                .source(BeerOrderStatusEnum.VALIDATION_PENDING).target(BeerOrderStatusEnum.VALIDATION_EXCEPTION).event(BeerOrderEventEnum.VALIDATION_FAILED)

                .and().withExternal()
                .source(BeerOrderStatusEnum.VALIDATION_PENDING).target(BeerOrderStatusEnum.VALIDATED).event(BeerOrderEventEnum.VALIDATION_APPROVED)
                //.action(preAuthDeclineAction)

                .and().withExternal()
                .source(BeerOrderStatusEnum.VALIDATED).target(BeerOrderStatusEnum.ALLOCATION_PENDING).event(BeerOrderEventEnum.ALLOCATION_REQUESTED)
                .action(allocateRequestAction)
                .guard(orderIdMessageHeaderNullGuard)

                .and().withExternal()
                .source(BeerOrderStatusEnum.ALLOCATION_PENDING).target(BeerOrderStatusEnum.ALLOCATED).event(BeerOrderEventEnum.ALLOCATION_APPROVED)
                //.action(authAction)

                .and().withExternal()
                .source(BeerOrderStatusEnum.ALLOCATION_PENDING).target(BeerOrderStatusEnum.ALLOCATION_EXCEPTION).event(BeerOrderEventEnum.ALLOCATION_FAILED)
                //.action(authDeclineAction)

                .and().withExternal()
                .source(BeerOrderStatusEnum.ALLOCATION_PENDING).target(BeerOrderStatusEnum.PENDING_INVENTORY).event(BeerOrderEventEnum.ALLOCATION_NO_INVENTORY_FOUND)

                .and().withExternal()
                .source(BeerOrderStatusEnum.ALLOCATED).target(BeerOrderStatusEnum.PICKED_UP).event(BeerOrderEventEnum.PICKED_UP)

                .and().withExternal()
                .source(BeerOrderStatusEnum.PICKED_UP).target(BeerOrderStatusEnum.DELIVERY_EXCEPTION).event(BeerOrderEventEnum.DELIVERY_FAILED)

                .and().withExternal()
                .source(BeerOrderStatusEnum.PICKED_UP).target(BeerOrderStatusEnum.DELIVERED).event(BeerOrderEventEnum.PICKED_UP);


    }

}
