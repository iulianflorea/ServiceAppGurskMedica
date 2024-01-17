package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.EmployeeDto;
import com.example.ServiceApp.entity.Employee;
import com.example.ServiceApp.mapper.EmployeeMapper;
import com.example.ServiceApp.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    public EmployeeDto create (EmployeeDto employeeDto) {
        Employee employeeToBeSaved = employeeMapper.toEmployee(employeeDto);
        Employee employeeSaved = employeeRepository.save(employeeToBeSaved);
        return employeeMapper.toDto(employeeSaved);
    }

    public EmployeeDto findById(Long id) {
        Employee employeeFound = employeeRepository.findById(id).orElseThrow();
        return employeeMapper.toDto(employeeFound);
    }

    public List<EmployeeDto> findAll() {
        List<Employee> employeeList = employeeRepository.findAll();
        return employeeMapper.toDtoList(employeeList);
    }

    public EmployeeDto update(EmployeeDto employeeDto) {
        Employee employee = employeeRepository.findById(employeeDto.getId()).orElseThrow();
        employee.setCnp(employeeDto.getCnp());
        employee.setName(employeeDto.getName());
        Employee employeeSaved = employeeRepository.save(employee);
        return employeeMapper.toDto(employeeSaved);
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }
}
