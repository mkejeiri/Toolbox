package com.elearning.msscstatemachine.services;

import com.elearning.msscstatemachine.domain.Payment;
import com.elearning.msscstatemachine.domain.PaymentEvent;
import com.elearning.msscstatemachine.domain.PaymentState;
import com.elearning.msscstatemachine.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    public static final String PAYMENT_ID_HEADER = "payment_id";
    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> factory;
    private final PaymentStateChangedInterceptor paymentStateChangedInterceptor;

    @Transactional
    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuthorizePayment(Long paymentId) {
        //Rehydrate StateMachine from DB.
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEvent(paymentId, stateMachine, PaymentEvent.PRE_AUTH_REQUESTED);
        return stateMachine;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> approvePreAuthorizePayment(Long paymentId) {
        //Rehydrate StateMachine from DB.
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEvent(paymentId, stateMachine, PaymentEvent.PRE_AUTH_APPROVED);
        return stateMachine;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> declinePreAuthorizePayment(Long paymentId) {
        //Rehydrate StateMachine from DB.
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEvent(paymentId, stateMachine, PaymentEvent.PRE_AUTH_DECLINED);
        return stateMachine;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> authorisePayment(Long paymentId) {
        //Rehydrate StateMachine from DB.
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEvent(paymentId, stateMachine, PaymentEvent.AUTH_REQUESTED);
        return stateMachine;
    }


    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> approveAuthorisePayment(Long paymentId) {
        //Rehydrate StateMachine from DB.
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEvent(paymentId, stateMachine, PaymentEvent.AUTH_APPROVED);
        return stateMachine;
    }


    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuthorizePayment(Long paymentId) {
        //Rehydrate StateMachine from DB.
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEvent(paymentId, stateMachine, PaymentEvent.AUTH_DECLINED);
        return stateMachine;
    }


    //Build & restore payment StateMachine from DB.
    @Transactional
    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {

        //Restore payment from DB
        Payment payment = paymentRepository.getOne(paymentId);

        //Create a state machine based on paymentId
        StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(Long.toString(payment.getId()));

        //stop state machine
        stateMachine.stop();

        //reset state machine default context
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(stateMachineAccessor -> {

                            //added the paymentStateChangedInterceptor to track state machine changes
                            //and save them into db.
                            stateMachineAccessor.addStateMachineInterceptor(paymentStateChangedInterceptor);

                            //reset the context of the newly hydrated state machine.
                            stateMachineAccessor.resetStateMachine(
                                    new DefaultStateMachineContext<>(payment.getState(),
                                            null, null, null));
                        }
                );
        stateMachine.start();
        return stateMachine;
    }

    //send standard Spring message instead of the PaymentEvent
    @Transactional
    private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event) {

        //standard Spring message infrastructure supported by the state machine.
        Message message = MessageBuilder.withPayload(event)
                //the state machine is aware of the payment ID.
                //i.e. message enriched with data (e.g. financial payment ID)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();
        sm.sendEvent(message);


    }
}
