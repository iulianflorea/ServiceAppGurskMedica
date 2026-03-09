package com.example.ServiceApp.dto;

import com.example.ServiceApp.entity.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TicketDto {

    private Long id;
    private String clinicName;
    private String equipmentModel;
    private String equipmentBrand;
    private String serialNumber;
    private String phone;
    private String email;
    private String problem;
    private String city;
    private String address;
    private TicketStatus status;
    private LocalDateTime createdAt;
}
