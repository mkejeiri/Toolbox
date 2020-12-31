package com.elearning.msscstatemachine.guards;

import com.elearning.msscstatemachine.domain.PaymentEvent;
import com.elearning.msscstatemachine.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

//this will help us to run some check before running the attached actions
@Component
@Slf4j
public class CheckOrderGuard implements Guard<PaymentState, PaymentEvent> {
    @Override
    public boolean evaluate(StateContext<PaymentState, PaymentEvent> context) {
      log.debug("checking customer order through order API");
      //some Api call or DB check!
      boolean  checked = true;
            return checked;
    }
}
