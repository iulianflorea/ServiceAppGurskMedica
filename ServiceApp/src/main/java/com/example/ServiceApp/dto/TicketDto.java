package com.example.ServiceApp.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TicketDto {
    private Long id;
    private Long dateWhenTicketWasOpened;
    private String customerNotification;
    private Long customer;
}
