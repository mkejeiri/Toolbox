package com.elearning.msscstatemachine.config;

import com.elearning.msscstatemachine.domain.PaymentEvent;
import com.elearning.msscstatemachine.domain.PaymentState;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    @RepeatedTest(5)
    void testNewMachine() {
        ///PRE_AUTH_REQUESTED, PRE_AUTH_APPROVED, PRE_AUTH_DECLINED, AUTH_REQUESTED, AUTH_APPROVED, AUTH_DECLINED
        StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(UUID.randomUUID());
        stateMachine.start();
        System.out.println("started");
        System.out.println(stateMachine.getState().toString());

        System.out.println("PRE_AUTH_REQUESTED");
        stateMachine.sendEvent(PaymentEvent.PRE_AUTH_REQUESTED);
        System.out.println(stateMachine.getState().toString());

        System.out.println("PRE_AUTH_APPROVED");
        stateMachine.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);
        System.out.println(stateMachine.getState().toString());

        System.out.println("AUTH_REQUESTED");
        stateMachine.sendEvent(PaymentEvent.AUTH_REQUESTED);
        System.out.println(stateMachine.getState().toString());

        System.out.println("AUTH_DECLINED");
        stateMachine.sendEvent(PaymentEvent.AUTH_DECLINED);
        System.out.println(stateMachine.getState().toString());

        //NOTHING will happen: CAN'T transition from ERROR TO AUTH
        System.out.println("AUTH_APPROVED");
        stateMachine.sendEvent(PaymentEvent.AUTH_APPROVED);
        System.out.println(stateMachine.getState().toString());
    }
}