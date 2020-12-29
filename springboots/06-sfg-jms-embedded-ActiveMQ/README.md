# SFG JMS Example
This repository contains source code examples of JMS with Spring boot Framework.

## how to use JMS 
create a spring boot app with the following dependencies :
```xml
<dependencies>
	<dependency>
		<!--get us the jms client-->
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-artemis</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-devtools</artifactId>
		<scope>runtime</scope>
		<optional>true</optional>
	</dependency>
	<!--needed to create an embedded jms server-->
	<dependency>
		<groupId>org.apache.activemq</groupId>
		<artifactId>artemis-server</artifactId>
	</dependency>
	<!--needed to create an embedded jms server-->
	<dependency>
		<groupId>org.apache.activemq</groupId>
		<artifactId>artemis-jms-server</artifactId>
	</dependency>
	<dependency>
		<groupId>org.projectlombok</groupId>
		<artifactId>lombok</artifactId>
		<optional>true</optional>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-test</artifactId>
		<scope>test</scope>
	</dependency>
</dependencies>
```
### embedded activemq server

1- create the message HelloWorldMessage class
``` Java
package com.elearning.sbf.jmssample.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//for text message (json, xml) we DO NOT need Serializable,
//but if we used a Java Object JMS exchange we
//DO NEED Serializable.
public class HelloWorldMessage implements Serializable {

    //if not Java will create one for us,
    //needed for serialization to keep track of serialized object see Serializable class.
    static final long serialVersionUID = -14472175156460933L;

    private UUID id;
    private String message;
}

```

2 - start activemq server in the startup class
```java
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.core.server.ActiveMQServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JmsSampleApplication {

    public static void main(String[] args) throws Exception {
        //We create an embedded ActiveMQServer
        ActiveMQServer server = ActiveMQServers.newActiveMQServer(new ConfigurationImpl()
                .setPersistenceEnabled(false)
                .setJournalDirectory("target/data/journal") //local build directory
                .setSecurityEnabled(false)
                //enable communication within the VM
                .addAcceptorConfiguration("invm", "vm://0"));

		//start the activemq server in embedded mode.	
        server.start(); 

        SpringApplication.run(JmsSampleApplication.class, args);
    }
}

```
> this configuration isn't necessary because if we do have the server on the class path, Spring Boot is going to automatically bring up a configuration for us. We did just an exercice to set it up explicitly, when we have these two dependencies (i.e. `artemis-server` and `artemis-jms-server`) Spring Boot is going to auto configure it.

3- configure a spring boot scheduler to send periodically a message

- setting up a a bean called Task Executor and enable scheduling.
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//instruct Spring to expect a TaskExecutor class.
@EnableScheduling
//enable running async task out of a task pool.
@EnableAsync
//Scan by spring boot as a configuration bean.
@Configuration
public class TaskConfig {
    //setting up a a bean called Task Executor as a Simple Async TaskExecutor,
    //it will allow us to run async task, along with @EnableScheduling
    //which instructs Spring to expect a TaskExecutor class for scheduled tasks,
    //this will set up a schedule task that enables us to send out a message at a periodic basis.
    @Bean
    TaskExecutor taskExecutor(){
        return new SimpleAsyncTaskExecutor();
    }
}
``` 
- create a MappingJackson2MessageConverter
```java
	
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {
    
    //QUEUE Name
    public static final String MY_QUEUE = "my-hello-world";
    @Bean
    public MessageConverter messageConverter(){
        //Message converter that uses Jackson 2.x to convert messages to and from JSON. Maps an
        // object to a BytesMessage, or to a TextMessage if the targetType is set to MessageType.TEXT.
        // Converts from a TextMessage or BytesMessage to an object.
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        //Specify whether toMessage(Object, Session) should marshal to a BytesMessage or a TextMessage.
        //The default is MessageType.BYTES,
        converter.setTargetType(MessageType.TEXT);
        //Specify the name of the JMS message property that carries the type id for the contained object:
        //either a mapped id value or a raw Java class name.
		//e.g. key value pair : _type :com.elearning.sbf.jmssample.model.HelloWorldMessage
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
```
- Create a **sender/producer**

```java
mport com.elearning.sbf.jmssample.config.JmsConfig;
import com.elearning.sbf.jmssample.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HelloSender {

    //use as client to connect and send message to queue
    //JmsTemplate is pre-configured JMS template much like Spring JDBC template
    // it's autowired through lombok!
    private final JmsTemplate jmsTemplate;

    //called each 2000 milliseconds/2 secs
    @Scheduled(fixedRate = 2000)
    //The TaskConfig class instructs the task configuration to hold the TaskExecutor bean
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

}		
```

- Create a **message listener** :

*All that is required is payload and the HelloWorldMessage, so we could have omitted the headers and the message.* 
*they are there Because sometimes we might want to put custom properties in the headers (e.g. correlation ID)*

```java
import com.elearning.sbf.jmssample.config.JmsConfig;
import com.elearning.sbf.jmssample.model.HelloWorldMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Message;

@Component
public class HelloMessageListener {

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
        // Inside listen everything behave as single transaction, if it fails it will retry.
        // uncomment to see retry count in debugger
        // throw new RuntimeException("foo");
    }
}
```

- Case where we need to **send and receive across the JMS broker** (e.g. sending out to a queue, the message consumer then replies back on a temporary queue):

**HelloMessageListener.java** becomes :

```java
import com.elearning.sbf.jmssample.config.JmsConfig;
import com.elearning.sbf.jmssample.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HelloMessageListener {

    private final JmsTemplate jmsTemplate;
    @JmsListener(destination = JmsConfig.MY_SEND_RCV_QUEUE)
    public void listenForHello(@Payload HelloWorldMessage helloWorldMessage,
                               @Headers MessageHeaders headers,
                               Message message) throws JMSException {

        HelloWorldMessage payloadMsg = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("World!!")
                .build();

        jmsTemplate.convertAndSend(message.getJMSReplyTo(), payloadMsg);
    }
}
```

**HelloSender.java** becomes :

```java
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

    @Scheduled(fixedRate = 2000)
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
```
**Important**:  we resort to `jmsTemplate.sendAndReceive`, because `Jackson and Spring Boot` is wired into the convert and send (e.g. `jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message)`), we need manually to create a JMS text message, and in the constructor, we are providing the JSON value through the object mapper, and we set "_type" and with fully qualified class name of that JSON payload.
 