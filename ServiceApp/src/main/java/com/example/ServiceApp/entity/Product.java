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
    @Column(name = "producer_id")
    private Long producerId;
    @ManyToOne(targetEntity = Producer.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "producer_id", insertable = false, updatable = false)
    private Producer producer;
    private String imageName;
    private Double price;

    public Double getPriceWithVAT() {
        if (price == null) return null;
        return price * 1.21;
    }

}
