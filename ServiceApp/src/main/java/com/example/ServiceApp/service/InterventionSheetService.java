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

import java.util.List;

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
//        Equipment equipment = equipmentRepository.findById(interventionSheetDto.getEquipmentId()).orElseThrow();
        Customer customer = customerRepository.findById(interventionSheetDto.getCustomerId()).orElseThrow();
        Employee employee = employeeRepository.findById(interventionSheetDto.getEmployeeId()).orElseThrow();
//        interventionSheetToBeSaved.setEquipmentId(equipment.getId());
        interventionSheetToBeSaved.setCustomerId(customer.getId());
        interventionSheetToBeSaved.setEmployeeId(employee.getId());
        InterventionSheet interventionSheetSaved = interventionSheetRepository.save(interventionSheetToBeSaved);
        return interventionSheetMapper.toDto(interventionSheetSaved);
    }

    public InterventionSheetDto findById(Long id) {
        InterventionSheet interventionSheet = interventionSheetRepository.findById(id).orElseThrow();
        return interventionSheetMapper.toDto(interventionSheet);
    }

    public List<InterventionSheetDto> findAll() {
        List<InterventionSheet> interventionSheetList = interventionSheetRepository.findAll();
        return interventionSheetMapper.toDtoList(interventionSheetList);
    }

    public InterventionSheetDto update(InterventionSheetDto interventionSheetDto) {
        InterventionSheet interventionSheet = interventionSheetRepository.findById(interventionSheetDto.getId()).orElseThrow();
        interventionSheet.setEmployeeId(interventionSheetDto.getEmployeeId());
        interventionSheet.setDateOfIntervention(interventionSheetDto.getDateOfIntervention());
        interventionSheet.setCustomerId(interventionSheetDto.getCustomerId());
        interventionSheet.setEquipmentId(interventionSheetDto.getEquipmentId());
        interventionSheet.setFixed(interventionSheetDto.getFixed());
        interventionSheet.setSerialNumber(interventionSheetDto.getSerialNumber());
        interventionSheet.setEngineerNote(interventionSheetDto.getEngineerNote());
        interventionSheet.setNoticed(interventionSheetDto.getNoticed());
        interventionSheet.setTypeOfIntervention(interventionSheetDto.getTypeOfIntervention());
        InterventionSheet interventionSheetSaved = interventionSheetRepository.save(interventionSheet);
        return interventionSheetMapper.toDto(interventionSheetSaved);
    }

    public void delete (Long id) {
        interventionSheetRepository.deleteById(id);
    }

}
