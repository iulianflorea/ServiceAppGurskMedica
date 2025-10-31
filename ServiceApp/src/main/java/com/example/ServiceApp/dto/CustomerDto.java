package com.example.ServiceApp.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CustomerDto {

    private Long id;
    private String name;
    private String cui;
    private String address;
    private String telephone;
    private String email;
}
