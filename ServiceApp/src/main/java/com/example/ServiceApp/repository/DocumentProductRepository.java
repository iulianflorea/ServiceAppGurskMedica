package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.DocumentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentProductRepository extends JpaRepository<DocumentProduct, Long> {

    List<DocumentProduct> findByDocumentDataIdOrderBySortOrderAsc(Long documentDataId);

    void deleteByDocumentDataId(Long documentDataId);

    @Modifying
    @Query("DELETE FROM DocumentProduct p WHERE p.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<Long> ids);
}
