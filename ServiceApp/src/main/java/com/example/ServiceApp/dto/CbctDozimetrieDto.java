package com.example.ServiceApp.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CbctDozimetrieDto {
    private Long id;
    private Long measurementId;
    private String punctMasurat;
    private Double valoareaMaximaMarsurata;
    private String materialPerete;
}
