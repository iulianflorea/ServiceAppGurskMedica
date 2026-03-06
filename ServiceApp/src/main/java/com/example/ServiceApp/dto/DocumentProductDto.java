package com.example.ServiceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentProductDto {

    private Long id;
    private Long productId;
    private String productName;
    private String productCod;
    private Integer quantity;
    private Integer sortOrder;
}
