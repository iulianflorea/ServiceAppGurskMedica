package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class CbctDeviceReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id")
    private Long deviceId;

    private String mode;   // CT, PANO, CEPH
    private String gender; // BARBAT, FEMEIE, COPIL
    private Double kvp;
    private Double current;
    private Double scanTime;
    private Double dap;
}
