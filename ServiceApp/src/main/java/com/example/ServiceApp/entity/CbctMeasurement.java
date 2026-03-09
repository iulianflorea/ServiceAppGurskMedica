package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class CbctMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @Column(name = "device_id")
    private Long deviceId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    private CbctDevice device;

    private String serialNumber;
    private LocalDate measurementDate;
}
