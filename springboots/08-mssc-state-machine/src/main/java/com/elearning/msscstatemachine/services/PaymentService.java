package com.elearning.msscstatemachine.services;

import com.elearning.msscstatemachine.domain.Payment;
import com.elearning.msscstatemachine.domain.PaymentEvent;
import com.elearning.msscstatemachine.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {
    Payment newPayment(Payment payment);

    StateMachine<PaymentState, PaymentEvent> preAuthorizePayment(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> approvePreAuthorizePayment(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> declinePreAuthorizePayment(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> authorisePayment(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> approveAuthorisePayment(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> declineAuthorizePayment(Long paymentId);

}
