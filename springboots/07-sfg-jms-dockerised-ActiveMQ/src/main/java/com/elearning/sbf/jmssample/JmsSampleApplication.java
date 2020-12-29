package com.elearning.sbf.jmssample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JmsSampleApplication {

    /*
    this configuration actually isn't necessary because if you do have the server on your class path,
    Spring Boot is going to automatically bring up a configuration for us.
    We did just an exercise to set it up explicitly, when we have these
    two dependencies (i.e. `artemis-server` and `artemis-jms-server`)
    Spring Boot is going to auto configure it.
    **/
    public static void main(String[] args) throws Exception {
        /*
        //We create an embedded ActiveMQServer
        ActiveMQServer server = ActiveMQServers.newActiveMQServer(new ConfigurationImpl()
                .setPersistenceEnabled(false)
                .setJournalDirectory("target/data/journal") //local build directory
                .setSecurityEnabled(false)
                //enable communication within the VM
                .addAcceptorConfiguration("invm", "vm://0"));
        server.start(); */
        SpringApplication.run(JmsSampleApplication.class, args);

    }
}
