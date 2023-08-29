package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Producer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "producerId")
    private Set<Product> productName = new HashSet<>();

    public Producer(Long id, String name, Set<Product> productName) {
        this.id = id;
        this.name = name;
        this.productName = productName;
    }
}
