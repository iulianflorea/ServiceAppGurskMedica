package com.example.ServiceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PasswordResetDto {
    private Long userId;
    private String newPassword;
}
