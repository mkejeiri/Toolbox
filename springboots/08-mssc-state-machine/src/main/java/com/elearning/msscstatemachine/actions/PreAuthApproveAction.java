package com.elearning.msscstatemachine.actions;

import com.elearning.msscstatemachine.domain.PaymentEvent;
import com.elearning.msscstatemachine.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;


@Component
@Slf4j
//Action run after a message is received by a state machine
//In general it could call a more complex business logic, database, or a web server
public class PreAuthApproveAction implements Action<PaymentState, PaymentEvent> {
    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        log.debug("PreAuthApproveAction has been called!");
    }
}
