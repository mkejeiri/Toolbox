package elearning.sfg.beer.msscbeerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude =ArtemisAutoConfiguration.class)
@SpringBootApplication(scanBasePackages = {"elearning.sfg.beer"})
public class MsscBeerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsscBeerServiceApplication.class, args);
    }
}
