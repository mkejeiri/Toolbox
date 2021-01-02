package elearning.sfg.beer.msscbeerservice.services.order;


import elearning.sfg.beer.brewery.dtos.BeerOrderDto;
import elearning.sfg.beer.msscbeerservice.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class BeerOrderValidatorImpl implements BeerOrderValidator {
    private final BeerRepository beerRepository;

    @Override
    public boolean isOrderValid(BeerOrderDto beerOrderDto) {
        //AtomicInteger beerNotFound = new AtomicInteger();

        return !beerOrderDto.getBeerOrderLines()
                .stream()
                .anyMatch(line -> beerRepository.findByUpc(line.getUpc()) == null);

        /*beerOrderDto.getBeerOrderLines().forEach(orderline -> {
            if (beerRepository.findByUpc(orderline.getUpc()) == null)
                beerNotFound.incrementAndGet();
        });
        return beerNotFound.get() == 0;*/
    }
}
