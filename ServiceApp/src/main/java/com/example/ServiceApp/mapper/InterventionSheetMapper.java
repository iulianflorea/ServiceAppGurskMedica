package com.example.ServiceApp.mapper;

import com.example.ServiceApp.dto.InterventionSheetDto;
import com.example.ServiceApp.entity.Customer;
import com.example.ServiceApp.entity.Employee;
import com.example.ServiceApp.entity.Equipment;
import com.example.ServiceApp.entity.InterventionSheet;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor

public class InterventionSheetMapper {

    public InterventionSheet toInterventionSheet(InterventionSheetDto interventionSheetDto) {
        return InterventionSheet.builder()
                .typeOfIntervention(interventionSheetDto.getTypeOfIntervention())
                .equipmentId(interventionSheetDto.getEquipmentId())
                .serialNumber(interventionSheetDto.getSerialNumber())
                .dateOfIntervention(interventionSheetDto.getDateOfIntervention())
                .customerId(interventionSheetDto.getCustomerId())
                .employeeId(interventionSheetDto.getEmployeeId())
                .noticed(interventionSheetDto.getNoticed())
                .fixed(interventionSheetDto.getFixed())
                .engineerNote(interventionSheetDto.getEngineerNote())
                .build();
    }

//    public InterventionSheetDto toDto(InterventionSheet interventionSheet) {
//        return InterventionSheetDto.builder()
//                .id(interventionSheet.getId())
//                .typeOfIntervention(interventionSheet.getTypeOfIntervention())
//                .equipmentId(interventionSheet.getEquipmentId())
//                .serialNumber(interventionSheet.getSerialNumber())
//                .dateOfIntervention(interventionSheet.getDateOfIntervention())
//                .customerId(interventionSheet.getCustomerId())
//                .employeeId(interventionSheet.getEmployeeId())
//                .noticed(interventionSheet.getNoticed())
//                .fixed(interventionSheet.getFixed())
//                .engineerNote(interventionSheet.getEngineerNote())
//                .customerName(interventionSheet.getCustomer().getName())
//                .employeeName(interventionSheet.getEmployee().getName())
//                .equipmentName(interventionSheet.getEquipment().getModel())
//                .build();
//    }


    public InterventionSheetDto toDto(InterventionSheet interventionSheet) {
        return InterventionSheetDto.builder()
                .id(interventionSheet.getId())
                .typeOfIntervention(interventionSheet.getTypeOfIntervention())
                .equipmentId(interventionSheet.getEquipmentId())
                .serialNumber(interventionSheet.getSerialNumber())
                .dateOfIntervention(interventionSheet.getDateOfIntervention())
                .customerId(interventionSheet.getCustomerId())
                .employeeId(interventionSheet.getEmployeeId())
                .noticed(interventionSheet.getNoticed())
                .fixed(interventionSheet.getFixed())
                .engineerNote(interventionSheet.getEngineerNote())
                .customerName(getCustomerName(interventionSheet))
                .employeeName(getEmployeeName(interventionSheet))
                .equipmentName(getEquipmentName(interventionSheet))
                .build();
    }

    private String getCustomerName(InterventionSheet interventionSheet) {
        Customer customer = interventionSheet.getCustomer();
        return (customer != null) ? customer.getName() : null;
    }

    private String getEmployeeName(InterventionSheet interventionSheet) {
        Employee employee = interventionSheet.getEmployee();
        return (employee != null) ? employee.getName() : null;
    }

    private String getEquipmentName(InterventionSheet interventionSheet) {
        Equipment equipment = interventionSheet.getEquipment();
        return (equipment != null) ? equipment.getModel() : null;
    }

    public List<InterventionSheetDto> toDtoList(List<InterventionSheet> interventionSheetList) {
        List<InterventionSheetDto> interventionSheetDtoList = new ArrayList<>();
        for (InterventionSheet interventionSheet : interventionSheetList) {
            InterventionSheetDto interventionSheetDto = toDto(interventionSheet);
            interventionSheetDtoList.add(interventionSheetDto);
        }
        return interventionSheetDtoList;
    }

}
