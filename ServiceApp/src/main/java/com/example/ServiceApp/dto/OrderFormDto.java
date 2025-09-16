package com.example.ServiceApp.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OrderFormDto {

    private Long clientId;
    private List<OrderProductDto> products;
    private String deliveryAddress;
}
