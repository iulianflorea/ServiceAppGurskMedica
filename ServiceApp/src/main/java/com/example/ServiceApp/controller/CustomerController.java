package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.CustomerDto;
import com.example.ServiceApp.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public CustomerDto create(@RequestBody CustomerDto customerDto) {
        return customerService.create(customerDto);
    }

    @GetMapping("/{id}")
    public CustomerDto findById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @GetMapping("/customer-list")
    public List<CustomerDto> findAll() {
        return customerService.findAll();
    }

    @PutMapping("/update")
    public CustomerDto update(@RequestBody CustomerDto customerDto) {
        return customerService.update(customerDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }
}
