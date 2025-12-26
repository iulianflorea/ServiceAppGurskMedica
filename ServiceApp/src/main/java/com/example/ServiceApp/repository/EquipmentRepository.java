package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.Equipment;
import com.example.ServiceApp.entity.InterventionSheet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.List;
@EnableJpaRepositories
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    @Query("""
    SELECT t FROM Equipment t 
    WHERE 
        (:keyword IS NULL OR :keyword = '' OR (
            LOWER(t.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
            LOWER(t.productCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
            LOWER(t.producer.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ))
        ORDER BY t.id DESC
""")
    List<Equipment> seearchEquipments (@Param("keyword") String keyword);

    Page<Equipment> findAllByOrderByIdDesc(Pageable pageable);
}
