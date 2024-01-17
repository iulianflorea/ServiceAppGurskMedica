package com.example.ServiceApp.mapper;

import com.example.ServiceApp.dto.EmployeeDto;
import com.example.ServiceApp.entity.Employee;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeMapper {

    public Employee toEmployee(EmployeeDto employeeDto) {
        return Employee.builder()
                .cnp(employeeDto.getCnp())
                .name(employeeDto.getName())
                .build();
    }

    public EmployeeDto toDto(Employee employee) {
        return EmployeeDto.builder()
                .id(employee.getId())
                .cnp(employee.getCnp())
                .name(employee.getName())
                .build();
    }

    public List<EmployeeDto> toDtoList(List<Employee> employeeList) {
        List<EmployeeDto> employeeDtoList = new ArrayList<>();
        for (Employee employee : employeeList) {
            EmployeeDto employeeDto = toDto(employee);
            employeeDtoList.add(employeeDto);
        }
        return employeeDtoList;
    }
}
