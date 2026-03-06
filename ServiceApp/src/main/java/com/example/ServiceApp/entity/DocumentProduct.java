package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "document_product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DocumentProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_data_id")
    private Long documentDataId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_data_id", insertable = false, updatable = false)
    private DocumentData documentData;

    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    private String productName;
    private String productCod;
    private Integer quantity;
    private Integer sortOrder;
}
