package com.example.ServiceApp.dto;

import com.example.ServiceApp.entity.Customer;
import com.example.ServiceApp.entity.Employee;
import com.example.ServiceApp.entity.Equipment;
import com.example.ServiceApp.entity.TypeOfIntervention;
import lombok.*;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class InterventionSheetDto {
    private Long id;
    private TypeOfIntervention typeOfIntervention;
    private Long equipmentId;
    private String serialNumber;
    private LocalDate dateOfIntervention;
    private LocalDate dateOfExpireWarranty;
    private Integer yearsOfWarranty;
    private Long customerId;
    private Long employeeId;
    private String noticed;
    private String fixed;
    private String engineerNote;
    private String customerName;
    private String equipmentName;
    private String employeeName;
    private String signatureBase64;
}
