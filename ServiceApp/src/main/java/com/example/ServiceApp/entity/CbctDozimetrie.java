package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class CbctDozimetrie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "measurement_id")
    private Long measurementId;

    private String punctMasurat;
    private Double valoareaMaximaMarsurata;
    private String materialPerete;
}
