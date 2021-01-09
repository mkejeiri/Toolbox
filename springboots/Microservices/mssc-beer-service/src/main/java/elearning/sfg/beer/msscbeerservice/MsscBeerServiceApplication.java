package elearning.sfg.beer.msscbeerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//@SpringBootApplication(exclude =ArtemisAutoConfiguration.class)

@EnableFeignClients
//@SpringBootApplication(scanBasePackages = {"elearning.sfg.beer"})
@SpringBootApplication()
public class MsscBeerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsscBeerServiceApplication.class, args);
    }
}
