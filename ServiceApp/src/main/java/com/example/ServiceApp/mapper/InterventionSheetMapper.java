package com.example.ServiceApp.mapper;

import com.example.ServiceApp.dto.InterventionSheetDto;
import com.example.ServiceApp.entity.*;
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
                .dataOfExpireWarranty(interventionSheetDto.getDateOfExpireWarranty())
                .yearsOfWarranty(interventionSheetDto.getYearsOfWarranty())
                .noticed(interventionSheetDto.getNoticed())
                .fixed(interventionSheetDto.getFixed())
                .engineerNote(interventionSheetDto.getEngineerNote())
                .signatureBase64(interventionSheetDto.getSignatureBase64())
                .build();
    }



    public InterventionSheetDto toDto(InterventionSheet interventionSheet) {
        return InterventionSheetDto.builder()
                .id(interventionSheet.getId())
                .typeOfIntervention(interventionSheet.getTypeOfIntervention())
                .equipmentId(interventionSheet.getEquipmentId())
                .serialNumber(interventionSheet.getSerialNumber())
                .dateOfIntervention(interventionSheet.getDateOfIntervention())
                .dateOfExpireWarranty(interventionSheet.getDataOfExpireWarranty())
                .customerId(interventionSheet.getCustomerId())
                .employeeId(interventionSheet.getEmployeeId())
                .noticed(interventionSheet.getNoticed())
                .yearsOfWarranty(interventionSheet.getYearsOfWarranty())
                .fixed(interventionSheet.getFixed())
                .engineerNote(interventionSheet.getEngineerNote())
                .customerName(getCustomerName(interventionSheet))
                .employeeName(getEmployeeName(interventionSheet))
                .equipmentName(getEquipmentName(interventionSheet))
                .signatureBase64(interventionSheet.getSignatureBase64())
                .build();
    }

    private String getCustomerName(InterventionSheet interventionSheet) {
        Customer customer = interventionSheet.getCustomer();
        return (customer != null) ? customer.getName() : null;
    }

    private String getEmployeeName(InterventionSheet interventionSheet) {
        User employee = interventionSheet.getEmployee();
        return (employee != null) ? employee.getFirstname() : null;
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
