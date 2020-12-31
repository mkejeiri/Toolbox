package elearning.sfg.beer.msscbeerservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@EnableConfigurationProperties(ArtemisProperties.class)
@Configuration
public class JmsConfig {

    //QUEUE Name
    public static final String BREWING_REQUEST_QUEUE = "BREWING_REQUEST_QUEUE";
    public static final String NEW_INVENTORY_QUEUE = "NEW_INVENTORY_QUEUE";

    @Bean
    public MessageConverter  jacksonJmsMessageConverter(
            ObjectMapper objectMapper //Spring managed object mapper injection (used by web endpoint).
            //OffSetTime mapping not working properly => reconfigure Jackson JMS message converter
            //to use the Spring Boot managed Jackson object mapper.
    ){
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

        //set spring managed object mapper
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}