package com.example.ServiceApp.dto;

import com.example.ServiceApp.entity.Customer;
import com.example.ServiceApp.entity.Employee;
import com.example.ServiceApp.entity.Equipment;
import com.example.ServiceApp.entity.TypeOfIntervention;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class InterventionSheetDto {
    private Long id;
    private TypeOfIntervention typeOfIntervention;
    private Equipment equipmentId;
    private String serialNumber;
    private Date dateOfIntervention;
    private Customer customerId;
    private Employee employeeId;
    private String noticed;
    private String fixed;
    private String engineerNote;
}
