package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.InterventionSheet;
import com.example.ServiceApp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT t FROM Product t
        JOIN t.producer p
        WHERE 
            (:keyword IS NULL OR :keyword = '' OR (
                LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(t.cod) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                CAST(t.quantity AS string) LIKE CONCAT('%', :keyword, '%') OR 
                LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ))
    """)
    List<Product> searchProduct(@Param("keyword") String keyword);

    Optional<Product> findByCodIgnoreCase(String cod);



}
