package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(name = "equipment_id1")
    private Long equipmentId1;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id1", insertable = false, updatable = false)
    private Equipment equipment1;

    @Column(name = "equipment_id2")
    private Long equipmentId2;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id2", insertable = false, updatable = false)
    private Equipment equipment2;

    @Column(name = "equipment_id3")
    private Long equipmentId3;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id3", insertable = false, updatable = false)
    private Equipment equipment3;

    @Column(name = "equipment_id4")
    private Long equipmentId4;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id4", insertable = false, updatable = false)
    private Equipment equipment4;

    @Column(name = "equipment_id5")
    private Long equipmentId5;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id5", insertable = false, updatable = false)
    private Equipment equipment5;

    @Column(name = "equipment_id6")
    private Long equipmentId6;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id6", insertable = false, updatable = false)
    private Equipment equipment6;

    private String productCode1;
    private String productCode2;
    private String productCode3;
    private String productCode4;
    private String productCode5;
    private String productCode6;

    private String serialNumber1;
    private String serialNumber2;
    private String serialNumber3;
    private String serialNumber4;
    private String serialNumber5;
    private String serialNumber6;

    private LocalDate signatureDate;
    private String trainedPerson;
    private String jobFunction;
    private String phone;
    private String email;
    private String contactPerson;
}
