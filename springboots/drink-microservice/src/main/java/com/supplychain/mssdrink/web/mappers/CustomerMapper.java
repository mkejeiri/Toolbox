package com.supplychain.mssdrink.web.mappers;

import com.supplychain.mssdrink.domains.models.Customer;
import com.supplychain.mssdrink.web.dtos.CustomerDto;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDto dto);

    CustomerDto customerToCustomerDto(Customer customer);
}
