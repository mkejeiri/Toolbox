package com.supplychain.mssdrink.mappers;

import com.supplychain.mssdrink.domains.Customer;
import com.supplychain.mssdrink.dtos.CustomerDto;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDto dto);

    CustomerDto customerToCustomerDto(Customer customer);
}
