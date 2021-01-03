package elearning.sfg.beer.order.service.web.mappers;

import elearning.sfg.beer.brewery.dtos.CustomerDto;
import elearning.sfg.beer.order.service.domain.Customer;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface CustomerMapper {
    CustomerDto customerToDto(Customer customer);

    Customer dtoToCustomer(Customer dto);
}