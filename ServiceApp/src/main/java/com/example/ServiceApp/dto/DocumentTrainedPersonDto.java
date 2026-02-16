package com.example.ServiceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTrainedPersonDto {

    private Long id;
    private String trainedPersonName;
    private String jobFunction;
    private String phone;
    private String email;
    private String signatureBase64;
    private Integer sortOrder;
}
