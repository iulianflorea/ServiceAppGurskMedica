package com.example.ServiceApp.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductDto {

    private Long id;
    private String name;
    private String cod;
    private Integer quantity;
    private Long producer;
    private String producerName;
}

