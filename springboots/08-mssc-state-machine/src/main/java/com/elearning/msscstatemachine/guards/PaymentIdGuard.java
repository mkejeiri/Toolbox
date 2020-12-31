package com.elearning.msscstatemachine.guards;

import com.elearning.msscstatemachine.domain.PaymentEvent;
import com.elearning.msscstatemachine.domain.PaymentState;
import com.elearning.msscstatemachine.services.PaymentServiceImpl;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

//this will help us to guard the preAuthAction & authAction action against
//a nullable context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER)
@Component
public class PaymentIdGuard implements Guard<PaymentState, PaymentEvent> {
    @Override
    public boolean evaluate(StateContext<PaymentState, PaymentEvent> context) {
            return context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) !=null;
    }
}
