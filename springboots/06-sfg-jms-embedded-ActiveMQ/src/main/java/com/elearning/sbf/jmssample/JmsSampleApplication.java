package com.elearning.sbf.jmssample;

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
        server.start();
        SpringApplication.run(JmsSampleApplication.class, args);
    }

}
