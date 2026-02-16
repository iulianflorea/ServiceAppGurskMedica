package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.DocumentData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.List;

@EnableJpaRepositories
public interface DocumentDataRepository extends JpaRepository<DocumentData, Long> {

    @Query("""
                SELECT DISTINCT t FROM DocumentData t
                LEFT JOIN t.customer c
                LEFT JOIN t.equipments eq
                LEFT JOIN eq.equipment e
                LEFT JOIN t.trainedPersons tp
                WHERE
                    :keyword IS NULL OR :keyword = '' OR (
                        LOWER(COALESCE(c.name, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.cui, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(t.numberOfContract, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR

                        LOWER(COALESCE(e.model, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(eq.productCode, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(eq.serialNumber, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR

                        LOWER(COALESCE(tp.trainedPersonName, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(tp.jobFunction, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(tp.phone, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                        LOWER(COALESCE(tp.email, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
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
