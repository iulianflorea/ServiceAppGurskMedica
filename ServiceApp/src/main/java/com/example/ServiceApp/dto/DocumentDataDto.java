package com.example.ServiceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDataDto {

    private Long id;
    private Long customerId;
    private String customerName; // optional, pentru afișare
    private String cui;
    private LocalDate contractDate;
    private Integer monthOfWarranty;
    private String numberOfContract;

    private Long equipmentId1;
    private Long equipmentId2;
    private Long equipmentId3;
    private Long equipmentId4;
    private Long equipmentId5;
    private Long equipmentId6;

    private String equipmentName1;
    private String equipmentName2;
    private String equipmentName3;
    private String equipmentName4;
    private String equipmentName5;
    private String equipmentName6;

    private String productCode1;
    private String productCode2;
    private String productCode3;
    private String productCode4;
    private String productCode5;
    private String productCode6;

    private String serialNumber1;
    private String serialNumber2;
    private String serialNumber3;
    private String serialNumber4;
    private String serialNumber5;
    private String serialNumber6;

    private LocalDate signatureDate;
    private String trainedPerson;
    private String jobFunction;
    private String phone;
    private String email;
    private String contactPerson;
}
