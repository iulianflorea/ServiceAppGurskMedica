package com.example.ServiceApp.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BackupDto {
    private Long id;
    private String sqlPath;
    private String documentPath;
}
