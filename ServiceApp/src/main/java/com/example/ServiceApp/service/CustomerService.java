package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.CustomerDto;
import com.example.ServiceApp.dto.ProductDto;
import com.example.ServiceApp.entity.Customer;
import com.example.ServiceApp.entity.Product;
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
        if (customerDto.getId() == null) {
            Customer customerSaved = customerRepository.save(customerToBeSaved);
            return customerMapper.toDto(customerSaved);
        } else {
            update(customerDto);
        }
        return customerMapper.toDto(customerToBeSaved);
    }

    public CustomerDto findById(Long id) {
        Customer customerToBeFind = customerRepository.findById(id).orElseThrow();
        return customerMapper.toDto(customerToBeFind);
    }

    public List<CustomerDto> findAll() {
        List<Customer> customers = customerRepository.findAll();
        return customerMapper.toDtoList(customers);
    }

    public CustomerDto update(CustomerDto customerDto) {
        Customer customerToBeSaved = customerRepository.findById(customerDto.getId()).orElseThrow();
        customerToBeSaved.setName(customerDto.getName());
        customerToBeSaved.setCui(customerDto.getCui());
        customerToBeSaved.setAddress(customerDto.getAddress());
        customerToBeSaved.setTelephone(customerDto.getTelephone());
        customerToBeSaved.setEmail(customerDto.getEmail());
        Customer customerSaved = customerRepository.save(customerToBeSaved);
        return customerMapper.toDto(customerSaved);
    }

    public void delete(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow();
        customerRepository.delete(customer);
    }


    public List<CustomerDto> search(String keyword) {
        List<Customer> customerList = customerRepository.searchCustomer(keyword);
        return customerMapper.toDtoList(customerList);
    }

}
