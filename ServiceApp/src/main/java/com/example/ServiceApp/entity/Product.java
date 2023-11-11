package com.example.ServiceApp.entity;

import com.example.ServiceApp.dto.ProducerDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String cod;
    private Integer quantity;
    @ManyToOne
    @JoinColumn(name = "producer_id")
    private Producer producerId;

}
