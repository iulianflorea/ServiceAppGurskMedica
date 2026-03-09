package com.example.ServiceApp.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CbctMeasurementDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long deviceId;
    private String deviceBrand;
    private String deviceModel;
    private String serialNumber;
    private LocalDate measurementDate;
    private List<CbctMeasurementValueDto> values;
    private List<CbctDozimetrieDto> dozimetrie;
}
