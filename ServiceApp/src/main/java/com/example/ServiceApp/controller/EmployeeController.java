package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.EmployeeDto;
import com.example.ServiceApp.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public EmployeeDto create(@RequestBody EmployeeDto employeeDto) {
        return employeeService.create(employeeDto);
    }
    @GetMapping("/find-by-id/{id}")
    public EmployeeDto findById(@PathVariable Long id) {
       return employeeService.findById(id);
    }

    @GetMapping("/find-all")
    public List<EmployeeDto> findAll() {
        return employeeService.findAll();
    }

    @PutMapping()
    public EmployeeDto update(@RequestBody EmployeeDto employeeDto) {
        return employeeService.update(employeeDto);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        employeeService.delete(id);
    }
}
