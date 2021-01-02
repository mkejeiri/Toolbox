package elearning.sfg.beer.order.service.services;

import elearning.sfg.beer.order.service.domain.BeerOrder;
import elearning.sfg.beer.order.service.domain.BeerOrderEventEnum;
import elearning.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import elearning.sfg.beer.order.service.repositories.BeerOrderRepository;
import elearning.sfg.beer.order.service.statemachine.BeerOrderStateMachineConfig;
import elearning.sfg.beer.order.service.statemachine.Interceptor.BeerOrderStateChangedInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BeerOrderManagerImpl implements BeerOrderManager {

    private final BeerOrderRepository beerOrderRepository;
    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> factory;
    private final BeerOrderStateChangedInterceptor beerOrderStateChangedInterceptor;

    @Transactional
    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {

        //little defensive programming!
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);

        BeerOrder savedBeerOrder = beerOrderRepository.save(beerOrder);

        //sending event to the state machine
        sendBeerOrderEvent(beerOrder,BeerOrderEventEnum.VALIDATION_REQUESTED);

        return savedBeerOrder;
    }


    //send standard Spring message instead of the Beer enum event
    @Transactional
    private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventEnum event) {

        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm=build(beerOrder);
        //standard Spring message infrastructure supported by the state machine.
        Message message = MessageBuilder.withPayload(event)
                //the state machine is beerId aware.
                //i.e. message enriched with data (e.g. beerId)
                .setHeader(BeerOrderStateMachineConfig.BEER_ORDER_ID_HEADER, beerOrder.getId())
                .build();
        sm.sendEvent(message);
    }

    //Build & restore StateMachine from DB.
    @Transactional
    private StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> build(BeerOrder beerOrder) {
    /*
    it will make a request of the stateMachineFactory to return back a state machine for that beerOrderId.
    Spring is will do caching of that if we're already working with it(i.e. give us an available one or build a new one)
    We have to stop it, because what we want to force the initial state.
    */

        //Restore order from DB
        BeerOrder order = beerOrderRepository.getOne(beerOrder.getId());

        //Create a state machine based on the orderId
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachine = factory.getStateMachine(order.getId());

        //stop state machine
        stateMachine.stop();

        //reset state machine default context
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(stateMachineAccessor -> {

                            //added the BeerOrderChangedInterceptor to track state machine changes
                            //and save them into db.
                            stateMachineAccessor.addStateMachineInterceptor(beerOrderStateChangedInterceptor);

                            //reset the context of the newly hydrated state machine.
                            stateMachineAccessor.resetStateMachine(
                                    new DefaultStateMachineContext<>(order.getOrderStatus(),
                                            null, null, null));
                        }
                );
        stateMachine.start();
        return stateMachine;
    }
}
