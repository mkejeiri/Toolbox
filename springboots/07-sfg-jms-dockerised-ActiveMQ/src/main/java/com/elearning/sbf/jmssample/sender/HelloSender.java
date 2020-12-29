package com.elearning.sbf.jmssample.sender;
import com.elearning.sbf.jmssample.config.JmsConfig;
import com.elearning.sbf.jmssample.model.HelloWorldMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HelloSender {

    private final ObjectMapper objectMapper;

    //use as client to connect and send message to queue
    //JmsTemplate is pre-configured JMS template much like Spring JDBC template
    // it's autowired through lombok!
    private final JmsTemplate jmsTemplate;

    //called each 2000 milliseconds/2 secs
    @Scheduled(fixedRate = 5000)
    //The TaskConfig class tells the task configuration to hold the TaskExecutor bean
    //and execute the sendMessage every two seconds.
    public void sendMessage(){

        System.out.println("I'm Sending a message");

        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello World!")
                .build();

        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);

        System.out.println("Message Sent!");

    }

    @Scheduled(fixedRate = 10000)
    public void sendAndReceiveMessage() throws JMSException {

        //sending only hello and get world back!
        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello")
                .build();

        //wait for receive message
        Message receviedMsg = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RCV_QUEUE, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message helloMessage = null;

                try {
                    helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                    helloMessage.setStringProperty("_type", HelloWorldMessage.class.getTypeName());

                    System.out.println("Sending Hello");

                    return helloMessage;

                } catch (JsonProcessingException e) {
                    throw new JMSException("induced exception");
                }
            }
        });

        //print receive message
        System.out.println(receviedMsg.getBody(String.class));

    }



}