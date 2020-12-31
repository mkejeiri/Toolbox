package com.elearning.msscstatemachine.actions;

import com.elearning.msscstatemachine.domain.PaymentEvent;
import com.elearning.msscstatemachine.domain.PaymentState;
import com.elearning.msscstatemachine.services.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
//Action run after a message is received by a state machine
//In general it could call a more complex business logic, database, or a web server*
//Simulate 20% pre-authorization declined and 80% approved
public class PreAuthAction implements Action<PaymentState, PaymentEvent> {
    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
            log.debug("preAuthAction has been called!");
            //80% chance approved!
            if (new Random().nextInt(10) < 8) {
                log.debug("Approved");
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED)
                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,
                                context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                        .build());

            } else {
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,
                                context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                        .build());
            }
    }
}
