package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.Customer;
import com.example.ServiceApp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value = """
    SELECT t.* FROM Customer t
    WHERE 
        (:keyword IS NULL OR :keyword = '' OR (
            LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
            LOWER(t.cui) LIKE LOWER(CONCAT('%', :keyword, '%')) OR  
            LOWER(t.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR  
            LOWER(t.telephone) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ))
""", nativeQuery = true)
    List<Customer> searchCustomer(@Param("keyword") String keyword);
}
