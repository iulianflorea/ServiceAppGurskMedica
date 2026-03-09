package com.example.ServiceApp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CbctMeasurementValueDto {
    private Long id;
    private Long measurementId;
    private String mode;
    private String gender;
    private Double kvp;
    private Double scanTime;
    private Double dap;
    private Double mgy;
    private Double mmAiHvl;
    @JsonProperty("uGyPerS")
    private Double uGyPerS;
    private Double pulses;
    private Double mmAiTf;
}
