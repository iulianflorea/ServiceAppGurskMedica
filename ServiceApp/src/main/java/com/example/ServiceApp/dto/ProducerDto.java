package com.example.ServiceApp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProducerDto {

    private Long id;
    private String name;
}
