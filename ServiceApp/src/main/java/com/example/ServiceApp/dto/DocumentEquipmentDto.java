package com.example.ServiceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEquipmentDto {

    private Long id;
    private Long equipmentId;
    private String equipmentName;
    private String productCode;
    private String serialNumber;
    private Integer sortOrder;
}
