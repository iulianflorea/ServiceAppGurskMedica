package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String model;
    @Column(name = "producer_id")
    private Long producerId;
    @ManyToOne(targetEntity = Producer.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Producer producer;

}
