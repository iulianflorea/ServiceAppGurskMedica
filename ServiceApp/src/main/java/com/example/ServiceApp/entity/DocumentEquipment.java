package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "document_equipment")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DocumentEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_data_id")
    private Long documentDataId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_data_id", insertable = false, updatable = false)
    private DocumentData documentData;

    @Column(name = "equipment_id")
    private Long equipmentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id", insertable = false, updatable = false)
    private Equipment equipment;

    private String equipmentName;
    private String productCode;
    private String serialNumber;
    private Integer sortOrder;
}
