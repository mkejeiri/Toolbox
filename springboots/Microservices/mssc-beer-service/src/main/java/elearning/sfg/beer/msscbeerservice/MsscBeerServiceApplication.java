package elearning.sfg.beer.msscbeerservice;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@SpringBootApplication(exclude =ArtemisAutoConfiguration.class)
public class MsscBeerServiceApplication {

    @Autowired
    private JmsTemplate jmsTemplate;


    public static void main(String[] args) {
        SpringApplication.run(MsscBeerServiceApplication.class, args);

        ConfigurableApplicationContext context = SpringApplication.run(SpringApplication.class, args);
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        jmsTemplate.convertAndSend("robotCommand", "test");
    }

    @Bean
    public ConnectionFactory jmsConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        return connectionFactory;
    }
}
