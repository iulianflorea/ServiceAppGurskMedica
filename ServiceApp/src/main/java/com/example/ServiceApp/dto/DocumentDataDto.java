package com.example.ServiceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDataDto {

    private Long id;
    private Long customerId;
    private String customerName;
    private String cui;
    private LocalDate contractDate;
    private Integer monthOfWarranty;
    private Integer monthOfWarrantyHandPieces;
    private String numberOfContract;

    @Builder.Default
    private List<DocumentEquipmentDto> equipments = new ArrayList<>();

    @Builder.Default
    private List<DocumentProductDto> products = new ArrayList<>();

    @Builder.Default
    private List<DocumentTrainedPersonDto> trainedPersons = new ArrayList<>();

    private LocalDate signatureDate;
    private String contactPerson;
}
