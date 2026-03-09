package com.example.ServiceApp.dto;

import lombok.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CbctDeviceDto {
    private Long id;
    private String brand;
    private String model;
    private List<CbctReferenceDto> references;
}
