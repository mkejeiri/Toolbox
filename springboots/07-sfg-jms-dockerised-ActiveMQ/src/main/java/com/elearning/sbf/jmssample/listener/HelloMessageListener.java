package com.elearning.sbf.jmssample.listener;

import com.elearning.sbf.jmssample.config.JmsConfig;
import com.elearning.sbf.jmssample.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HelloMessageListener {

    private final JmsTemplate jmsTemplate;

    //@JmsListener: instructs Spring to listen JmsConfig.MY_QUEUE name and
    //when there's a message on that queue, send the message or call upon "listen" method.
    @JmsListener(destination = JmsConfig.MY_QUEUE)
    //@Payload : instruct Spring Framework deserialize HelloWorldMessage
    public void listen(@Payload HelloWorldMessage helloWorldMessage,
                       //@Headers: instructs Spring Framework to get the message headers which
                       //is equivalent to the JMS message properties and the header properties.
                       @Headers MessageHeaders headers,
                       //here we use javax.jms.Message instead of jms flavor,
                       //Just to prove how spring can abstract away jms implementation
                       Message message){

        System.out.println("I Got a Message!!!!!");

        System.out.println(helloWorldMessage);
        // Inside listen everything behave as single transaction, if it fails it will retry (by default 10 times).
        //we can see the retrial in the headers object.
        // uncomment to see retry count in debugger
        //throw new RuntimeException("foo");
    }

    @JmsListener(destination = JmsConfig.MY_SEND_RCV_QUEUE)
    public void listenForHello(@Payload HelloWorldMessage helloWorldMessage,
                               @Headers MessageHeaders headers,
                               Message message) throws JMSException {

        HelloWorldMessage payloadMsg = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("World!!")
                .build();

        //using the native JMS message
        jmsTemplate.convertAndSend(message.getJMSReplyTo(), payloadMsg);
    }

    @JmsListener(destination = JmsConfig.MY_SEND_RCV_QUEUE)
    public void listenForHelloSpringMessaging(@Payload HelloWorldMessage helloWorldMessage,
                               @Headers MessageHeaders headers, Message jmsMessage,
                               //using springframework.messaging instead of JMS Message
                               //this has the benefit to abstract away the messaging system used
                               //if we want to switch for instance from JMS to Kafka
                               org.springframework.messaging.Message springMessage) throws JMSException {

        HelloWorldMessage payloadMsg = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("World!!")
                .build();

        //example to use Spring Message type
        // we have the JMS, the javax.jms message, that is the JMS API specification for a message.
        // We are using the ActiveMQ message class as implementation of JMS Java API.
        //here we are decoupling from the native JMS message.
        jmsTemplate.convertAndSend((Destination) springMessage.getHeaders().get("jms_replyTo"), "got it!");

        //using the native JMS message
        //jmsTemplate.convertAndSend(jmsMessage.getJMSReplyTo(), payloadMsg);

    }
}