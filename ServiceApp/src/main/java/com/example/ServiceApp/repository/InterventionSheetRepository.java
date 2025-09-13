package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.InterventionSheet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@EnableJpaRepositories

public interface InterventionSheetRepository extends JpaRepository<InterventionSheet, Long> {


//    @Query("""
//    SELECT t FROM InterventionSheet t
//    WHERE
//        (:keyword IS NULL OR :keyword = '' OR (
//            LOWER(t.typeOfIntervention) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.serialNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.equipment.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.customer.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.employee.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.noticed) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.fixed) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//            LOWER(t.engineerNote) LIKE LOWER(CONCAT('%', :keyword, '%'))
//        ))
//        AND (:dateOfIntervention IS NULL OR t.dateOfIntervention = :dateOfIntervention)
//        AND (:dataOfExpireWarranty IS NULL OR t.dataOfExpireWarranty = :dataOfExpireWarranty)
//        AND (:yearsOfWarranty IS NULL OR t.yearsOfWarranty = :yearsOfWarranty)
//""")
//    List<InterventionSheet> searchIntervention(
//            @Param("keyword") String keyword,
//            @Param("dateOfIntervention") LocalDate dateOfIntervention,
//            @Param("dataOfExpireWarranty") LocalDate dataOfExpireWarranty,
//            @Param("yearsOfWarranty") Integer yearsOfWarranty
//    );

    @Query("""
    SELECT t FROM InterventionSheet t 
    WHERE 
        (:keyword IS NULL OR :keyword = '' OR (
            LOWER(t.typeOfIntervention) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
            LOWER(t.serialNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
            LOWER(t.equipment.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
            LOWER(t.customer.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
            LOWER(t.employee.firstname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
            LOWER(t.noticed) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
            LOWER(t.fixed) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
            LOWER(t.engineerNote) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            CAST(t.yearsOfWarranty AS string) LIKE CONCAT('%', :keyword, '%') OR
            CAST(t.dateOfIntervention AS string) LIKE CONCAT('%', :keyword, '%') OR
            CAST(t.dataOfExpireWarranty AS string) LIKE CONCAT('%', :keyword, '%')
        ))
        ORDER BY t.id DESC
""")
    List<InterventionSheet> searchIntervention(@Param("keyword") String keyword);

    List<InterventionSheet> findAllByOrderByIdDesc();
    List<InterventionSheet> findAllByOrderByDateOfInterventionDesc();
    Page<InterventionSheet> findAllByOrderByDateOfInterventionDesc(Pageable pageable);

}