package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "document_trained_person")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DocumentTrainedPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_data_id")
    private Long documentDataId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_data_id", insertable = false, updatable = false)
    private DocumentData documentData;

    private String trainedPersonName;
    private String jobFunction;
    private String phone;
    private String email;

    @Column(columnDefinition = "LONGTEXT")
    private String signatureBase64;

    private Integer sortOrder;
}
