package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String cod;
    @ManyToOne
    @JoinColumn(name = "producer_id")
    private Producer producerId;

    public Product(Long id, String name, String cod, Producer producerId) {
        this.id = id;
        this.name = name;
        this.cod = cod;
        this.producerId = producerId;
    }
}
