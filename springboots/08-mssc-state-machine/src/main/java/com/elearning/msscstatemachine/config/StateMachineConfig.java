package com.elearning.msscstatemachine.config;

import com.elearning.msscstatemachine.domain.PaymentEvent;
import com.elearning.msscstatemachine.domain.PaymentState;
import com.elearning.msscstatemachine.guards.CheckEnoughCreditGuard;
import com.elearning.msscstatemachine.guards.CheckOrderGuard;
import com.elearning.msscstatemachine.guards.PaymentIdGuard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

//by enabling the state machine factory we instructs
//Spring to scan this component and generate state machine for us.
@EnableStateMachineFactory
@Configuration
@Slf4j
@RequiredArgsConstructor
//We could also extends EnumStateMachineConfigurerAdapter which inherits from StateMachineConfigurerAdapter
//We could also use a Builder pattern for state machine
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    //lombok create required constructor to inject all final's fields (i.e. @RequiredArgsConstructor)
    //spring boot has no ambiguity on which Action<PaymentState, PaymentEvent> to inject,
    //since it relies on property names of implemented components (preAuthAction & authAction) to inject the correct one,
    //otherwise we need to use qualified name as shown below.
    @Qualifier("preAuthAction")//could be omitted and shown for educational purposes(reason described above)
    private final Action<PaymentState, PaymentEvent> preAuthAction;
    @Qualifier("authAction") //could be omitted and shown for educational purposes(reason described above)
    private final Action<PaymentState, PaymentEvent> authAction;
    private final Action<PaymentState, PaymentEvent> preAuthDeclineAction;
    private final Action<PaymentState, PaymentEvent> authDeclineAction;
    private final Action<PaymentState, PaymentEvent> preAuthApproveAction;
    private final Action<PaymentState, PaymentEvent> authApproveAction;
    private final PaymentIdGuard paymentIdGuard;
    private final CheckOrderGuard checkOrderGuard;
    private final CheckEnoughCreditGuard checkEnoughCreditGuard;


    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        //configure all states machine: NEW, PRE_AUTH, PRE_AUTH_ERROR, AUTH, AUTH_ERROR
        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(PaymentState.AUTH)
                .end(PaymentState.AUTH_ERROR)
                .end(PaymentState.PRE_AUTH_ERROR);
    }

    //configure all state machine transitions
    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions.withExternal()
                //doesn't cause a state change
                .source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTH_REQUESTED)
                .action(preAuthAction)
                //.action(preAuthAction()) // method call
                .guard(paymentIdGuard)
                .guard(checkOrderGuard)

                .and().withExternal()
                .source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED)
                .action(preAuthApproveAction)
                .guard(paymentIdGuard)
                //.guard(paymentIdGuard()) //method call
                //.guard(extraGuard())

                .and().withExternal()
                .source(PaymentState.PRE_AUTH).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED)
                .action(preAuthDeclineAction)

                .and().withExternal()
                .source(PaymentState.PRE_AUTH).target(PaymentState.PRE_AUTH).event(PaymentEvent.AUTH_REQUESTED)
                .action(authAction)

                .and().withExternal()
                .source(PaymentState.PRE_AUTH).target(PaymentState.AUTH_ERROR).event(PaymentEvent.AUTH_DECLINED)
                .action(authDeclineAction)

                .and().withExternal()
                .source(PaymentState.PRE_AUTH).target(PaymentState.AUTH).event(PaymentEvent.AUTH_APPROVED)
                .action(authApproveAction)
                .guard(checkEnoughCreditGuard);
    }

    //Springs State Machine supports event listeners,
    //We configure a listener to observe the state machine transitions.
    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        //listener adapter
        StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>() {
            //Observing stateChanged event.
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.debug(String.format("stateChanged: from %s to %s", from, to));
            }
        };
        //listener configuration
        config.withConfiguration().listener(adapter);

    }

    //this will help us to guard the preAuthAction & authAction action against
//    // a nullable context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER)
//    public Guard<PaymentState, PaymentEvent> paymentIdGuard() {
//        return context -> context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
//    }
//
//
//    public Guard<PaymentState, PaymentEvent> extraGuard() {
//        return context -> true;
//    }

    //Simulate 20% pre-authorization declined and 80% approved
    //In general it could call a more complex business logic, database, or a web server
   /* public Action<PaymentState, PaymentEvent> preAuthAction() {
        return context -> {
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
        };
    }*/

    //Simulate 20% authorization declined and 80% approved
    //In general it could call a more complex business logic, database, or a web server
   /* public Action<PaymentState, PaymentEvent> authAction() {
        return context -> {
            log.debug("authAction has been called!");
            //80% chance approved!
            if (new Random().nextInt(10) < 8) {
                log.debug("Approved");
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED)
                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,
                                context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                        .build());

            } else {
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,
                                context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                        .build());
            }
        };
    }*/

}
