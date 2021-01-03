package elearning.sfg.beer.order.service.services;

import elearning.sfg.beer.brewery.dtos.CustomerPagedList;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    CustomerPagedList listCustomers(Pageable pageable);
}
