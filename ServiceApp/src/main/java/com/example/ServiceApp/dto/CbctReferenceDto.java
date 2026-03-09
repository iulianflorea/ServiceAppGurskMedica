package com.example.ServiceApp.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CbctReferenceDto {
    private Long id;
    private Long deviceId;
    private String mode;
    private String gender;
    private Double kvp;
    private Double current;
    private Double scanTime;
    private Double dap;
}
