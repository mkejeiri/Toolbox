package elearning.sfg.beer.msscbeerservice.bootstrap;


import elearning.sfg.beer.msscbeerservice.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@RequiredArgsConstructor
@Component
public class BeerLoaderFromScript {
    private final BeerRepository beerRepository;
    private final DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        if(beerRepository.count() == 0) loadData();
    }

    private void loadData() {
        ResourceDatabasePopulator resourceDatabasePopulator =
                new ResourceDatabasePopulator(false,
                        false,
                        "UTF-8",
                        new ClassPathResource("data.sql"));
        resourceDatabasePopulator.execute(dataSource);
    }
}
