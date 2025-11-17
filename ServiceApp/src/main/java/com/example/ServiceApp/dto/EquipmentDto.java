package com.example.ServiceApp.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class EquipmentDto {

    private Long id;
    private String model;
    private String productCode;
    private Long producerId;
    private String producerName;
}
