package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.CustomerDto;
import com.example.ServiceApp.entity.Customer;
import com.example.ServiceApp.mapper.CustomerMapper;
import com.example.ServiceApp.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }


    public CustomerDto create(CustomerDto customerDto) {
        Customer customerToBeSaved = customerMapper.toCustomer(customerDto);
        Customer customerSaved = customerRepository.save(customerToBeSaved);
        return customerMapper.toDto(customerSaved);
    }

    public CustomerDto findById(Long id) {
        Customer customerToBeFind = customerRepository.findById(id).orElseThrow();
        return customerMapper.toDto(customerToBeFind);
    }

    public List<CustomerDto> findAll() {
        List<Customer> customers = customerRepository.findAll();
        return customerMapper.toDtoList(customers);
    }

}
