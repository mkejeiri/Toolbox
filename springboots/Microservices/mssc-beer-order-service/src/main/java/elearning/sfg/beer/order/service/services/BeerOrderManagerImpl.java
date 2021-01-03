package elearning.sfg.beer.order.service.services;

import elearning.sfg.beer.brewery.dtos.BeerOrderDto;
import elearning.sfg.beer.order.service.domain.BeerOrder;
import elearning.sfg.beer.order.service.domain.BeerOrderEventEnum;
import elearning.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import elearning.sfg.beer.order.service.repositories.BeerOrderRepository;
import elearning.sfg.beer.order.service.statemachine.BeerOrderStateMachineConfig;
import elearning.sfg.beer.order.service.statemachine.interceptors.BeerOrderStateChangedInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
@Service
public class BeerOrderManagerImpl implements BeerOrderManager {

    private final BeerOrderRepository beerOrderRepository;
    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> factory;
    private final BeerOrderStateChangedInterceptor beerOrderStateChangedInterceptor;
    private final EntityManager entityManager;

    @Transactional
    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {

        //little defensive programming!
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);

        BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);

        //sending event to the state machine
        sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_REQUESTED);

        return savedBeerOrder;
    }

    @Transactional
    @Override
    public void processValidation(UUID beerOrderId, boolean isValid) {

        //Racing condition with the interceptor, we need to flush before entering into this transaction.
        //entityManager.flush(); //replaced by awaitForStatus fct!

        log.debug("processValidation beerOrderId : " + beerOrderId + "IsValid: " + isValid);

        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderId);
        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            if (isValid) {
                //1- when we sendBeerOrderEvent, the BeerOrderStateChangedInterceptor will persist beerOrder into the DB
                //and it becomes a stale beerOrder object.
                sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_APPROVED);

                //wait for status change into the DB
                awaitForStatus(beerOrderId, BeerOrderStatusEnum.VALIDATED);


                //2- need to fetch the beerOrder again from the DB.
                //otherwise hibernate will guess that "beerOrder" is a new version!
                //unless we operate on current version of that object from the db.
                //Hibernate could have cached this last saved "beerOrder" order (most likely no performance hit there)
                BeerOrder hydratedBeerOrder = beerOrderRepository.findById(beerOrderId).get();

                sendBeerOrderEvent(hydratedBeerOrder, BeerOrderEventEnum.ALLOCATION_REQUESTED);

            } else {
                sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_FAILED);
            }

        }, () -> log.debug("not found beerOrderId : " + beerOrderId));
    }


    @Override
    public void beerOrderAllocationApproved(BeerOrderDto beerOrderDto) {

        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());
        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_APPROVED);
            updateAllocateQty(beerOrderDto);
        }, () -> log.debug("Not found beerOrderI " + beerOrderDto.getId()));
    }


    @Override
    public void beerOrderAllocationPendingInventory(BeerOrderDto beerOrderDto) {

        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());
        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            //Update BeerOrder State Machine
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_NO_INVENTORY_FOUND);

            awaitForStatus(beerOrder.getId(), BeerOrderStatusEnum.PENDING_INVENTORY);

            updateAllocateQty(beerOrderDto);
        }, () -> log.debug("Not found beerOrderId: " + beerOrderDto.getId()));
    }


    @Override
    public void beerOrderAllocationFailed(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());
        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            //Update BeerOrder State Machine
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_FAILED);
        }, () -> log.debug("Not found beerOrderId: " + beerOrderDto.getId()));
    }

    @Override
    public void beerOrderPickedUp(UUID beerOrderId) {

        beerOrderRepository.findById(beerOrderId).ifPresentOrElse(beerOrder -> {
            //Update BeerOrder State Machine
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.PICKED_UP);
        }, () -> log.debug("Not found beerOrderId: " + beerOrderId));
    }

    @Override
    public void cancelOrder(UUID beerOrderId) {
        beerOrderRepository.findById(beerOrderId).ifPresentOrElse(beerOrder -> {
            //Update BeerOrder State Machine
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ORDER_CANCELLED);
        }, () -> log.debug("Not found beerOrderId: " + beerOrderId));
    }

    private void updateAllocateQty(BeerOrderDto beerOrderDto) {

        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());
        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            beerOrder.getBeerOrderLines().forEach(beerOrderLine -> {
                beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
                    if (beerOrderLine.getId().equals(beerOrderLineDto.getId()))
                        beerOrderLine.setQuantityAllocated(beerOrderLineDto.getQuantityAllocated());
                });
            });
            beerOrderRepository.saveAndFlush(beerOrder);
        }, () -> log.debug("Not found beerOrderId: " + beerOrderDto.getId()));
    }

    //wait for the changes (statusEnum) to be reflected in the DB
    private void awaitForStatus(UUID beerOrderId, BeerOrderStatusEnum statusEnum) {

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicInteger loopCount = new AtomicInteger(0);

        while (!found.get()) {
            if (loopCount.incrementAndGet() > 10) {
                found.set(true);
                log.debug("Loop Retries exceeded");
            }

            beerOrderRepository.findById(beerOrderId).ifPresentOrElse(beerOrder -> {
                if (beerOrder.getOrderStatus().equals(statusEnum)) {
                    found.set(true);
                    log.debug("Order Found");
                } else {
                    log.debug("Order Status Not Equal. Expected: " + statusEnum.name() + " Found: " + beerOrder.getOrderStatus().name());
                }
            }, () -> {
                log.debug("Order Id Not Found");
            });

            if (!found.get()) {
                try {
                    log.debug("Sleeping for retry");
                    Thread.sleep(100);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }



    //send standard Spring message instead of the Beer enum event
    private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventEnum event) {

        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = build(beerOrder);
        //standard Spring message infrastructure supported by the state machine.
        Message message = MessageBuilder.withPayload(event)
                //the state machine is beerId aware.
                //i.e. message enriched with data (e.g. beerId)
                .setHeader(BeerOrderStateMachineConfig.BEER_ORDER_ID_HEADER, beerOrder.getId())
                .build();
        sm.sendEvent(message);
    }

    //Build & restore StateMachine from DB.
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
