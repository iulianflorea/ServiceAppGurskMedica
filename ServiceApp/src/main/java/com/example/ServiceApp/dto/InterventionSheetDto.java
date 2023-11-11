package com.example.ServiceApp.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class InterventionSheetDto {
    private Long id;
    private String typeOfIntervention;
    private Long equipmentId;
    private String serialNumber;
    private Long dateOfIntervention;
    private Long customerId;
    private Long employeeId;
    private String noticed;
    private String fixed;
    private String engineerNote;
}
