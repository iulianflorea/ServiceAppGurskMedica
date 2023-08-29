package com.example.ServiceApp.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class ProductDto {

    private Long id;
    private String name;
    private String cod;
    private Long producerId;
}

