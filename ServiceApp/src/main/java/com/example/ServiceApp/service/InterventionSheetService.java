package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.InterventionSheetDto;
import com.example.ServiceApp.entity.Customer;
import com.example.ServiceApp.entity.Employee;
import com.example.ServiceApp.entity.Equipment;
import com.example.ServiceApp.entity.InterventionSheet;
import com.example.ServiceApp.mapper.InterventionSheetMapper;
import com.example.ServiceApp.repository.CustomerRepository;
import com.example.ServiceApp.repository.EmployeeRepository;
import com.example.ServiceApp.repository.EquipmentRepository;
import com.example.ServiceApp.repository.InterventionSheetRepository;
import org.springframework.stereotype.Service;

@Service
public class InterventionSheetService {

    private final InterventionSheetRepository interventionSheetRepository;
    private final InterventionSheetMapper interventionSheetMapper;
    private final EquipmentRepository equipmentRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;


    public InterventionSheetService(InterventionSheetRepository interventionSheetRepository, InterventionSheetMapper interventionSheetMapper, EquipmentRepository equipmentRepository, CustomerRepository customerRepository, EmployeeRepository employeeRepository) {
        this.interventionSheetRepository = interventionSheetRepository;
        this.interventionSheetMapper = interventionSheetMapper;
        this.equipmentRepository = equipmentRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
    }

    public InterventionSheetDto create(InterventionSheetDto interventionSheetDto) {
        InterventionSheet interventionSheetToBeSaved = interventionSheetMapper.toInterventionSheet(interventionSheetDto);
        Equipment equipment = equipmentRepository.findById(interventionSheetDto.getEquipmentId().getId()).orElseThrow();
        Customer customer = customerRepository.findById(interventionSheetDto.getCustomerId().getId()).orElseThrow();
        Employee employee = employeeRepository.findById(interventionSheetDto.getEmployeeId().getId()).orElseThrow();
        interventionSheetToBeSaved.setEquipmentId(equipment);
        interventionSheetToBeSaved.setCustomerId(customer);
        interventionSheetToBeSaved.setEmployeeId(employee);
        InterventionSheet interventionSheetSaved = interventionSheetRepository.save(interventionSheetToBeSaved);
        return interventionSheetMapper.toDto(interventionSheetSaved);
    }
}
