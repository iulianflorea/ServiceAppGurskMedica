package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class CbctMeasurementValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "measurement_id")
    private Long measurementId;

    private String mode;   // CT, PANO, CEPH
    private String gender; // BARBAT, FEMEIE, COPIL
    private Double kvp;
    private Double scanTime;
    private Double dap;
    private Double mgy;
    private Double mmAiHvl;
    private Double uGyPerS;
    private Double pulses;
    private Double mmAiTf;
}
