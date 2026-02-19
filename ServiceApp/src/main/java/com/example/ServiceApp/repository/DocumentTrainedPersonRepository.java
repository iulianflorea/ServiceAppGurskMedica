package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.DocumentTrainedPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.List;

@EnableJpaRepositories
public interface DocumentTrainedPersonRepository extends JpaRepository<DocumentTrainedPerson, Long> {

    List<DocumentTrainedPerson> findByDocumentDataIdOrderBySortOrderAsc(Long documentDataId);

    void deleteByDocumentDataId(Long documentDataId);

    @Modifying
    @Query("DELETE FROM DocumentTrainedPerson e WHERE e.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<Long> ids);
}
