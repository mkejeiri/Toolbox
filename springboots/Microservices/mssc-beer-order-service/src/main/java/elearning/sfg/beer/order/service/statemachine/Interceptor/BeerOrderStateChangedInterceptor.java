package elearning.sfg.beer.order.service.statemachine.Interceptor;

import elearning.sfg.beer.order.service.domain.BeerOrder;
import elearning.sfg.beer.order.service.domain.BeerOrderEventEnum;
import elearning.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import elearning.sfg.beer.order.service.repositories.BeerOrderRepository;
import elearning.sfg.beer.order.service.statemachine.BeerOrderStateMachineConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BeerOrderStateChangedInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;

    //Intercept state machine changes and save them into DB.
    //this will be called up on before machine state change.
    //@Transactional
    @Override
    public void preStateChange(State<BeerOrderStatusEnum, BeerOrderEventEnum> state, Message<BeerOrderEventEnum> message,
                               Transition<BeerOrderStatusEnum, BeerOrderEventEnum> transition,
                               StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachine) {

        Optional.ofNullable(message).ifPresent(msg -> {
            Optional.ofNullable(UUID.class.cast(msg.getHeaders()
                    .getOrDefault(BeerOrderStateMachineConfig.BEER_ORDER_ID_HEADER, -1)))
                    .ifPresent(beerOrderId -> {
                        BeerOrder beerOrder = beerOrderRepository.getOne(beerOrderId);
                        beerOrder.setOrderStatus(state.getId());

                        //hibernate save is write lazily to the DB, saveAndFlush force it to write to db right away!
                        beerOrderRepository.saveAndFlush(beerOrder);
                    });
        });
    }
}
