package com.elearning.msscstatemachine.services;

import com.elearning.msscstatemachine.domain.Payment;
import com.elearning.msscstatemachine.domain.PaymentEvent;
import com.elearning.msscstatemachine.domain.PaymentState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;


@RequiredArgsConstructor
@Service
@Slf4j
//This service will trigger runStateMachine on fixedRate milliseconds
public class ScheduledService {
    private final StateMachineFactory<PaymentState, PaymentEvent> factory;
    private final PaymentService paymentService;

    @Scheduled(fixedRate = 100) //every 5 seconds
    @Transactional
    public void triggerMachineStateChanges() {
        Payment payment = Payment.builder()
                .amount(new BigDecimal(12.56))
                .state(PaymentState.NEW)
                .build();
        paymentService.newPayment(payment);
        paymentService.preAuthorizePayment(payment.getId());


        //this logic has been moved to action
        //20% chance declineAuthorizePayment
        if (new Random().nextInt(10) < 2)
        paymentService.declinePreAuthorizePayment(payment.getId());

        //change state only if previous
        paymentService.approvePreAuthorizePayment(payment.getId());

        //this logic has been moved to action
        paymentService.authorisePayment(payment.getId());
        //30% chance declineAuthorizePayment
        if (new Random().nextInt(10) < 3)
        paymentService.declineAuthorizePayment(payment.getId());
        paymentService.authorisePayment(payment.getId());
    }

    //@Scheduled(fixedRate = 2000) //every 2 seconds
    /*public void runStateMachine() {

        ///PRE_AUTH_REQUESTED, PRE_AUTH_APPROVED, PRE_AUTH_DECLINED, AUTH_REQUESTED, AUTH_APPROVED, AUTH_DECLINED
        StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(UUID.randomUUID());
        stateMachine.start();
        log.debug("started");
        log.debug(stateMachine.getState().toString());

        log.debug("PRE_AUTH_REQUESTED");
        stateMachine.sendEvent(PaymentEvent.PRE_AUTH_REQUESTED);
        log.debug(stateMachine.getState().toString());

        log.debug("PRE_AUTH_APPROVED");
        stateMachine.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);
        log.debug(stateMachine.getState().toString());

        log.debug("AUTH_REQUESTED");
        stateMachine.sendEvent(PaymentEvent.AUTH_REQUESTED);
        log.debug(stateMachine.getState().toString());

        log.debug("AUTH_DECLINED");
        stateMachine.sendEvent(PaymentEvent.AUTH_DECLINED);
        log.debug(stateMachine.getState().toString());

        //NOTHING will happen: CAN'T transition from ERROR TO AUTH
        log.debug("AUTH_APPROVED");
        stateMachine.sendEvent(PaymentEvent.AUTH_APPROVED);
        log.debug(stateMachine.getState().toString());
    }*/
}
