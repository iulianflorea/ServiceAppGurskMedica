package com.example.ServiceApp.mapper;

import com.example.ServiceApp.dto.CustomerDto;
import com.example.ServiceApp.entity.Customer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class CustomerMapper {

    public Customer toCustomer(CustomerDto customerDto) {
        return Customer.builder()
                .name(customerDto.getName())
                .cui(customerDto.getCui())
                .address(customerDto.getAddress())
                .telephone(customerDto.getTelephone())
                .build();
    }

    public CustomerDto toDto(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .cui(customer.getCui())
                .address(customer.getAddress())
                .telephone(customer.getTelephone())
                .build();
    }

    public List<CustomerDto> toDtoList(List<Customer> customerList) {
        List<CustomerDto> customerDtoList = new ArrayList<>();
        for (Customer customer : customerList) {
            CustomerDto customerDto = toDto(customer);
            customerDtoList.add(customerDto);
        }
        return customerDtoList;
    }
}
