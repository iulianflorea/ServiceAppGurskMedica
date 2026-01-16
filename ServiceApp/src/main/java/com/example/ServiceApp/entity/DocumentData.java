package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DocumentData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    private String cui;
    private LocalDate contractDate;
    private Integer monthOfWarranty;
    private Integer monthOfWarrantyHandPieces;
    private String numberOfContract;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_data_id", insertable = false, updatable = false)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<DocumentEquipment> equipments = new ArrayList<>();

    private LocalDate signatureDate;
    private String trainedPerson;
    private String jobFunction;
    private String phone;
    private String email;
    private String contactPerson;
}
