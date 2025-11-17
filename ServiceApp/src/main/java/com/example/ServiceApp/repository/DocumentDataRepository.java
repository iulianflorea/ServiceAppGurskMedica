package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.DocumentData;
import com.example.ServiceApp.entity.InterventionSheet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.List;

@EnableJpaRepositories
public interface DocumentDataRepository extends JpaRepository<DocumentData, Long> {

    //    @Query("""
//    SELECT t FROM DocumentData t
//    WHERE
//        (:keyword IS NULL OR :keyword = '' OR (
//            LOWER(t.customer.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.cui) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.numberOfContract) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//
//            LOWER(t.equipment1.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.equipment2.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.equipment3.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.equipment4.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.equipment5.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.equipment6.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//
//            LOWER(t.productCode1) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.productCode2) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.productCode3) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.productCode4) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.productCode5) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.productCode6) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//
//            LOWER(t.serialNumber1) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.serialNumber2) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.serialNumber3) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.serialNumber4) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.serialNumber5) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.serialNumber6) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//
//            LOWER(t.trainedPerson) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.jobFunction) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.contactPerson) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            CAST(t.monthOfWarranty AS string) LIKE CONCAT('%', :keyword, '%') OR
//            CAST(t.contractDate AS string) LIKE CONCAT('%', :keyword, '%') OR
//            CAST(t.signatureDate AS string) LIKE CONCAT('%', :keyword, '%')
//        ))
//        ORDER BY t.id DESC
//""")
    @Query("""
                SELECT DISTINCT t FROM DocumentData t
                LEFT JOIN t.customer c
                LEFT JOIN t.equipment1 e1
                LEFT JOIN t.equipment2 e2
                LEFT JOIN t.equipment3 e3
                LEFT JOIN t.equipment4 e4
                LEFT JOIN t.equipment5 e5
                LEFT JOIN t.equipment6 e6
                WHERE 
                    :keyword IS NULL OR :keyword = '' OR (
                        LOWER(COALESCE(c.name, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.cui, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.numberOfContract, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR

                        LOWER(COALESCE(e1.model, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(e2.model, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(e3.model, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(e4.model, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(e5.model, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(e6.model, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR

                        LOWER(COALESCE(t.productCode1, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.productCode2, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.productCode3, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.productCode4, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.productCode5, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.productCode6, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR

                        LOWER(COALESCE(t.serialNumber1, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.serialNumber2, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.serialNumber3, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.serialNumber4, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.serialNumber5, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.serialNumber6, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR

                        LOWER(COALESCE(t.trainedPerson, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.jobFunction, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.phone, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.email, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.contactPerson, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR

                        CAST(t.monthOfWarranty AS string) LIKE CONCAT('%', :keyword, '%') OR
                        CAST(t.monthOfWarrantyHandPieces AS string) LIKE CONCAT('%', :keyword, '%') OR
                        CAST(t.contractDate AS string) LIKE CONCAT('%', :keyword, '%') OR
                        CAST(t.signatureDate AS string) LIKE CONCAT('%', :keyword, '%')
                    )
                ORDER BY t.id DESC
            """)
    List<DocumentData> searchDocument(@Param("keyword") String keyword);

    Page<DocumentData> findAllByOrderByContractDateDesc(Pageable pageable);
}
